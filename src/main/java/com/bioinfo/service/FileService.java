package com.bioinfo.service;

import com.bioinfo.entity.FileEntity;
import com.bioinfo.dto.Result;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

/**
 * @author 刘家雯
 * @version 1.0
 * @Date 2025/4/16
 */
public interface FileService {
    Result upload(MultipartFile file) throws IOException;

    List<FileEntity> getFilesByUserId(Long userId);
}
