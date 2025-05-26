package com.bioinfo.mapper;

import com.bioinfo.entity.Task;
import org.apache.ibatis.annotations.*;

import java.nio.file.Path;
import java.util.List;

/**
 * @author 刘家雯
 * @Date 2025/5/15
 */
@Mapper
public interface TaskMapper {

    // 根据用户ID获取所有任务
    @Select("select task_name, task_id, file_name, " +
            "status from analysis_tasks " +
            "where user_id = #{userId} order by create_time desc")
    List<Task> getTasksByUserId(@Param("taskId") Long userId);

    // 通过taskId找任务
    @Select("select task_name, user_id, task_id, file_name, file_path," +
            "status " +
            "from analysis_tasks where task_id = #{taskId}")
    Task findTaskById(@Param("taskId") Long taskId);

    // 更改任务状态
    @Update("update analysis_tasks set status = #{status} where task_id = #{taskId}")
    void updateTaskStatus(@Param("taskId") Long taskId, @Param("status") int status);

    // 新建任务
    @Insert("insert into analysis_tasks (task_id, task_name, user_id, file_name, file_path)" +
            "values (#{taskId}, #{taskName}, #{userId}, #{fileName}, #{filePath})")
    @Options(useGeneratedKeys = true, keyProperty = "taskId")
    void uploadTask(Task task);

    // 获取结果文件路径
    @Select("select result_path from analysis_tasks where task_id = #{taskId}")
    String getAnalysisResultFilePathByTaskId(@Param("taskId") Long taskId);

    // 增加结果文件路径
    @Update("update analysis_tasks set result_path = (#{resultPath}) where task_id = #{taskId}")
    void updateResult(@Param("resultPath") String resultPath, @Param("taskId") Long taskId);
}
