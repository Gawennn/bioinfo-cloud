package com.bioinfo.dto;

import lombok.Data;

/**
 * @author 刘家雯
 * @version 1.0
 * @Date 2025/4/23
 */
@Data
public class ForgetPasswordDTO {
    private String phone;
    private String code;
    private String newPassword;
}
