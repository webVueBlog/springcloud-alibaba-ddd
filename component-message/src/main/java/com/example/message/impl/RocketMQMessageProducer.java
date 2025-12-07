package com.example.message.impl;

import com.alibaba.fastjson2.JSON;
import com.example.message.MessageProducer;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.spring.core.RocketMQTemplate;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Component;

/**
 * RocketMQ 消息生产者实现
 * <p>
 * 基于 RocketMQ 和 RocketMQTemplate 实现消息发送功能
 * 支持普通消息和延迟消息的发送
 * </p>
 * <p>
 * 特性：
 * <ul>
 *   <li>自动序列化：消息对象自动序列化为 JSON 字符串</li>
 *   <li>支持延迟消息：支持 18 个延迟级别</li>
 *   <li>完善的日志记录：记录消息发送的详细信息</li>
 * </ul>
 * </p>
 * <p>
 * 注意：只有当 RocketMQTemplate 存在时才会创建此 Bean
 * 如果 RocketMQ 未配置，此服务不会被创建，使用此服务的代码需要处理 null 的情况
 * </p>
 * 
 * @author system
 * @version 1.0.0
 * @since 1.0.0
 */
@Slf4j
@Component
@RequiredArgsConstructor
@ConditionalOnBean(RocketMQTemplate.class)
public class RocketMQMessageProducer implements MessageProducer {

    /** RocketMQ 模板，用于发送消息 */
    private final RocketMQTemplate rocketMQTemplate;

    /**
     * 发送消息
     * <p>
     * 将消息对象序列化为 JSON 字符串后发送到指定的 topic 和 tag
     * </p>
     * 
     * @param topic 消息主题
     * @param tag 消息标签，可以为空字符串
     * @param message 消息对象
     */
    @Override
    public void send(String topic, String tag, Object message) {
        try {
            if (topic == null || topic.trim().isEmpty()) {
                throw new IllegalArgumentException("消息主题不能为空");
            }
            if (message == null) {
                throw new IllegalArgumentException("消息内容不能为空");
            }
            
            // 构建 destination：topic:tag
            String destination = buildDestination(topic, tag);
            
            // 序列化消息
            String messageBody = JSON.toJSONString(message);
            
            // 发送消息
            rocketMQTemplate.convertAndSend(destination, messageBody);
            
            log.info("发送消息成功: topic={}, tag={}, messageLength={}", 
                    topic, tag, messageBody.length());
            log.debug("消息内容: {}", messageBody);
        } catch (Exception e) {
            log.error("发送消息失败: topic={}, tag={}", topic, tag, e);
            throw new RuntimeException("发送消息失败: " + e.getMessage(), e);
        }
    }

    /**
     * 发送延迟消息
     * <p>
     * 发送延迟消息，消息会在指定延迟级别后消费
     * </p>
     * <p>
     * 延迟级别说明：
     * <ul>
     *   <li>1-18：对应不同的延迟时间（1秒到2小时）</li>
     *   <li>如果 delayLevel 不在 1-18 范围内，会使用默认延迟级别</li>
     * </ul>
     * </p>
     * 
     * @param topic 消息主题
     * @param tag 消息标签，可以为空字符串
     * @param message 消息对象
     * @param delayLevel 延迟级别（1-18）
     */
    @Override
    public void sendDelay(String topic, String tag, Object message, long delayLevel) {
        try {
            if (topic == null || topic.trim().isEmpty()) {
                throw new IllegalArgumentException("消息主题不能为空");
            }
            if (message == null) {
                throw new IllegalArgumentException("消息内容不能为空");
            }
            if (delayLevel < 1 || delayLevel > 18) {
                log.warn("延迟级别超出范围 (1-18)，使用默认值: delayLevel={}", delayLevel);
                delayLevel = 3;  // 默认 10 秒
            }
            
            // 构建 destination：topic:tag
            String destination = buildDestination(topic, tag);
            
            // 序列化消息
            String messageBody = JSON.toJSONString(message);
            
            // 构建消息对象，设置延迟级别
            org.springframework.messaging.Message<String> rocketMessage = MessageBuilder
                    .withPayload(messageBody)
                    .setHeader("DELAY", delayLevel)
                    .build();
            
            // 发送延迟消息
            rocketMQTemplate.syncSend(destination, rocketMessage);
            
            log.info("发送延迟消息成功: topic={}, tag={}, delayLevel={}, messageLength={}", 
                    topic, tag, delayLevel, messageBody.length());
            log.debug("消息内容: {}", messageBody);
        } catch (Exception e) {
            log.error("发送延迟消息失败: topic={}, tag={}, delayLevel={}", topic, tag, delayLevel, e);
            throw new RuntimeException("发送延迟消息失败: " + e.getMessage(), e);
        }
    }
    
    /**
     * 构建消息目标地址
     * <p>
     * 格式：topic:tag，如果 tag 为空则只使用 topic
     * </p>
     * 
     * @param topic 消息主题
     * @param tag 消息标签
     * @return 消息目标地址
     */
    private String buildDestination(String topic, String tag) {
        if (tag == null || tag.trim().isEmpty()) {
            return topic;
        }
        return topic + ":" + tag;
    }
}

