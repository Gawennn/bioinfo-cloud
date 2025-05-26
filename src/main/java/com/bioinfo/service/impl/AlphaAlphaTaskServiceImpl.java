package com.bioinfo.service.impl;

import com.bioinfo.dto.AlphaAnalysisDTO;
import com.bioinfo.entity.AlphaDiversity;
import com.bioinfo.dto.Result;
import com.bioinfo.entity.Task;
import com.bioinfo.mapper.AlphaAnalysisMapper;
import com.bioinfo.mapper.TaskMapper;
import com.bioinfo.service.AlphaTaskService;
import com.bioinfo.utils.UserHolder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

/**
 * @author 刘家雯
 * @Date 2025/5/15
 */
@Service
public class AlphaAlphaTaskServiceImpl implements AlphaTaskService {

    @Autowired
    private TaskMapper taskMapper;

    @Autowired
    private AlphaAnalysisMapper alphaAnalysisMapper;

    @Override
    public Result resultQuery(Long taskId) {

        Long userId = UserHolder.getUser().getId();
        Task task = taskMapper.findTaskById(taskId);

        if (task == null || !task.getUserId().equals(userId)) {
            return Result.fail("无权限或任务不存在");
        }

        if (task.getStatus() == 0) {
            return Result.ok("分析任务已提交，请稍后查看结果。");
        } else if (task.getStatus() == 2) {
            return Result.fail("分析失败！");
        }

        // 展示分析结果
        List<AlphaDiversity> results = alphaAnalysisMapper.selectByTaskId(taskId);
        List<AlphaAnalysisDTO> dtoList = results.stream()
                .map(AlphaAnalysisDTO::fromEntity)
                .collect(Collectors.toList());

        return Result.ok(dtoList);
    }

    @Override
    public List<Task> getTasksByUserId(Long userId) {
        return taskMapper.getTasksByUserId(userId);
    }
}
