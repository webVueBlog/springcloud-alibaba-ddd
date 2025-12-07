package com.example.content.infrastructure.po;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 专题持久化对象
 * 
 * @author system
 * @version 1.0.0
 * @since 1.0.0
 */
@Data
@TableName("content_topic")
public class TopicPO {
    
    @TableId(type = IdType.ASSIGN_ID)
    private Long id;
    
    private String topicName;
    private String topicCode;
    private String coverImage;
    private String description;
    private Integer articleCount;
    private Integer followerCount;
    private String accessLevel;
    private Integer status;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
    private Integer deleted;
}

