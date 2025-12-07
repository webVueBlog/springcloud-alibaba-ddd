package com.example.content.infrastructure.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.content.infrastructure.po.TopicPO;
import org.apache.ibatis.annotations.Mapper;

/**
 * 专题Mapper接口
 * 
 * @author system
 * @version 1.0.0
 * @since 1.0.0
 */
@Mapper
public interface TopicMapper extends BaseMapper<TopicPO> {
}

