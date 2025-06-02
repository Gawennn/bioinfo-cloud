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