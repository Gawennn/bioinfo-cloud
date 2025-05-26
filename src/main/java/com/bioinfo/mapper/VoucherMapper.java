package com.bioinfo.mapper;

import com.bioinfo.entity.Voucher;
import com.bioinfo.entity.VoucherOrder;
import org.apache.ibatis.annotations.*;

/**
 * @author 刘家雯
 * @Date 2025/5/17
 */
@Mapper
public interface VoucherMapper {

    // 新增一种优惠券
    @Insert("insert into voucher(voucher_id, stock, title, pay_value, begin_time, end_time) " +
            "values(#{voucherId}, #{stock}, #{title}, #{payValue}, #{beginTime}, #{endTime})")
    boolean addVoucher(Voucher voucher);

    // 得到优惠券价格
    @Select("select (pay_value / 100) from voucher where voucher_id = #{voucherId}")
    Long queryPrice(@Param("voucherId") Long voucherId);

    // 得到优惠券title
    @Select("select title from voucher where voucher_id = #{voucherId} and stock > 0")
    String queryTitle(@Param("voucherId") Long voucherId);

    // 判断用户是否对同一种券重复下单
    @Select("select count(*) from voucher_order where user_id = #{userId} and voucher_id = #{voucherId}")
    int countByUserIdAndVoucherId(@Param("userId") Long userId, @Param("voucherId") Long voucherId);

    // 扣减库存
    @Update("update voucher set stock = stock - 1 where voucher_id = #{voucherId}")
    boolean reduceStock(@Param("voucherId") Long voucherId);

    // 保存购买券的订单
    @Insert("insert into voucher_order (id, title, price, user_id, voucher_id) " +
            "values (#{id}, #{title}, #{price}, #{userId}, #{voucherId})")
    void insertOrder(VoucherOrder voucherOrder);
}
