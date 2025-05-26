package com.bioinfo.dto;

import com.bioinfo.entity.AlphaDiversity;
import com.bioinfo.entity.BetaDiversity;
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
public class BetaAnalysisDTO {

    private Long taskId;
    private String sampleName;
    private BigDecimal pcoa1;
    private BigDecimal pcoa2;

    // 从 BetaDiversity 实体类转换为 BetaAnalysisDTO
    public static BetaAnalysisDTO fromEntity(BetaDiversity entity) {
        return new BetaAnalysisDTO(
                entity.getTaskId(),
                entity.getSampleName(),
                entity.getPcoa1(),
                entity.getPcoa2()
        );
    }
}
