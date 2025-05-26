package com.bioinfo.controller;

import com.bioinfo.dto.Result;
import com.bioinfo.service.BetaAnalysisService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;

/**
 * @author 刘家雯
 * @version 1.0
 * @Date 2025/4/18
 */
@RestController
@RequestMapping("/analysis/beta")
public class BetaAnalysisController {

    @Autowired
    private BetaAnalysisService betaAnalysisService;

    /**
     * 前端传递fileId用于beta多样性分析
     * @param fileId
     * @return
     */
    @PostMapping("/{fileId}")
    public Result betaAnalysis(@PathVariable("fileId") Long fileId) {

        return (Result) betaAnalysisService.analyze(fileId);
    }

    /**
     * 下载分析结果文件
     */
    @GetMapping("/download")
    public void download(@RequestParam Long taskId,
                         HttpServletResponse response) {
        betaAnalysisService.downloadResult(taskId, response);
    }
}
