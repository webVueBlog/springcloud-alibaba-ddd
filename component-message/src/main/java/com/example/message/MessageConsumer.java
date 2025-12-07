package com.example.message;

/**
 * 消息消费者接口
 * <p>
 * 提供消息消费功能，用于处理从消息队列接收到的消息
 * </p>
 * <p>
 * 注意：此接口主要用于定义消费逻辑，实际的消息监听由 RocketMQ 的注解实现
 * 使用 {@link org.apache.rocketmq.spring.annotation.RocketMQMessageListener} 注解监听消息
 * </p>
 * <p>
 * 使用示例：
 * <pre>
 * @Component
 * @RocketMQMessageListener(topic = "order-topic", consumerGroup = "order-consumer-group")
 * public class OrderMessageConsumer implements RocketMQListener&lt;String&gt; {
 *     @Override
 *     public void onMessage(String message) {
 *         // 处理消息
 *         OrderDTO order = JSON.parseObject(message, OrderDTO.class);
 *         orderService.processOrder(order);
 *     }
 * }
 * </pre>
 * </p>
 * 
 * @author system
 * @version 1.0.0
 * @since 1.0.0
 * @see org.apache.rocketmq.spring.annotation.RocketMQMessageListener
 * @see org.apache.rocketmq.spring.core.RocketMQListener
 */
public interface MessageConsumer {
    
    /**
     * 消费消息
     * <p>
     * 处理从消息队列接收到的消息
     * </p>
     * <p>
     * 注意：此方法主要用于定义消费逻辑的接口规范
     * 实际的消息消费通过实现 {@link org.apache.rocketmq.spring.core.RocketMQListener} 接口
     * 并使用 {@link org.apache.rocketmq.spring.annotation.RocketMQMessageListener} 注解实现
     * </p>
     * 
     * @param topic 消息主题
     * @param tag 消息标签
     * @param message 消息内容（JSON 字符串）
     */
    void consume(String topic, String tag, String message);
}

