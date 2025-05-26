package com.bioinfo.analysis.impl;

import com.bioinfo.analysis.AnalysisStrategy;
import com.bioinfo.entity.AlphaDiversity;
import com.bioinfo.entity.Task;
import com.bioinfo.mapper.AlphaAnalysisMapper;
import com.bioinfo.mapper.TaskMapper;
import com.bioinfo.utils.EasyExcelUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.InputStream;
import java.math.BigDecimal;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author 刘家雯
 * @Date 2025/5/18
 */
@Component
public class AlphaAnalysisStrategy implements AnalysisStrategy {

    @Autowired
    private AlphaAnalysisMapper alphaAnalysisMapper;

    @Autowired
    private TaskMapper taskMapper;

    @Override
    public void analyze(Task task) throws Exception {
        Path inputPath = Paths.get(task.getFilePath());
        Path resultPath = getResultPath(task.getUserId(), task.getFileName());

        Files.createDirectories(resultPath.getParent());

        ProcessBuilder pb = new ProcessBuilder("Rscript", "scripts/alpha_analysis.R",
                inputPath.toString(), resultPath.toString());
        pb.redirectErrorStream(true);
        Process process = pb.start();
        process.waitFor();

        List<Map<String, String>> resultData;
        try (InputStream resultStream = Files.newInputStream(resultPath)) {
            resultData = EasyExcelUtils.readDynamicExcel(resultStream);
        }

        List<AlphaDiversity> records = resultData.stream()
                .filter(row -> row.get("Sample") != null)
                .map(row -> {
                    AlphaDiversity entity = new AlphaDiversity();
                    entity.setTaskId(task.getTaskId());
                    entity.setSampleName(row.get("Sample"));
                    entity.setGroupName(null);
                    entity.setShannon(new BigDecimal(row.get("Shannon")));
                    entity.setSimpson(new BigDecimal(row.get("Simpson")));
                    entity.setChao1(new BigDecimal(row.get("Chao1")));
                    entity.setObservedSpecies(Integer.valueOf(row.get("Observed_Species")));
                    entity.setGoodsCoverage(new BigDecimal(row.get("Goods_Coverage")));
                    return entity;
                }).collect(Collectors.toList());

        for (AlphaDiversity record : records) {
            alphaAnalysisMapper.insert(record);
        }

        taskMapper.updateResult(resultPath.toString(), task.getTaskId());
        taskMapper.updateTaskStatus(task.getTaskId(), 1);
    }

    private Path getResultPath(Long userId, String fileName) {
        String userDir = "user_" + userId;
        String resultFile = fileName.replace(".xlsx", "_alpha_result.xlsx");
        return Paths.get("results", userDir, resultFile);
    }
}
