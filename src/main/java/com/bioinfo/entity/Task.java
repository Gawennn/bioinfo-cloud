package com.bioinfo.entity;

import lombok.Data;

/**
 * @author 刘家雯
 * @version 1.0
 * @Date 2025/4/17
 */
@Data
public class Task {

    private Long taskId;
    private Long userId;
    private String fileName;
    private String taskName;
    private String filePath;
    private String resultPath;
    private int status;

}
