package com.bioinfo.controller;

import com.bioinfo.dto.Result;
import com.bioinfo.entity.Task;
import com.bioinfo.service.AlphaTaskService;
import com.bioinfo.utils.UserHolder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * @author 刘家雯
 * @Date 2025/5/15
 */
@RestController("/tasks")
public class TaskController {

    @Autowired
    private AlphaTaskService alphaTaskService;

    /**
     * 用户查询任务处理结果
     * @param taskId
     * @return
     */
    @GetMapping("/taskid/{taskId}")
    public Result resultQuery(@PathVariable Long taskId) {

        return alphaTaskService.resultQuery(taskId);
    }

    /**
     * 获取用户所有任务的列表
     * @return
     */
    @GetMapping("/list")
    public Result tasksQuery() {

        // 获取用户ID
        Long userId = UserHolder.getUser().getId();

        List<Task> tasks = alphaTaskService.getTasksByUserId(userId);
        return Result.ok(tasks); // 返回包含taskId的任务列表
    }
}
