package com.example.audit.domain.model;

import com.example.common.domain.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDateTime;

/**
 * 审计日志领域模型
 * 
 * @author system
 * @version 1.0.0
 * @since 1.0.0
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class AuditLog extends BaseEntity {
    
    /** 操作人ID */
    private Long userId;
    
    /** 操作人用户名 */
    private String username;
    
    /** 操作类型：CREATE-新增，UPDATE-修改，DELETE-删除，VIEW-查看，LOGIN-登录，LOGOUT-登出等 */
    private String operationType;
    
    /** 操作模块：USER-用户，ROLE-角色，PERMISSION-权限，ARTICLE-文章，TOPIC-专题等 */
    private String module;
    
    /** 目标类型 */
    private String targetType;
    
    /** 目标ID */
    private Long targetId;
    
    /** 目标名称 */
    private String targetName;
    
    /** 操作描述 */
    private String operationDesc;
    
    /** IP地址 */
    private String ipAddress;
    
    /** 用户代理 */
    private String userAgent;
    
    /** 请求方法：GET，POST，PUT，DELETE等 */
    private String requestMethod;
    
    /** 请求URL */
    private String requestUrl;
    
    /** 请求参数，JSON格式 */
    private String requestParams;
    
    /** 变更前值，JSON格式 */
    private String oldValue;
    
    /** 变更后值，JSON格式 */
    private String newValue;
    
    /** 变更字段，逗号分隔 */
    private String changedFields;
    
    /** 操作状态：SUCCESS-成功，FAILED-失败 */
    private String operationStatus;
    
    /** 错误信息 */
    private String errorMessage;
    
    /** 执行时间（毫秒） */
    private Integer executionTime;
}

