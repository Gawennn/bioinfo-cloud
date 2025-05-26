package com.bioinfo.entity;

import lombok.Data;

/**
 * @author 刘家雯
 * @Date 2025/5/15
 */
@Data
public class FileEntity {
    private Long fileId;
    private Long userId;
    private String fileName;
    private String filePath;
}
