package com.example.statistics.infrastructure.po;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 事件日志持久化对象
 * 
 * @author system
 * @version 1.0.0
 * @since 1.0.0
 */
@Data
@TableName("statistics_event_log")
public class EventLogPO {
    
    @TableId(type = IdType.ASSIGN_ID)
    private Long id;
    
    private Long userId;
    private String eventType;
    private String eventCategory;
    private Long targetId;
    private String targetType;
    private String ipAddress;
    private String userAgent;
    private String referer;
    private String deviceType;
    private String osType;
    private String browserType;
    private String sessionId;
    private String extraData;
    private LocalDateTime createTime;
}

