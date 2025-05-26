package com.bioinfo.controller;

import com.bioinfo.dto.Result;
import com.bioinfo.service.AlphaAnalysisService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;

/**
 * @author 刘家雯
 * @version 1.0
 * @Date 2025/4/18
 */
@RestController
@RequestMapping("/analysis/alpha")
public class AlphaAnalysisController {

    @Autowired
    private AlphaAnalysisService alphaAnalysisService;

    /**
     * 前端传递fileId用于alpha多样性分析
     * @param fileId
     * @return
     */
    @PostMapping("/{fileId}")
    public Result alphaAnalysis(@PathVariable("fileId") Long fileId) {

        return (Result) alphaAnalysisService.analyze(fileId);
    }

    /**
     * 下载分析结果文件
     */
    @GetMapping("/download")
    public void download(@RequestParam Long taskId,
                         HttpServletResponse response) {
        alphaAnalysisService.downloadResult(taskId, response);
    }
}
