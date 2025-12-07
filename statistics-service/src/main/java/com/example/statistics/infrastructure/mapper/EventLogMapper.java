package com.example.statistics.infrastructure.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.statistics.infrastructure.po.EventLogPO;
import org.apache.ibatis.annotations.Mapper;

/**
 * 事件日志Mapper接口
 * 
 * @author system
 * @version 1.0.0
 * @since 1.0.0
 */
@Mapper
public interface EventLogMapper extends BaseMapper<EventLogPO> {
}

