package com.bioinfo.dto;

import lombok.Data;

import java.time.LocalDateTime;


/**
 * @author 刘家雯
 * @Date 2025/5/17
 */
@Data
public class VoucherDTO {

    private Long voucherId;
    private Integer stock;
    private String title;
    private Long payValue;
    private LocalDateTime beginTime;
    private LocalDateTime endTime;
}
