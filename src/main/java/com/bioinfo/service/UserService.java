package com.bioinfo.service;

import com.bioinfo.dto.ChangePasswordDTO;
import com.bioinfo.dto.ForgetPasswordDTO;
import com.bioinfo.dto.LoginFormDTO;
import com.bioinfo.dto.UserDTO;
import com.bioinfo.dto.Result;


/**
 * @author 刘家雯
 * @version 1.0
 * @Date 2025/4/16
 */
public interface UserService {
    Result sendCode(String phone);

    Result login(LoginFormDTO loginFormDTO);

    Boolean register(UserDTO userDTO);

    Result changePassword(ChangePasswordDTO changePasswordDTO);

    Result forgetPassword(ForgetPasswordDTO forgetPasswordDTO);

    Result deposit(Long money, String phone);
}
