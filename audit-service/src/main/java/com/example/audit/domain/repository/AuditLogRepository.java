package com.example.audit.domain.repository;

import com.example.audit.domain.model.AuditLog;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 审计日志仓储接口
 * 
 * @author system
 * @version 1.0.0
 * @since 1.0.0
 */
public interface AuditLogRepository {
    
    /**
     * 保存审计日志
     */
    void save(AuditLog auditLog);
    
    /**
     * 根据条件查询审计日志
     */
    List<AuditLog> findByCondition(Long userId, String module, String operationType, 
                                    LocalDateTime startTime, LocalDateTime endTime);
}

