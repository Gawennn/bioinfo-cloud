package com.bioinfo.controller;

import com.bioinfo.dto.Result;
import com.bioinfo.entity.DiscussionComment;
import com.bioinfo.entity.DiscussionPost;
import com.bioinfo.service.DiscussionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @author 刘家雯
 * @Date 2025/5/17
 */
@RestController
@RequestMapping("/discussion")
public class DiscussionController {

    @Autowired
    private DiscussionService discussionService;

    @PostMapping("/post")
    public Result<?> createPost(@RequestBody DiscussionPost post) {
        discussionService.createPost(post);
        return Result.ok("发帖成功");
    }

    @GetMapping("/posts")
    public Result<List<DiscussionPost>> getAllPosts() {
        return Result.ok(discussionService.getAllPosts());
    }

    @GetMapping("/post/{id}")
    public Result<DiscussionPost> getPost(@PathVariable Long id) {
        return Result.ok(discussionService.getPost(id));
    }

    @PostMapping("/comment")
    public Result<?> addComment(@RequestBody DiscussionComment comment) {
        discussionService.addComment(comment);
        return Result.ok("评论成功");
    }

    @GetMapping("/comments/{postId}")
    public Result<List<DiscussionComment>> getComments(@PathVariable Long postId) {
        return Result.ok(discussionService.getCommentsByPost(postId));
    }

    @PostMapping("/post/like/{id}")
    public Result<?> likePost(@PathVariable Long id) {
        discussionService.likePost(id);
        return Result.ok("点赞成功");
    }

    @PostMapping("/comment/like/{id}")
    public Result<?> likeComment(@PathVariable Long id) {
        discussionService.likeComment(id);
        return Result.ok("点赞成功");
    }
}
