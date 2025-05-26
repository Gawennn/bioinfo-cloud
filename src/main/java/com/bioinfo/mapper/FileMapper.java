package com.bioinfo.mapper;

import com.bioinfo.entity.FileEntity;
import com.bioinfo.entity.Task;
import org.apache.ibatis.annotations.*;

import java.util.List;
import java.util.Optional;

/**
 * @author 刘家雯
 * @version 1.0
 * @Date 2025/4/17
 */
@Mapper
public interface FileMapper {

    // 文件上传
    @Insert("insert into files (file_id, file_name, user_id, file_path)" +
            "values (#{fileId}, #{fileName}, #{userId}, #{filePath})")
    void upload(FileEntity file);

    // 根据用户ID获取所有历史文件列表
    @Select("select file_id, file_name from files where user_id = #{userId} order by create_time desc")
    List<FileEntity> getFilesByUserId(Long userId);

    // 通过fileId找文件
    @Select("select user_id, file_id, file_name, file_path " +
            "from files where file_id = #{fileId}")
    FileEntity findfileById(Long fileId);
}
