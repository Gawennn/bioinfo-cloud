package com.bioinfo.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.bean.copier.CopyOptions;
import cn.hutool.core.util.RandomUtil;
import cn.hutool.core.util.StrUtil;
import com.bioinfo.dto.ChangePasswordDTO;
import com.bioinfo.dto.ForgetPasswordDTO;
import com.bioinfo.dto.LoginFormDTO;
import com.bioinfo.dto.UserDTO;
import com.bioinfo.dto.Result;
import com.bioinfo.entity.User;
import com.bioinfo.mapper.UserMapper;
import com.bioinfo.service.UserService;
import com.bioinfo.utils.RegexUtils;
import com.bioinfo.utils.UserHolder;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.DigestUtils;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

/**
 * @author 刘家雯
 * @version 1.0
 * @Date 2025/4/16
 */
@Slf4j
@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    @Autowired
    private UserMapper userMapper;

    @Override
    public Result sendCode(String phone) {
        // 校验手机号
        if (RegexUtils.isPhoneInvalid(phone)) {
            // 如果不符合，返回错误信息
            return Result.fail("手机号格式有误！");
        }

        // 符合，生成验证码
        String code = RandomUtil.randomNumbers(6);

        // 保存验证码到redis  // == set key value ex 120
        stringRedisTemplate.opsForValue().set("login:code:" + phone, code, 2, TimeUnit.MINUTES);

        // 发送验证码
        log.debug("发送短信验证码成功，验证码：{}", code);

        // 返回OK
        return Result.ok("发送短信验证码成功");
    }

    @Override
    public Result login(LoginFormDTO loginFormDTO) {

        String phone = loginFormDTO.getPhone();

        // 校验手机号
        if (RegexUtils.isPhoneInvalid(phone)) {
            return Result.fail("手机号错误！");
        }

        // 根据手机号码去数据库查询用户
        User user = userMapper.queryByPhone(phone);
        if (user == null) {
            // 若用户不存在，则返回错误
            return Result.fail("用户名不存在！");
        }

        // 判断登录方式
        if (StrUtil.isNotBlank(loginFormDTO.getPassword())) {
            // 密码登录逻辑
            String encryptedPwd = DigestUtils.md5DigestAsHex(loginFormDTO.getPassword().getBytes());
            if (!encryptedPwd.equals(user.getPassword())) {
                return Result.fail("密码错误！");
            }
        } else {
            // 验证码登录逻辑
            String cacheCode = stringRedisTemplate.opsForValue().get("login:code:" + phone);
            if (StrUtil.isBlank(loginFormDTO.getCode()) || !loginFormDTO.getCode().equals(cacheCode)) {
                return Result.fail("验证码错误！");
            }
        }

        // 保存用户信息到Redis中
        // 随机生成token，作为令牌
        String token = UUID.randomUUID().toString();
        // 将user对象转为hash存储
        UserDTO userDTO = BeanUtil.copyProperties(user, UserDTO.class);
        userDTO.setId(user.getUserId());

        Map<String, Object> userMap = BeanUtil.beanToMap(
                userDTO,
                new HashMap<>(),
                CopyOptions.create().setIgnoreNullValue(true)
                        .setFieldValueEditor((fieldName, fieldValue) -> fieldValue == null ? "" : fieldValue.toString())
        );

        String tokenKey = "login:token:" + token;
        stringRedisTemplate.opsForHash().putAll(tokenKey, userMap);
        // 设置token有效期
        stringRedisTemplate.expire(tokenKey, 600L, TimeUnit.MINUTES);

        return Result.ok("登陆成功！");
    }

    @Override
    public Boolean register(UserDTO userDTO) {

        // 判断该手机号是否被注册
        if (userMapper.queryByPhone(userDTO.getPhone()) != null) {
            return false;
        }

        User user = new User();
        BeanUtil.copyProperties(userDTO, user);

        // 密码加密
        String s = DigestUtils.md5DigestAsHex(userDTO.getPassword().getBytes());
        user.setPassword(s);

        userMapper.register(user);
        log.debug("用户注册成功！");

        return true;
    }

    @Override
    public Result changePassword(ChangePasswordDTO changePasswordDTO) {

        UserDTO userdto = UserHolder.getUser();

        // 对旧密码进行 MD5 加密后比对
        String oldPwdMd5 = DigestUtils.md5DigestAsHex(changePasswordDTO.getOldPassword().getBytes());
        if (!oldPwdMd5.equals(userdto.getPassword())) {
            return Result.fail("旧密码不正确！");
        }

        // 对新密码加密并比对是否与旧密码相同
        String newPwdMd5 = DigestUtils.md5DigestAsHex(changePasswordDTO.getNewPassword().getBytes());
        if (newPwdMd5.equals(userdto.getPassword())) {
            return Result.fail("新密码不能与旧密码一致！");
        }

        // 更新密码
        userMapper.updateById(userdto.getId(), newPwdMd5);

        return Result.ok("密码修改成功！");
    }

    @Override
    public Result forgetPassword(ForgetPasswordDTO dto) {

        // 参数校验
        if (dto.getPhone() == null || dto.getCode() == null || dto.getNewPassword() == null) {
            return Result.fail("参数不能为空");
        }

        // 校验验证码
        String code = RandomUtil.randomNumbers(6);
        stringRedisTemplate.opsForValue().set("forget-password:code:" + dto.getPhone(), code, 2, TimeUnit.MINUTES);
        String cacheCode = stringRedisTemplate.opsForValue().get("forget-password:code:" + dto.getPhone());
        if (cacheCode == null || !cacheCode.equals(dto.getCode())) {
            return Result.fail("验证码错误！");
        }

        // 根据手机号查用户
        User user = userMapper.queryByPhone(dto.getPhone());
        if (user == null) {
            return Result.fail("该手机号尚未注册！");
        }

        // 修改密码（加密后保存）
        String newPwdMd5 = DigestUtils.md5DigestAsHex(dto.getNewPassword().getBytes());
        user.setPassword(newPwdMd5);
        // 重置密码
        userMapper.updateById(user.getUserId(), user.getPassword());

        return Result.ok("密码重置成功！");
    }

    @Override
    public Result deposit(Long money, String phone) {
        String phone1 = UserHolder.getUser().getPhone();
        if(!phone1.equals(phone)) {
            return Result.fail("手机号不正确！");
        }
        userMapper.deposit(money, phone);
        return Result.ok("号码为" + phone + "的用户成功充值" + money + "元！");
    }
}
