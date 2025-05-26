package com.bioinfo.controller;

import com.bioinfo.dto.ChangePasswordDTO;
import com.bioinfo.dto.ForgetPasswordDTO;
import com.bioinfo.dto.LoginFormDTO;
import com.bioinfo.dto.UserDTO;
import com.bioinfo.dto.Result;
import com.bioinfo.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * @author 刘家雯
 * @version 1.0
 * @Date 2025/4/16
 */
@Slf4j
@RestController
@RequestMapping("/user")
public class UserController {

    @Autowired
    private UserService userService;

    /**
     * 发送验证码
     * @param phone
     * @return
     */
    @PostMapping("/code")
    public Result sendCode(@RequestParam("phone") String phone) {
        return userService.sendCode(phone);
    }

    /**
     * 用户登陆
     * @param loginFormDTO
     * @return
     */
    @PostMapping("/login")
    public Result login(@RequestBody LoginFormDTO loginFormDTO) {
        return userService.login(loginFormDTO);
    }

    /**
     * 用户注册
     * @param userDTO
     * @return
     */
    @PostMapping("/register")
    public Result register(@RequestBody UserDTO userDTO) {
        return userService.register(userDTO)
                ? Result.ok("注册成功！") : Result.fail("手机号已被注册！");
    }

    /**
     * 修改密码
     * @param changePasswordDTO
     * @return
     */
    @PostMapping("/change-password")
    public Result changePassword(@RequestBody ChangePasswordDTO changePasswordDTO) {
        return userService.changePassword(changePasswordDTO);
    }

    /**
     * 忘记密码，通过电话验证码找回
     * @param forgetPasswordDTO
     * @return
     */
    @PostMapping("/forget")
    public Result forgetPassword(@RequestBody ForgetPasswordDTO forgetPasswordDTO) {
        return userService.forgetPassword(forgetPasswordDTO);
    }

    /**
     * 用户充值
     * @param money
     * @return
     */
    @PostMapping("/deposit")
    public Result deposit(@RequestParam Long money, String phone) {
        return userService.deposit(money, phone);
    }
}
