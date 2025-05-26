package com.bioinfo.analysis;

import com.bioinfo.analysis.impl.AlphaAnalysisStrategy;
import com.bioinfo.analysis.impl.BetaAnalysisStrategy;
import com.bioinfo.enumeration.AnalysisType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * @author 刘家雯
 * @Date 2025/5/18
 *
 * 简单工厂类
 *
 */
@Component
public class AnalysisStrategyFactory {

    @Autowired
    private AlphaAnalysisStrategy alphaAnalysisStrategy;

    @Autowired
    private BetaAnalysisStrategy betaAnalysisStrategy;

    public AnalysisStrategy getStrategy(AnalysisType type) {
        switch (type) {
            case ALPHA:
                return alphaAnalysisStrategy;
            case BETA:
                return betaAnalysisStrategy;
            default:
                throw new IllegalArgumentException("不支持的分析类型：" + type);
        }
    }
}