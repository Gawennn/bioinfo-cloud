package com.bioinfo.entity;

import lombok.Data;

/**
 * @author 刘家雯
 * @Date 2025/5/17
 */
@Data
public class VoucherOrder {
    private Long id;
    private String title;
    private Long price;
    private Long userId;
    private Long voucherId;
}
