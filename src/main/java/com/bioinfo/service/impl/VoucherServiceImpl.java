package com.bioinfo.service.impl;

import cn.hutool.core.bean.BeanUtil;
import com.bioinfo.dto.VoucherDTO;
import com.bioinfo.dto.Result;
import com.bioinfo.entity.Voucher;
import com.bioinfo.entity.VoucherOrder;
import com.bioinfo.mapper.VoucherMapper;
import com.bioinfo.service.VoucherService;
import com.bioinfo.utils.SnowflakeIdGenerator;
import com.bioinfo.utils.UserHolder;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.aop.framework.AopContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.data.redis.connection.stream.*;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.PostConstruct;
import java.time.Duration;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * @author 刘家雯
 * @Date 2025/5/17
 *
 *
 * 用户点击秒杀券  | seckillVoucher(id) |
 *                +---------------------+
 *                         |
 *                     Lua脚本执行（Redis原子操作）
 *                     | - 判断是否重复下单
 *                     | - 判断库存
 *                     | - 写入Redis Stream（消息队列）
 *                         |
 *                +-------------------------+
 *                | Redis Stream（订单消息） |
 *                +-------------------------+
 *                         |
 *       +-----------------+------------------+
 *       |                                   |
 * 线程池从队列消费                      pending list处理异常消息
 *       |                                   |
 * RLock 加锁防止重复下单                 RLock 加锁防止重复下单
 *       |                                   |
 * 调用代理对象 proxy.createVoucherOrder(...) （事务）
 */
@Slf4j
@Service
public class VoucherServiceImpl implements VoucherService {

    @Autowired
    private VoucherMapper voucherMapper;

    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    @Autowired
    private RedissonClient redissonClient;

    private static final DefaultRedisScript<Long> SECKILL_SCRIPT;
    static {
        SECKILL_SCRIPT = new DefaultRedisScript<>();
        SECKILL_SCRIPT.setLocation(new ClassPathResource("seckill.lua"));
        SECKILL_SCRIPT.setResultType(Long.class);
    }

    // 线程池创建子线程，处理订单
    private static final ExecutorService SECKILL_ORDER_EXECUTOR = Executors.newSingleThreadExecutor(r -> new Thread(r, "order-handler"));

        @PostConstruct // 表示在该类一初始化好就开始执行
    private void init() {
        SECKILL_ORDER_EXECUTOR.submit(new VoucherOrderHandler());
    }


    @Override
    @Transactional
    public Result addVoucher(VoucherDTO voucherDTO) {

        // 生成voucherId
        SnowflakeIdGenerator idGenerator = new SnowflakeIdGenerator(2, 1);
        long voucherId = idGenerator.nextId();
        voucherDTO.setVoucherId(voucherId);

        Voucher voucher = new Voucher();
        BeanUtil.copyProperties(voucherDTO, voucher);
        boolean isAdd = voucherMapper.addVoucher(voucher);
        if (isAdd) {
            // 存进redis
            stringRedisTemplate.opsForValue().set("seckill:stock:" + voucher.getVoucherId(), voucher.getStock().toString());
            return Result.ok("抵扣券添加成功！", voucher.getVoucherId());
        }
        return Result.fail("抵扣券添加失败！");
    }

    private VoucherService proxy;

    // 用户秒杀代金券
    @Override
    public Result seckillVoucher(Long voucherId) {

        Long userId = UserHolder.getUser().getId();

        // 先判断用户余额是否充足（先进行一次过滤）
        Integer balance = UserHolder.getUser().getBalance();
        if (balance < voucherMapper.queryPrice(voucherId)) {
            return Result.fail("您的账户余额不足！");
        }

        // 订单id
        SnowflakeIdGenerator idGenerator = new SnowflakeIdGenerator(2, 1);
        long orderId = idGenerator.nextId();

        // 券的title
        String title = voucherMapper.queryTitle(voucherId);

        // 执行lua脚本（判断是否有购买资格，创建订单，扔到阻塞队列）
        Long result = stringRedisTemplate.execute(
                SECKILL_SCRIPT,
                Collections.emptyList(),
                voucherId.toString(),
                userId.toString(),
                String.valueOf(orderId),
                title,
                voucherMapper.queryPrice(voucherId).toString()
        );

        // 判断结果是否为0
        int r = result.intValue();
        if (r != 0) {
            // 不为0，代表无购买资格
            return Result.fail(r == 1 ? "库存不足" : "不能重复下单");
        }
        // 获取代理对象（为了调用 @Transactional 注解的方法，因为Spring事务是由代理机制实现的）
        proxy = (VoucherService) AopContext.currentProxy();

        // 返回id
        return Result.ok("userId = " + userId);
    }


