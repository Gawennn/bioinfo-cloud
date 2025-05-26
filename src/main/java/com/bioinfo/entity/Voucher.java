package com.bioinfo.entity;


import lombok.Data;

import java.time.LocalDateTime;

/**
 * @author 刘家雯
 * @Date 2025/5/17
 */
@Data
public class Voucher {

    private Long voucherId;
    private Integer stock;
    private LocalDateTime createTime;
    private LocalDateTime beginTime;
    private LocalDateTime endTime;
    private LocalDateTime updateTime;
    private String title;
    private Long payValue;

}
