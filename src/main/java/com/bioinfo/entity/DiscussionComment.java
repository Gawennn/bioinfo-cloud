package com.bioinfo.entity;

import lombok.Data;

import java.time.LocalDateTime;

/**
 * @author 刘家雯
 * @Date 2025/5/18
 */
@Data
public class DiscussionComment {
    private Long id;
    private Long postId;
    private Long userId;
    private String userName;
    private String content;
    private Integer liked;
    private Integer status;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
}
