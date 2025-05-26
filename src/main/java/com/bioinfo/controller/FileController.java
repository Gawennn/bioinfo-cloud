package com.bioinfo.controller;

import com.bioinfo.entity.FileEntity;
import com.bioinfo.dto.Result;
import com.bioinfo.service.FileService;
import com.bioinfo.utils.UserHolder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

/**
 * @author 刘家雯
 * @version 1.0
 * @Date 2025/4/16
 */
@RestController
@RequestMapping("/file")
public class FileController {

    @Autowired
    private FileService fileService;

    /**
     * 文件上传
     * @param file
     * @return
     * @throws IOException
     */
    @PostMapping("/upload")
    public Result fileUpload(@RequestParam("file") MultipartFile file) throws IOException {

        Result upload = fileService.upload(file);
        return Result.ok(upload);
    }

    /**
     * 获取所有文件列表
     * @return
     */
    @GetMapping("/query")
    public Result queryFileList() {
        // 获取用户ID
        Long userId = UserHolder.getUser().getId();

        List<FileEntity> files = fileService.getFilesByUserId(userId);
        return Result.ok(files);
    }
}
