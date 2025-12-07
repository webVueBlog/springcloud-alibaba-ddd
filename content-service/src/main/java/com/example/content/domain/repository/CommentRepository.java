package com.example.content.domain.repository;

import com.example.content.domain.model.Comment;

import java.util.List;

/**
 * 评论仓储接口
 * 
 * @author system
 * @version 1.0.0
 * @since 1.0.0
 */
public interface CommentRepository {
    
    /**
     * 保存评论
     */
    Comment save(Comment comment);
    
    /**
     * 根据ID查找评论
     */
    Comment findById(Long id);
    
    /**
     * 根据目标类型和目标ID查询顶级评论列表
     */
    List<Comment> findTopLevelComments(String targetType, Long targetId);
    
    /**
     * 根据父评论ID查询回复列表
     */
    List<Comment> findRepliesByParentId(Long parentId);
    
    /**
     * 删除评论（逻辑删除）
     */
    void delete(Long id);
    
    /**
     * 增加点赞数
     */
    void incrementLikeCount(Long id);
    
    /**
     * 减少点赞数
     */
    void decrementLikeCount(Long id);
    
    /**
     * 增加回复数
     */
    void incrementReplyCount(Long id);
    
    /**
     * 减少回复数
     */
    void decrementReplyCount(Long id);
}

