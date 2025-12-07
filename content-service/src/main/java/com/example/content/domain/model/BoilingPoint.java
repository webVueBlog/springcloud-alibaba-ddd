package com.example.content.domain.model;

import com.example.common.domain.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;

/**
 * 沸点领域模型（短内容）
 * 
 * @author system
 * @version 1.0.0
 * @since 1.0.0
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class BoilingPoint extends BaseEntity {
    
    /** 发布用户ID */
    private Long userId;
    
    /** 沸点内容 */
    private String content;
    
    /** 图片URLs，JSON数组格式 */
    private String imageUrls;
    
    /** 点赞数 */
    private Integer likeCount;
    
    /** 评论数 */
    private Integer commentCount;
    
    /** 分享数 */
    private Integer shareCount;
    
    /** 审核状态：PENDING-待审核，APPROVED-已通过，REJECTED-已拒绝 */
    private String auditStatus;
    
    /** 状态：1-正常，0-禁用 */
    private Integer status;
    
    /** 图片URL列表（不持久化） */
    private List<String> imageUrlList;
}

