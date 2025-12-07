package com.example.audit.infrastructure.repository;

import com.example.audit.domain.model.AuditLog;
import com.example.audit.domain.repository.AuditLogRepository;
import com.example.audit.infrastructure.mapper.AuditLogMapper;
import com.example.audit.infrastructure.po.AuditLogPO;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 审计日志仓储实现
 * 
 * @author system
 * @version 1.0.0
 * @since 1.0.0
 */
@Repository
@RequiredArgsConstructor
public class AuditLogRepositoryImpl implements AuditLogRepository {
    
    private final AuditLogMapper auditLogMapper;
    
    @Override
    public void save(AuditLog auditLog) {
        AuditLogPO po = domainToPo(auditLog);
        auditLogMapper.insert(po);
    }
    
    @Override
    public List<AuditLog> findByCondition(Long userId, String module, String operationType, 
                                           LocalDateTime startTime, LocalDateTime endTime) {
        List<AuditLogPO> pos = auditLogMapper.findByCondition(userId, module, operationType, startTime, endTime);
        return pos.stream().map(this::poToDomain).collect(Collectors.toList());
    }
    
    private AuditLog poToDomain(AuditLogPO po) {
        if (po == null) {
            return null;
        }
        AuditLog auditLog = new AuditLog();
        BeanUtils.copyProperties(po, auditLog);
        return auditLog;
    }
    
    private AuditLogPO domainToPo(AuditLog auditLog) {
        if (auditLog == null) {
            return null;
        }
        AuditLogPO po = new AuditLogPO();
        BeanUtils.copyProperties(auditLog, po);
        return po;
    }
}

