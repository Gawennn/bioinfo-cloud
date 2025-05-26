package com.bioinfo.service.impl;

import com.alibaba.fastjson.JSON;
import com.bioinfo.dto.Result;
import com.bioinfo.entity.FileEntity;
import com.bioinfo.entity.Task;
import com.bioinfo.mapper.FileMapper;
import com.bioinfo.mapper.ItemMapper;
import com.bioinfo.mapper.TaskMapper;
import com.bioinfo.mapper.UserMapper;
import com.bioinfo.service.AlphaAnalysisService;
import com.bioinfo.service.BetaAnalysisService;
import com.bioinfo.utils.SnowflakeIdGenerator;
import com.bioinfo.utils.UserHolder;
import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.spring.core.RocketMQTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.FileCopyUtils;

import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.URLEncoder;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

/**
 * @author 刘家雯
 * @Date 2025/4/18
 *
 * 只扔进MQ，不在这真正的处理
 *
 */
@Slf4j
@Service
public class BetaAnalysisServiceImpl implements BetaAnalysisService {

    @Autowired
    private FileMapper fileMapper;

    @Autowired
    private TaskMapper taskMapper;

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private ItemMapper itemMapper;

    @Autowired
    private RocketMQTemplate rocketMQTemplate;

    // 结果保存的路径
    private final String resultDir = "results"; //当前根路径下的 /results

    @Override
    public Object analyze(Long fileId) {

        try {
            // 获取用户ID
            Long userId = UserHolder.getUser().getId();

            // 验证fileId是否属于该用户
            FileEntity file = fileMapper.findfileById(fileId);

            if (file == null) {
                return Result.fail("文件不存在！");
            }

            // 检查文件的用户ID是否与当前用户ID匹配
            if (file.getUserId() == null || !file.getUserId().equals(userId)) {
                return Result.fail("无权操作此任务！");
            }

            // 新建任务
            Task task = new Task();
            SnowflakeIdGenerator idGenerator = new SnowflakeIdGenerator(1, 1);
            long taskId = idGenerator.nextId();
            task.setTaskId(taskId);
            task.setFileName(file.getFileName());
            task.setUserId(userId);
            task.setFilePath(file.getFilePath());
            task.setTaskName("beta多样性分析");


            Long price = itemMapper.getPriceByName("beta多样性分析");
            if (userMapper.getBalance(userId) < price) {
                return Result.fail("抱歉，您的余额已不足！");
            }
            // 将分析任务扔进MQ
            rocketMQTemplate.convertAndSend("analysis-task-topic", JSON.toJSONString(task));
            taskMapper.uploadTask(task);
            // 扣减用户余额
            userMapper.consume(price, userId);

            return Result.ok("分析任务已提交，请稍后查看结果。");

        } catch (Exception e) {
            log.error("分析任务提交失败", e);
            return Result.fail("任务提交失败: " + e.getMessage());
        }
    }

    @Override
    public void downloadResult(Long taskId, HttpServletResponse response) {
        try {
            // 获取用户ID
            Long userId = UserHolder.getUser().getId();

            // 查询 task
            Task task = taskMapper.findTaskById(taskId);
            if (task == null || !task.getUserId().equals(userId)) {
                Result.forbidden("无权下载该任务文件");
                return;
            }

            // 构建文件路径
            Path resultPath = getResultPath(task, userId);
            File file = resultPath.toFile();

            if (!file.exists()) {
                Result.notFound("文件不存在！");
                return;
            }

            // 复制一份文件到本地 （假装是用户下载）
            Path userHome = Paths.get(System.getProperty("user.home")); // /Users/xxx
            Path downloadsDir = userHome.resolve("Downloads");
            Files.copy(resultPath, downloadsDir.resolve(resultPath.getFileName()), StandardCopyOption.REPLACE_EXISTING);

            // 设置响应头
            String fileName = resultPath.getFileName().toString();
            response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
            response.setHeader("Content-Disposition", "attachment; filename=" + URLEncoder.encode(fileName, "UTF-8"));

            // 写出文件内容
            try (FileInputStream fis = new FileInputStream(file);
                 OutputStream os = response.getOutputStream()) {
                FileCopyUtils.copy(fis, os);
            }

        } catch (Exception e) {
            log.error("下载分析结果失败", e);
            response.setContentType("text/plain;charset=UTF-8");
            try {
                response.getWriter().write("下载分析结果失败: " + e.getMessage());
            } catch (IOException ex) {
                throw new RuntimeException(ex);
            }
            response.setStatus(500);
        }
    }

    private Path getResultPath (Task task, Long userId){
        String fileName = task.getFileName().replace(".xlsx", "_beta_result.xlsx");
        String userDir = "user_" + userId;
        return Paths.get(resultDir, userDir, fileName);
    }

}
