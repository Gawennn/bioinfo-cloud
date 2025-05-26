package com.bioinfo.dto;

import lombok.Data;

/**
 * @author 刘家雯
 * @version 1.0
 * @Date 2025/4/16
 */
@Data
public class UserDTO {
    private Long id;
    private String userName;
    private String password;
    private String phone;
    private String email;
    private Integer balance;
}
