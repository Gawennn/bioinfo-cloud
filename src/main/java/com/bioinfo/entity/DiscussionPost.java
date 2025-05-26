package com.bioinfo.entity;

import lombok.Data;

import java.time.LocalDateTime;

/**
 * @author 刘家雯
 * @Date 2025/5/17
 */
@Data
public class DiscussionPost {
    private Long id;
    private Long userId;
    private String title;
    private String content;
    private Integer liked;
    private String userName;
    private Integer status;
    private Integer comments; // 评论数量
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
}

