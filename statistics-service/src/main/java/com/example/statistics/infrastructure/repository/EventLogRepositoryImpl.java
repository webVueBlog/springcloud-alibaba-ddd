package com.example.statistics.infrastructure.repository;

import com.example.statistics.domain.model.EventLog;
import com.example.statistics.domain.repository.EventLogRepository;
import com.example.statistics.infrastructure.mapper.EventLogMapper;
import com.example.statistics.infrastructure.po.EventLogPO;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Repository;

/**
 * 事件日志仓储实现
 * 
 * @author system
 * @version 1.0.0
 * @since 1.0.0
 */
@Repository
@RequiredArgsConstructor
public class EventLogRepositoryImpl implements EventLogRepository {
    
    private final EventLogMapper eventLogMapper;
    
    @Override
    public void save(EventLog eventLog) {
        EventLogPO po = domainToPo(eventLog);
        eventLogMapper.insert(po);
    }
    
    private EventLogPO domainToPo(EventLog eventLog) {
        if (eventLog == null) {
            return null;
        }
        EventLogPO po = new EventLogPO();
        BeanUtils.copyProperties(eventLog, po);
        return po;
    }
}

