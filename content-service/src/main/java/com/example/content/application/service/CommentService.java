package com.example.content.application.service;

import com.example.common.exception.BusinessException;
import com.example.content.domain.model.Comment;
import com.example.content.domain.repository.CommentRepository;
import com.example.content.domain.repository.ArticleRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 评论应用服务
 * 
 * @author system
 * @version 1.0.0
 * @since 1.0.0
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class CommentService {
    
    private final CommentRepository commentRepository;
    private final ArticleRepository articleRepository;
    
    /**
     * 创建评论
     */
    @Transactional
    public Comment createComment(Comment comment) {
        Assert.notNull(comment, "评论不能为空");
        Assert.notNull(comment.getUserId(), "用户ID不能为空");
        Assert.hasText(comment.getTargetType(), "目标类型不能为空");
        Assert.notNull(comment.getTargetId(), "目标ID不能为空");
        Assert.hasText(comment.getContent(), "评论内容不能为空");
        
        try {
            // 设置默认值
            if (comment.getLikeCount() == null) {
                comment.setLikeCount(0);
            }
            if (comment.getReplyCount() == null) {
                comment.setReplyCount(0);
            }
            if (comment.getAuditStatus() == null) {
                comment.setAuditStatus("APPROVED"); // 默认已通过审核
            }
            if (comment.getStatus() == null) {
                comment.setStatus(1);
            }
            
            Comment saved = commentRepository.save(comment);
            
            // 如果是回复，增加父评论的回复数
            if (saved.getParentId() != null) {
                commentRepository.incrementReplyCount(saved.getParentId());
            }
            
            // 如果是文章评论，增加文章的评论数
            if ("ARTICLE".equals(saved.getTargetType()) && saved.getTargetId() != null) {
                articleRepository.incrementCommentCount(saved.getTargetId());
            }
            
            return saved;
        } catch (Exception e) {
            log.error("创建评论失败: userId={}, targetType={}, targetId={}", 
                comment.getUserId(), comment.getTargetType(), comment.getTargetId(), e);
            throw new BusinessException("创建评论失败: " + e.getMessage());
        }
    }
    
    /**
     * 删除评论
     */
    @Transactional
    public void deleteComment(Long id) {
        Assert.notNull(id, "评论ID不能为空");
        
        try {
            Comment comment = commentRepository.findById(id);
            if (comment == null) {
                throw new BusinessException("评论不存在");
            }
            
            commentRepository.delete(id);
            
            // 如果是文章评论，减少文章的评论数
            if ("ARTICLE".equals(comment.getTargetType()) && comment.getTargetId() != null) {
                articleRepository.decrementCommentCount(comment.getTargetId());
            }
            
            // 如果是回复，减少父评论的回复数
            if (comment.getParentId() != null) {
                commentRepository.decrementReplyCount(comment.getParentId());
            }
        } catch (BusinessException e) {
            throw e;
        } catch (Exception e) {
            log.error("删除评论失败: id={}", id, e);
            throw new BusinessException("删除评论失败: " + e.getMessage());
        }
    }
    
    /**
     * 根据ID获取评论
     */
    public Comment getCommentById(Long id) {
        Assert.notNull(id, "评论ID不能为空");
        
        Comment comment = commentRepository.findById(id);
        if (comment == null) {
            throw new BusinessException("评论不存在");
        }
        
        return comment;
    }
    
    /**
     * 查询评论列表（包含回复）
     */
    public List<Comment> getCommentList(String targetType, Long targetId) {
        try {
            // 查询顶级评论
            List<Comment> topComments = commentRepository.findTopLevelComments(targetType, targetId);
            
            // 为每个顶级评论加载回复
            return topComments.stream()
                .map(comment -> {
                    List<Comment> replies = commentRepository.findRepliesByParentId(comment.getId());
                    comment.setReplies(replies);
                    return comment;
                })
                .collect(Collectors.toList());
        } catch (Exception e) {
            log.error("查询评论列表失败: targetType={}, targetId={}", targetType, targetId, e);
            throw new BusinessException("查询评论列表失败: " + e.getMessage());
        }
    }
    
    /**
     * 点赞评论
     */
    @Transactional
    public void likeComment(Long id) {
        Assert.notNull(id, "评论ID不能为空");
        
        try {
            Comment comment = commentRepository.findById(id);
            if (comment == null) {
                throw new BusinessException("评论不存在");
            }
            
            commentRepository.incrementLikeCount(id);
        } catch (BusinessException e) {
            throw e;
        } catch (Exception e) {
            log.error("点赞评论失败: id={}", id, e);
            throw new BusinessException("点赞评论失败: " + e.getMessage());
        }
    }
    
    /**
     * 取消点赞评论
     */
    @Transactional
    public void unlikeComment(Long id) {
        Assert.notNull(id, "评论ID不能为空");
        
        try {
            Comment comment = commentRepository.findById(id);
            if (comment == null) {
                throw new BusinessException("评论不存在");
            }
            
            commentRepository.decrementLikeCount(id);
        } catch (BusinessException e) {
            throw e;
        } catch (Exception e) {
            log.error("取消点赞评论失败: id={}", id, e);
            throw new BusinessException("取消点赞评论失败: " + e.getMessage());
        }
    }
}

