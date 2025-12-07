package com.example.content.domain.model;

import com.example.common.domain.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 文章领域模型
 * <p>
 * 文章的聚合根，包含文章的核心业务属性
 * </p>
 * 
 * @author system
 * @version 1.0.0
 * @since 1.0.0
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class Article extends BaseEntity {
    
    /** 作者ID */
    private Long userId;
    
    /** 文章标题 */
    private String title;
    
    /** 文章摘要 */
    private String summary;
    
    /** 封面图片URL */
    private String coverImage;
    
    /** 文章内容（Markdown/HTML） */
    private String content;
    
    /** 内容类型：MARKDOWN- Markdown，RICH_TEXT-富文本 */
    private String contentType;
    
    /** 分类ID */
    private Long categoryId;
    
    /** 阅读数 */
    private Integer viewCount;
    
    /** 点赞数 */
    private Integer likeCount;
    
    /** 评论数 */
    private Integer commentCount;
    
    /** 收藏数 */
    private Integer collectCount;
    
    /** 分享数 */
    private Integer shareCount;
    
    /** 发布状态：DRAFT-草稿，PUBLISHED-已发布，OFFLINE-已下架 */
    private String publishStatus;
    
    /** 发布时间 */
    private LocalDateTime publishTime;
    
    /** 访问级别：PUBLIC-公开，PRIVATE-私有，PAID-付费可见，SPECIFIED-指定用户可见 */
    private String accessLevel;
    
    /** 是否置顶：0-否，1-是 */
    private Integer isTop;
    
    /** 是否推荐：0-否，1-是 */
    private Integer isRecommend;
    
    /** 是否热门：0-否，1-是 */
    private Integer isHot;
    
    /** 版本号，用于历史版本回溯 */
    private Integer version;
    
    /** 状态：1-正常，0-禁用 */
    private Integer status;
    
    /** 标签列表（不持久化） */
    private List<Long> tagIds;
    
    /** 标签名称列表（不持久化） */
    private List<String> tagNames;
}

