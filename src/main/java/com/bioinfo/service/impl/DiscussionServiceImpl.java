package com.bioinfo.service.impl;

import com.bioinfo.entity.DiscussionComment;
import com.bioinfo.entity.DiscussionPost;
import com.bioinfo.mapper.DiscussionCommentMapper;
import com.bioinfo.mapper.DiscussionPostMapper;
import com.bioinfo.service.DiscussionService;
import com.bioinfo.utils.UserHolder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author 刘家雯
 * @Date 2025/5/17
 */
@Service
public class DiscussionServiceImpl implements DiscussionService {

    @Autowired
    private DiscussionPostMapper postMapper;

    @Autowired
    private DiscussionCommentMapper commentMapper;

    @Override
    public void createPost(DiscussionPost post) {
        Long userId = UserHolder.getUser().getId();
        String userName = UserHolder.getUser().getUserName();
        post.setUserId(userId);
        post.setUserName(userName);
        postMapper.insert(post);
    }

    @Override
    public List<DiscussionPost> getAllPosts() {
        return postMapper.selectAll();
    }

    @Override
    public DiscussionPost getPost(Long id) {
        return postMapper.selectById(id);
    }

    @Override
    public void addComment(DiscussionComment comment) {
        comment.setUserId(UserHolder.getUser().getId());
        comment.setUserName(UserHolder.getUser().getUserName());
        commentMapper.insert(comment);
        postMapper.incrementComments(comment.getPostId());
    }

    @Override
    public List<DiscussionComment> getCommentsByPost(Long postId) {
        return commentMapper.selectByPostId(postId);
    }

    @Override
    public void likePost(Long postId) {
        postMapper.incrementLike(postId);
    }

    @Override
    public void likeComment(Long commentId) {
        commentMapper.incrementLike(commentId);
    }
}
