package com.example.content.infrastructure.po;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 评论持久化对象
 * 
 * @author system
 * @version 1.0.0
 * @since 1.0.0
 */
@Data
@TableName("content_comment")
public class CommentPO {
    
    @TableId(type = IdType.ASSIGN_ID)
    private Long id;
    
    private Long userId;
    private String targetType;
    private Long targetId;
    private Long parentId;
    private String content;
    private Integer likeCount;
    private Integer replyCount;
    private String auditStatus;
    private Long auditUserId;
    private LocalDateTime auditTime;
    private String auditReason;
    private Integer status;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
    private Integer deleted;
}

