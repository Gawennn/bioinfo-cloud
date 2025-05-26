package com.bioinfo.service;

import com.bioinfo.entity.DiscussionComment;
import com.bioinfo.entity.DiscussionPost;

import java.util.List;

/**
 * @author 刘家雯
 * @Date 2025/5/18
 */
public interface DiscussionService {
    void createPost(DiscussionPost post);
    List<DiscussionPost> getAllPosts();
    DiscussionPost getPost(Long id);

    void addComment(DiscussionComment comment);
    List<DiscussionComment> getCommentsByPost(Long postId);

    void likePost(Long postId);
    void likeComment(Long commentId);
}
