package com.bioinfo.analysis;

import com.bioinfo.entity.Task;

/**
 * @author 刘家雯
 * @Date 2025/5/18
 */
public interface AnalysisStrategy {
    void analyze(Task task) throws Exception;
}