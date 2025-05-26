package com.bioinfo.service;

import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * @author 刘家雯
 * @version 1.0
 * @Date 2025/4/18
 */
public interface AlphaAnalysisService {

    Object analyze(Long fileId);

    void downloadResult(Long taskId, HttpServletResponse response);
}
