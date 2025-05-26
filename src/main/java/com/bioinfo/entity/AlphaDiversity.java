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
@Table(name = "alpha_diversity")
public class AlphaDiversity {

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

    @Column(name = "group_name", length = 50)
    private String groupName;

    @Column(name = "shannon", precision = 10, scale = 4)
    private BigDecimal shannon;

    @Column(name = "simpson", precision = 10, scale = 4)
    private BigDecimal simpson;

    @Column(name = "chao1", precision = 10, scale = 2)
    private BigDecimal chao1;

    @Column(name = "observed_species")
    private Integer observedSpecies;

    @Column(name = "goods_coverage", precision = 10, scale = 4)
    private BigDecimal goodsCoverage;

}