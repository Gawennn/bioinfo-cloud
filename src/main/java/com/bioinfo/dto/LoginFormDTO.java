package com.bioinfo.dto;

import lombok.Data;

/**
 * @author 刘家雯
 * @version 1.0
 * @Date 2025/4/16
 */
@Data
public class LoginFormDTO {
    private String phone;
    private String password;
    private String code;
}
