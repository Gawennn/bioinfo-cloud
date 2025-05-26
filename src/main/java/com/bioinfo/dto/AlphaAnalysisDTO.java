package com.bioinfo.dto;

import com.bioinfo.entity.AlphaDiversity;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.math.BigDecimal;

/**
 * @author 刘家雯
 * @version 1.0
 * @Date 2025/4/14
 */
@Data
@AllArgsConstructor
public class AlphaAnalysisDTO {

    private Long taskId;
    private String sampleName;
    private String groupName;
    private BigDecimal shannon;
    private BigDecimal simpson;
    private BigDecimal chao1;
    private Integer observedSpecies;
    private BigDecimal goodsCoverage;

    // 从 AlphaDiversity 实体类转换为 AlphaAnalysisDTO
    public static AlphaAnalysisDTO fromEntity(AlphaDiversity entity) {
        return new AlphaAnalysisDTO(
                entity.getTaskId(),
                entity.getSampleName(),
                entity.getGroupName(),
                entity.getShannon(),
                entity.getSimpson(),
                entity.getChao1(),
                entity.getObservedSpecies(),
                entity.getGoodsCoverage()
        );
    }
}
