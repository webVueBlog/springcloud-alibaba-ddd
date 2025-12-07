package com.example.message.impl;

import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.spring.annotation.ConsumeMode;
import org.apache.rocketmq.spring.annotation.RocketMQMessageListener;
import org.apache.rocketmq.spring.core.RocketMQListener;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

/**
 * RocketMQ 消息消费者示例实现
 * <p>
 * 这是一个示例实现，展示如何使用 RocketMQ 消费消息
 * 实际使用时，应该创建自己的消费者类，实现 RocketMQListener 接口
 * </p>
 * <p>
 * 使用说明：
 * <ul>
 *   <li>使用 @RocketMQMessageListener 注解指定 topic 和 consumerGroup</li>
 *   <li>实现 RocketMQListener 接口的 onMessage 方法</li>
 *   <li>在 onMessage 方法中处理消息逻辑</li>
 * </ul>
 * </p>
 * <p>
 * 注意：此示例仅用于演示，实际使用时应该：
 * <ul>
 *   <li>根据业务需求创建不同的消费者类</li>
 *   <li>为每个消费者指定不同的 topic 和 consumerGroup</li>
 *   <li>实现具体的业务逻辑</li>
 * </ul>
 * </p>
 * 
 * @author system
 * @version 1.0.0
 * @since 1.0.0
 * @see org.apache.rocketmq.spring.annotation.RocketMQMessageListener
 * @see org.apache.rocketmq.spring.core.RocketMQListener
 */
@Slf4j
@Component
@ConditionalOnProperty(name = "rocketmq.name-server")
@RocketMQMessageListener(
    topic = "order-topic", 
    consumerGroup = "order-consumer-group",
    consumeMode = ConsumeMode.CONCURRENTLY  // 并发消费模式（默认）
    // 如果需要顺序消费，可以设置为：consumeMode = ConsumeMode.ORDERLY
)
public class RocketMQMessageConsumer implements RocketMQListener<String> {

    /**
     * 消费消息
     * <p>
     * 当消息队列中有新消息时，此方法会被自动调用
     * </p>
     * <p>
     * 注意：
     * <ul>
     *   <li>消息内容为 JSON 字符串，需要反序列化为对象</li>
     *   <li>如果处理失败，消息会重新投递（根据重试策略）</li>
     *   <li>应该实现幂等性，确保重复消费不会产生问题</li>
     * </ul>
     * </p>
     * 
     * @param message 消息内容（JSON 字符串）
     */
    @Override
    public void onMessage(String message) {
        try {
            log.info("收到消息: topic=order-topic, message={}", message);
            
            // 反序列化消息
            // OrderDTO order = JSON.parseObject(message, OrderDTO.class);
            
            // 处理消息逻辑
            // orderService.processOrder(order);
            
            log.info("消息处理成功: message={}", message);
        } catch (Exception e) {
            log.error("消息处理失败: message={}", message, e);
            // 抛出异常会导致消息重新投递
            throw new RuntimeException("消息处理失败: " + e.getMessage(), e);
        }
    }
}

