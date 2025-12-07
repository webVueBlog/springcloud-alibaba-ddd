package com.example.statistics.domain.repository;

import com.example.statistics.domain.model.EventLog;

/**
 * 事件日志仓储接口
 * 
 * @author system
 * @version 1.0.0
 * @since 1.0.0
 */
public interface EventLogRepository {
    
    /**
     * 保存事件日志
     */
    void save(EventLog eventLog);
}

