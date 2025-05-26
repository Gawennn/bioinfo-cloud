package com.bioinfo.service;

import com.bioinfo.dto.Result;
import com.bioinfo.entity.Task;

import java.util.List;

/**
 * @author 刘家雯
 * @Date 2025/5/15
 */
public interface AlphaTaskService {
    Result resultQuery(Long taskId);

    List<Task> getTasksByUserId(Long userId);
}
