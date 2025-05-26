package com.bioinfo.service;

import javax.servlet.http.HttpServletResponse;

/**
 * @author 刘家雯
 * @version 1.0
 * @Date 2025/4/18
 */
public interface BetaAnalysisService {

    Object analyze(Long fileId);

    void downloadResult(Long taskId, HttpServletResponse response);
}
