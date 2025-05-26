package com.bioinfo.service.impl;

import com.bioinfo.entity.FileEntity;
import com.bioinfo.dto.Result;
import com.bioinfo.mapper.FileMapper;
import com.bioinfo.service.FileService;
import com.bioinfo.utils.SnowflakeIdGenerator;
import com.bioinfo.utils.UserHolder;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

/**
 * @author 刘家雯
 * @version 1.0
 * @Date 2025/4/16
 */
@Slf4j
@Service
public class FileServiceImpl implements FileService {

    @Autowired
    private FileMapper fileMapper;

    @Override
    public Result upload(MultipartFile file) {

        try {
            // 获取用户ID
            Long userId = UserHolder.getUser().getId();
            log.debug("userId为：{}", userId);

            // 验证文件
            if (file.isEmpty()) {
                return Result.fail(400, "文件不能为空");
            }

            // 创建上传目录
            String userDir = "user_" + userId;
            String uploadDir = System.getProperty("user.dir") + "/uploads/" + userDir;
            Path uploadPath = Paths.get(uploadDir);
            if (!Files.exists(uploadPath)) {
                Files.createDirectories(uploadPath);
            }
            String originalFilename = file.getOriginalFilename();
            String fullFilePath = uploadDir + "/" + originalFilename; // 获取完整路径
            Path fullPath = Paths.get(fullFilePath);

            // 保存文件到本地
            file.transferTo(fullPath.toFile());

            FileEntity fileEntity = new FileEntity();
            SnowflakeIdGenerator idGenerator = new SnowflakeIdGenerator(1, 1);
            long fileId = idGenerator.nextId();
            fileEntity.setFileId(fileId);
            fileEntity.setFileName(originalFilename);
            fileEntity.setUserId(userId);
            fileEntity.setFilePath(fullFilePath);

            fileMapper.upload(fileEntity);

            log.debug("文件 {} 上传成功！", fileEntity);
            return Result.ok("文件上传成功", fileEntity);
        } catch (IOException e) {
            log.error("文件上传失败！", e);
            return Result.error("文件上传失败：" + e.getMessage());
        }
    }

    @Override
    public List<FileEntity> getFilesByUserId(Long userId) {
        return fileMapper.getFilesByUserId(userId);
    }
}
