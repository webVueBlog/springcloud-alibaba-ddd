package com.example.statistics.domain.model;

import lombok.Data;

import java.time.LocalDateTime;

/**
 * 事件日志领域模型
 * 
 * @author system
 * @version 1.0.0
 * @since 1.0.0
 */
@Data
public class EventLog {
    
    /** 用户ID，未登录用户为NULL */
    private Long userId;
    
    /** 事件类型：VIEW-浏览，CLICK-点击，LIKE-点赞，COMMENT-评论，SHARE-分享，SEARCH-搜索等 */
    private String eventType;
    
    /** 事件分类：ARTICLE-文章，TOPIC-专题，BOILING_POINT-沸点，COURSE-课程等 */
    private String eventCategory;
    
    /** 目标ID（文章ID、专题ID等） */
    private Long targetId;
    
    /** 目标类型 */
    private String targetType;
    
    /** IP地址 */
    private String ipAddress;
    
    /** 用户代理（浏览器信息） */
    private String userAgent;
    
    /** 来源页面 */
    private String referer;
    
    /** 设备类型：PC，MOBILE，TABLET */
    private String deviceType;
    
    /** 操作系统类型 */
    private String osType;
    
    /** 浏览器类型 */
    private String browserType;
    
    /** 会话ID */
    private String sessionId;
    
    /** 扩展数据，JSON格式 */
    private String extraData;
    
    /** 创建时间 */
    private LocalDateTime createTime;
}

