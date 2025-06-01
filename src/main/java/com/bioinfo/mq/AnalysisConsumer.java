package com.bioinfo.mq;

import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson.JSON;
import com.bioinfo.analysis.AnalysisStrategy;
import com.bioinfo.analysis.AnalysisStrategyFactory;
import com.bioinfo.entity.AlphaDiversity;
import com.bioinfo.entity.Task;
import com.bioinfo.enumeration.AnalysisType;
import com.bioinfo.mapper.AlphaAnalysisMapper;
import com.bioinfo.mapper.TaskMapper;
import com.bioinfo.mq.messageEntity.EmailTaskMessage;
import com.bioinfo.thread.AnalysisThreadPool;
import com.bioinfo.utils.EasyExcelUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.spring.annotation.RocketMQMessageListener;
import org.apache.rocketmq.spring.core.RocketMQListener;
import org.apache.rocketmq.spring.core.RocketMQTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.math.BigDecimal;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author 刘家雯
 * @Date 2025/5/15
 *
 * MQ消费（分析任务）
 *
 * 根据taskId去数据库中找到文件，开始分析
 */
@Slf4j
@Component
@RocketMQMessageListener(topic = "analysis-task-topic", consumerGroup = "analysis-consumer-group")
public class AnalysisConsumer implements RocketMQListener<String> {

    @Autowired
    private TaskMapper taskMapper;

    @Autowired
    private AnalysisStrategyFactory strategyFactory;

    @Autowired
    private RocketMQTemplate rocketMQTemplate;

//    @Override
//    public void onMessage(String message) {
//
//        Task task = JSON.parseObject(message, Task.class);
//
//        // 开启子线程进行消费任务（分析开始）
//        AnalysisThreadPool.getRScriptThreadPool().submit(() -> {
//            log.debug("任务已成功提交到子线程");
//            try {
//                String filePath = task.getFilePath();
//                String fileName = task.getFileName();
//                if (StrUtil.isEmpty(filePath) || StrUtil.isEmpty(fileName)) {
//                    log.warn("文件路径或文件名为空！");
//                    return;
//                }
//
//                Path inputPath = Paths.get(filePath);
//                Path resultPath = getResultPath(task, task.getUserId());
//                Files.createDirectories(resultPath.getParent());
//
//                log.info("开始执行 R 脚本任务：{}", fileName);
//
//                // 调用 R 脚本
//                ProcessBuilder pb = new ProcessBuilder("Rscript", "scripts/alpha_analysis.R",
//                        inputPath.toString(), resultPath.toString());
//                pb.redirectErrorStream(true);
//                Process process = pb.start();
//
//                StringBuilder output = new StringBuilder();
//                try (BufferedReader reader = new BufferedReader(
//                        new InputStreamReader(process.getInputStream()))) {
//                    String line;
//                    while ((line = reader.readLine()) != null) {
//                        output.append(line).append("\n");
//                    }
//                }
//
//                int exitCode = process.waitFor();
//                if (exitCode != 0) {
//                    log.error("R 脚本执行失败：{}", output);
//                    return;
//                }
//
//                // 读取分析结果并入库(表alpha_diversity)
//                List<Map<String, String>> resultData;
//                try (InputStream resultStream = Files.newInputStream(resultPath)) {
//                    resultData = EasyExcelUtils.readDynamicExcel(resultStream);
//                }
//
//                List<AlphaDiversity> records = resultData.stream()
//                        .filter(row -> row != null && row.get("Sample") != null)
//                        .map(row -> {
//                            AlphaDiversity entity = new AlphaDiversity();
//                            entity.setTaskId(task.getTaskId());
//                            entity.setSampleName(row.get("Sample"));
//                            entity.setGroupName(null);
//                            entity.setShannon(new BigDecimal(row.get("Shannon")));
//                            entity.setSimpson(new BigDecimal(row.get("Simpson")));
//                            entity.setChao1(new BigDecimal(row.get("Chao1")));
//                            entity.setObservedSpecies(Integer.valueOf(row.get("Observed_Species")));
//                            entity.setGoodsCoverage(new BigDecimal(row.get("Goods_Coverage")));
//                            return entity;
//                        }).collect(Collectors.toList());
//
//                // 睡2min 模拟大文件处理很久
//                Thread.sleep(120000);
//                for (AlphaDiversity entity : records) {
//                    alphaAnalysisMapper.insert(entity);
//                }
//
//                // 在tasks表中添加结果路径（analysis_tasks表）
//                taskMapper.updateResult(resultPath.toString(), task.getTaskId());
//                taskMapper.updateTaskStatus(task.getTaskId(), 1); // 成功
//                log.info("Alpha 多样性分析任务完成，保存 {} 条记录。", records.size());
//
//                // 构建发送email任务
//                EmailTaskMessage emailTask = new EmailTaskMessage(task.getUserId(), resultPath.toString());
//                rocketMQTemplate.convertAndSend("email-task-topic", JSON.toJSONString(emailTask));
//                log.info("分析完成，邮件任务已投递");
//
//            } catch (Exception e) {
//                taskMapper.updateTaskStatus(task.getTaskId(), 2); // 失败
//                log.error("分析线程执行失败", e);
//            }
//
//        });
//    }

    @Override
    public void onMessage(String message) {
        Task task = JSON.parseObject(message, Task.class);

        AnalysisThreadPool.getRScriptThreadPool().submit(() -> {
            try {
                if (StrUtil.isEmpty(task.getFilePath()) || StrUtil.isEmpty(task.getFileName())) {
                    log.warn("文件路径或文件名为空！");
                    return;
                }

                AnalysisType type = AnalysisType.fromDesc(task.getTaskName());
                AnalysisStrategy strategy = strategyFactory.getStrategy(type);

                strategy.analyze(task);

                // 分析完成，投递邮件任务
                Path resultPath = Paths.get("results", "user_" + task.getUserId(),
                        task.getFileName().replace(".xlsx", "_" + type.name().toLowerCase() + "_result.xlsx"));
                EmailTaskMessage emailMsg = new EmailTaskMessage(task.getUserId(), resultPath.toString());
                rocketMQTemplate.convertAndSend("email-task-topic", JSON.toJSONString(emailMsg));
                log.info("分析完成，结果已发送邮件任务");

            } catch (Exception e) {
                taskMapper.updateTaskStatus(task.getTaskId(), 2);
                log.error("分析失败", e);
            }
        });
    }
}