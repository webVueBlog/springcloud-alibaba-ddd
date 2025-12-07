package com.example.content.infrastructure.po;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 文章持久化对象
 * 
 * @author system
 * @version 1.0.0
 * @since 1.0.0
 */
@Data
@TableName("content_article")
public class ArticlePO {
    
    @TableId(type = IdType.ASSIGN_ID)
    private Long id;
    
    private Long userId;
    private String title;
    private String summary;
    private String coverImage;
    private String content;
    private String contentType;
    private Long categoryId;
    private Integer viewCount;
    private Integer likeCount;
    private Integer commentCount;
    private Integer collectCount;
    private Integer shareCount;
    private String publishStatus;
    private LocalDateTime publishTime;
    private String accessLevel;
    private Integer isTop;
    private Integer isRecommend;
    private Integer isHot;
    private Integer version;
    private Integer status;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
    private Integer deleted;
}

