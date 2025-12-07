package com.example.audit.application.service;

import com.example.audit.domain.model.AuditLog;
import com.example.audit.domain.repository.AuditLogRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 审计日志应用服务
 * 
 * @author system
 * @version 1.0.0
 * @since 1.0.0
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class AuditLogService {
    
    private final AuditLogRepository auditLogRepository;
    
    /**
     * 保存审计日志
     */
    public void saveAuditLog(AuditLog auditLog) {
        try {
            auditLogRepository.save(auditLog);
        } catch (Exception e) {
            log.error("保存审计日志失败", e);
            // 审计日志保存失败不应该影响主业务流程，只记录错误日志
        }
    }
    
    /**
     * 查询审计日志
     */
    public List<AuditLog> getAuditLogList(Long userId, String module, String operationType, 
                                          LocalDateTime startTime, LocalDateTime endTime) {
        return auditLogRepository.findByCondition(userId, module, operationType, startTime, endTime);
    }
}

