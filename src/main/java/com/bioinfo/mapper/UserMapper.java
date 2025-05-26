package com.bioinfo.mapper;

import com.bioinfo.entity.User;
import org.apache.ibatis.annotations.*;

/**
 * @author 刘家雯
 * @version 1.0
 * @Date 2025/4/16
 */
@Mapper
public interface UserMapper {

    /**
     * 根据号码查询用户
     * @param phone
     * @return
     */
    @Select("SELECT * from users where phone = #{phone}")
    User queryByPhone(@Param("phone") String phone);

    /**
     * 注册用户
     * @param user
     */
    @Insert("insert into users(username, password, phone, email) values (#{userName}, #{password}, #{phone}, #{email})")
    @Options(useGeneratedKeys = true, keyProperty = "userId")
    void register(User user);

    /**
     * 根据id修改用户密码
     * @param userId
     * @param password
     */
    @Update("update users set password = #{password} where user_id = #{userId}")
    void updateById(@Param("userId") Long userId, @Param("password") String password);

    /**
     * 用户充值
     * @param money
     * @param phone
     */
    @Update("update users set balance = balance + #{money} * 100 where phone = #{phone}")
    void deposit(@Param("money") Long money, @Param("phone") String phone);

    /**
     * 根据id查邮箱
     * @param userId
     * @return
     */
    @Select("select email from users where user_id = #{userId}")
    String getEmailById(@Param("userId") Long userId);

    /**
     * 扣减余额
     * @param money
     * @param userId
     */
    @Update("update users set balance = balance - #{money} * 100 where user_id = #{userId}")
    void consume(@Param("money") Long money, @Param("userId") Long userId);

    /**
     * 查看用户余额
     * @param userId
     * @return
     */
    @Select("select balance from users where user_id = #{userId}")
    Long getBalance(@Param("userId") Long userId);
}
