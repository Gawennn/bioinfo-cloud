-- 1.参数列表
-- 1.1券id
local voucherId = ARGV[1]
-- 1.2用户id
local userId = ARGV[2]
-- 1.3订单id
local orderId = ARGV[3]
-- 1.4券的title
local title = ARGV[4]
-- 1.5券的价格
local price = ARGV[5]

-- 2.数据key
-- 2.1库存key
local stockKey = 'seckill:stock:' .. voucherId
-- 2.2订单key
local orderKey = 'seckill:order:' .. voucherId

-- 3.脚本业务
-- 3.1获取库存（处理nil情况）
local stock = tonumber(redis.call('get', stockKey)) or 0
if (stock <= 0) then
    -- 3.2 库存不足，返回1
    return 1
end

-- 3.3判断用户是否下单
if (redis.call('sismember', orderKey, userId) == 1) then
    -- 3.4存在，说明是重复下单，返回2
    return 2
end

-- 3.4扣库存
redis.call('incrby', stockKey, -1)
-- 3.5下单（保存用户）
redis.call('sadd', orderKey, userId)
-- 3.6发送消息到队列中，XADD stream.orders * k1 v1 k2 v2 ...
redis.call('xadd', 'stream.orders', '*', 'userId', userId, 'voucherId', voucherId, 'id', orderId, 'title', title, 'price', price)
return 0