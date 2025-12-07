package com.example.content.domain.model;

import com.example.common.domain.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 评论领域模型
 * 
 * @author system
 * @version 1.0.0
 * @since 1.0.0
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class Comment extends BaseEntity {
    
    /** 评论用户ID */
    private Long userId;
    
    /** 目标类型：ARTICLE-文章，COMMENT-评论（回复） */
    private String targetType;
    
    /** 目标ID（文章ID或评论ID） */
    private Long targetId;
    
    /** 父评论ID，NULL表示顶级评论 */
    private Long parentId;
    
    /** 评论内容 */
    private String content;
    
    /** 点赞数 */
    private Integer likeCount;
    
    /** 回复数 */
    private Integer replyCount;
    
    /** 审核状态：PENDING-待审核，APPROVED-已通过，REJECTED-已拒绝 */
    private String auditStatus;
    
    /** 审核人ID */
    private Long auditUserId;
    
    /** 审核时间 */
    private LocalDateTime auditTime;
    
    /** 审核原因 */
    private String auditReason;
    
    /** 状态：1-正常，0-禁用 */
    private Integer status;
    
    /** 子评论列表（不持久化） */
    private List<Comment> replies;
}

