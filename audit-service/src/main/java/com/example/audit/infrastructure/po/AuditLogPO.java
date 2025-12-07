package com.example.audit.infrastructure.po;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 审计日志持久化对象
 * 
 * @author system
 * @version 1.0.0
 * @since 1.0.0
 */
@Data
@TableName("sys_audit_log")
public class AuditLogPO {
    
    @TableId(type = IdType.ASSIGN_ID)
    private Long id;
    
    private Long userId;
    private String username;
    private String operationType;
    private String module;
    private String targetType;
    private Long targetId;
    private String targetName;
    private String operationDesc;
    private String ipAddress;
    private String userAgent;
    private String requestMethod;
    private String requestUrl;
    private String requestParams;
    private String oldValue;
    private String newValue;
    private String changedFields;
    private String operationStatus;
    private String errorMessage;
    private Integer executionTime;
    private LocalDateTime createTime;
}

