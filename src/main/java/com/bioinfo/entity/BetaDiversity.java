package com.bioinfo.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * @author 刘家雯
 * @version 1.0
 * @Date 2025/4/18
 */
@Data
@Entity
@Table(name = "beta_result")
public class BetaDiversity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "task_id", nullable = false)
    private Long taskId;

    @Column(name = "created_time", updatable = false)
    private LocalDateTime createdTime = LocalDateTime.now();

    @Column(name = "updated_time")
    private LocalDateTime updatedTime;

    @Column(name = "sample_name", length = 100)
    private String sampleName;

    @Column(name = "pcoa1", precision = 10, scale = 4)
    private BigDecimal pcoa1;

    @Column(name = "pcoa2", precision = 10, scale = 4)
    private BigDecimal pcoa2;

}