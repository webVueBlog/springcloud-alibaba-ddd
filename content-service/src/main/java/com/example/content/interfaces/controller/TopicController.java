package com.example.content.interfaces.controller;

import com.example.common.result.Result;
import com.example.content.domain.model.Topic;
import com.example.content.infrastructure.mapper.TopicMapper;
import com.example.content.infrastructure.po.TopicPO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 专题控制器
 * 
 * @author system
 * @version 1.0.0
 * @since 1.0.0
 */
@Slf4j
@RestController
@RequestMapping("/api/content/topic")
@RequiredArgsConstructor
public class TopicController {
    
    private final TopicMapper topicMapper;
    
    /**
     * 获取专题列表
     */
    @GetMapping
    public Result<List<Topic>> getTopicList() {
        log.info("获取专题列表");
        try {
            // 查询所有未删除且状态正常的专题
            List<TopicPO> pos = topicMapper.selectList(
                new com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper<TopicPO>()
                    .eq(TopicPO::getDeleted, 0)
                    .eq(TopicPO::getStatus, 1)
                    .orderByDesc(TopicPO::getArticleCount)
                    .orderByDesc(TopicPO::getCreateTime)
            );
            
            List<Topic> topics = pos.stream()
                .map(this::poToDomain)
                .collect(Collectors.toList());
            
            return Result.success(topics);
        } catch (Exception e) {
            log.error("获取专题列表失败", e);
            return Result.error("获取专题列表失败: " + e.getMessage());
        }
    }
    
    /**
     * 根据ID获取专题
     */
    @GetMapping("/{id}")
    public Result<Topic> getTopicById(@PathVariable Long id) {
        log.info("获取专题详情: id={}", id);
        try {
            TopicPO po = topicMapper.selectById(id);
            if (po == null || po.getDeleted() == 1) {
                return Result.error("专题不存在");
            }
            Topic topic = poToDomain(po);
            return Result.success(topic);
        } catch (Exception e) {
            log.error("获取专题详情失败: id={}", id, e);
            return Result.error("获取专题详情失败: " + e.getMessage());
        }
    }
    
    /**
     * PO转Domain
     */
    private Topic poToDomain(TopicPO po) {
        if (po == null) {
            return null;
        }
        Topic topic = new Topic();
        BeanUtils.copyProperties(po, topic);
        return topic;
    }
}

