package com.example.content.interfaces.controller;

import com.example.common.result.Result;
import com.example.content.application.service.CommentService;
import com.example.content.domain.model.Comment;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.List;

/**
 * 评论控制器
 * 
 * @author system
 * @version 1.0.0
 * @since 1.0.0
 */
@Slf4j
@RestController
@RequestMapping("/api/content/comment")
@RequiredArgsConstructor
@Validated
public class CommentController {
    
    private final CommentService commentService;
    
    /**
     * 创建评论
     */
    @PostMapping
    public Result<Comment> createComment(
            @Valid @RequestBody Comment comment,
            @RequestHeader(value = "X-User-Id", required = false) Long currentUserId) {
        log.info("创建评论: userId={}, targetType={}, targetId={}", 
            comment.getUserId(), comment.getTargetType(), comment.getTargetId());
        
        // 如果请求头中有用户ID，使用请求头中的用户ID（更安全）
        if (currentUserId != null) {
            comment.setUserId(currentUserId);
        }
        
        Comment result = commentService.createComment(comment);
        return Result.success(result);
    }
    
    /**
     * 删除评论
     */
    @DeleteMapping("/{id}")
    public Result<Void> deleteComment(
            @PathVariable @NotNull(message = "评论ID不能为空") Long id) {
        log.info("删除评论: id={}", id);
        commentService.deleteComment(id);
        return Result.success();
    }
    
    /**
     * 根据ID获取评论
     */
    @GetMapping("/{id}")
    public Result<Comment> getCommentById(
            @PathVariable @NotNull(message = "评论ID不能为空") Long id) {
        Comment comment = commentService.getCommentById(id);
        return Result.success(comment);
    }
    
    /**
     * 查询评论列表
     */
    @GetMapping
    public Result<List<Comment>> getCommentList(
            @RequestParam @NotBlank(message = "目标类型不能为空") String targetType,
            @RequestParam @NotNull(message = "目标ID不能为空") Long targetId) {
        List<Comment> comments = commentService.getCommentList(targetType, targetId);
        return Result.success(comments);
    }
    
    /**
     * 点赞评论
     */
    @PostMapping("/{id}/like")
    public Result<Void> likeComment(
            @PathVariable @NotNull(message = "评论ID不能为空") Long id) {
        commentService.likeComment(id);
        return Result.success();
    }
    
    /**
     * 取消点赞评论
     */
    @PostMapping("/{id}/unlike")
    public Result<Void> unlikeComment(
            @PathVariable @NotNull(message = "评论ID不能为空") Long id) {
        commentService.unlikeComment(id);
        return Result.success();
    }
}