    private class VoucherOrderHandler implements Runnable {
        String queueName = "stream.orders";

        @Override
        public void run() {
            while (true) {
                try {
                    // 获取消息队列中的订单信息 XREADGROUP GROUP g1 c1 COUNT 1 BLOCK 2000 STREAMS streams.order >
                    List<MapRecord<String, Object, Object>> list = stringRedisTemplate.opsForStream().read(
                            Consumer.from("g1", "c1"),
                            StreamReadOptions.empty().count(1).block(Duration.ofSeconds(2)),
                            StreamOffset.create(queueName, ReadOffset.lastConsumed())
                    );
                    // 判断消息获取是否成功
                    if (list == null || list.isEmpty()) {
                        // 如果获取失败，说明没有消息，继续下一次循环
                        continue;
                    }
                    // 解析消息中的订单
                    MapRecord<String, Object, Object> record = list.get(0);
                    Map<Object, Object> values = record.getValue();
                    VoucherOrder voucherOrder = BeanUtil.fillBeanWithMap(values, new VoucherOrder(), true);
                    // 如果获取成功，可以下单
                    handleVoucherOrder(voucherOrder);
                    // ACK确认 SACK stream.orders g1 id
                    stringRedisTemplate.opsForStream().acknowledge(queueName, "g1", record.getId());

                } catch (Exception e) {
                    log.error("处理订单异常", e);
                    // 处理异常订单
                    handlePendingList();
                }
            }
        }

        // 出现异常的消息放入 pending-list
        private void handlePendingList() {
            while (true) {
                try {
                    // 获取 pending-list 的订单信息 XREADGROUP GROUP g1 c1 COUNT 1 BLOCK 2000 STREAMS streams.order >
                    List<MapRecord<String, Object, Object>> list = stringRedisTemplate.opsForStream().read(
                            Consumer.from("g1", "c1"),
                            StreamReadOptions.empty().count(1),
                            StreamOffset.create(queueName, ReadOffset.from("0"))
                    );
                    // 判断消息获取是否成功
                    if (list == null || list.isEmpty()) {
                        // 如果获取失败，说明没有消息，结束循环
                        break;
                    }
                    // 解析消息中的订单
                    MapRecord<String, Object, Object> record = list.get(0);
                    Map<Object, Object> values = record.getValue();
                    VoucherOrder voucherOrder = BeanUtil.fillBeanWithMap(values, new VoucherOrder(), true);
                    // 如果获取成功，可以下单
                    handleVoucherOrder(voucherOrder);
                    // ACK确认 SACK stream.orders g1 id
                    stringRedisTemplate.opsForStream().acknowledge(queueName, "g1", record.getId());
                } catch (Exception e) {
                    log.error("处理pending-list订单异常", e);
                    try {
                        Thread.sleep(20);
                    } catch (InterruptedException ex) {
                        throw new RuntimeException(ex);
                    }
                }
            }
        }
    }

    // 这个方法就是子线程处理了
    private void handleVoucherOrder(VoucherOrder voucherOrder) {
        // 获取用户
        Long userId = voucherOrder.getUserId();
        // 创建Redisson锁对象
        RLock lock = redissonClient.getLock("lock:order:" + userId);
        // 获取锁
        boolean isLock = lock.tryLock();
        // 判断是否获取成功
        if (!isLock) {
            // 获取锁失败
            log.error("不允许重复下单");
            return;
        }
        try {
            proxy.createVoucherOrder(voucherOrder);
        } finally {
            // 释放锁
            lock.unlock();
        }
    }

    // 真正创建订单的逻辑
    @Transactional
    public void createVoucherOrder(VoucherOrder voucherOrder) {

        // 一人一单
        Long userId = voucherOrder.getUserId();

        // 查询订单
        int count = voucherMapper.countByUserIdAndVoucherId(userId, voucherOrder.getVoucherId());
        // 再判断一下是否存在
        if (count > 0) {
            // 用户已经购买过了
            return;
        }

        // 扣减库存
        boolean success = voucherMapper.reduceStock(voucherOrder.getVoucherId());
        if (!success) {
            log.error("库存不足！");
            return;
        }

        //7.创建订单
        voucherMapper.insertOrder(voucherOrder);
    }
}
