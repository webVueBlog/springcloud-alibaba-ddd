package com.example.audit.infrastructure.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.audit.infrastructure.po.AuditLogPO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 审计日志Mapper接口
 * 
 * @author system
 * @version 1.0.0
 * @since 1.0.0
 */
@Mapper
public interface AuditLogMapper extends BaseMapper<AuditLogPO> {
    
    /**
     * 根据条件查询审计日志
     */
    List<AuditLogPO> findByCondition(
            @Param("userId") Long userId,
            @Param("module") String module,
            @Param("operationType") String operationType,
            @Param("startTime") LocalDateTime startTime,
            @Param("endTime") LocalDateTime endTime
    );
}

