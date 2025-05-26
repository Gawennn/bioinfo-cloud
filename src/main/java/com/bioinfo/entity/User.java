package com.bioinfo.entity;

import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * @author 刘家雯
 * @version 1.0
 * @Date 2025/4/16
 */
@Data
public class User implements Serializable {

    private Long userId;

    private String userName;

    private String password;

    private String phone;

    private String email;

    private LocalDateTime createTime;

    private LocalDateTime updateTime;

    private Integer balance;
}
