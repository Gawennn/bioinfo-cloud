package com.bioinfo.entity;

import lombok.Data;

import java.time.LocalDateTime;

/**
 * @author 刘家雯
 * @Date 2025/5/17
 */
@Data
public class Item {
    private Long id;
    private String name;
    private Long price;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
}
