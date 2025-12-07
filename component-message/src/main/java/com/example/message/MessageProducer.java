package com.example.message;

/**
 * 消息生产者接口
 * <p>
 * 提供消息发送功能，支持普通消息和延迟消息
 * 基于 RocketMQ 实现，支持分布式消息队列
 * </p>
 * <p>
 * 使用场景：
 * <ul>
 *   <li>异步处理：将耗时操作异步化</li>
 *   <li>解耦服务：服务间通过消息队列解耦</li>
 *   <li>削峰填谷：处理高并发请求</li>
 *   <li>延迟任务：发送延迟消息实现定时任务</li>
 * </ul>
 * </p>
 * <p>
 * 使用示例：
 * <pre>
 * // 注入消息生产者
 * @Autowired(required = false)
 * private MessageProducer messageProducer;
 * 
 * // 发送普通消息
 * messageProducer.send("order-topic", "create", orderDTO);
 * 
 * // 发送延迟消息（延迟级别 3，约 10 秒后消费）
 * messageProducer.sendDelay("order-topic", "cancel", orderDTO, 3);
 * </pre>
 * </p>
 * 
 * @author system
 * @version 1.0.0
 * @since 1.0.0
 */
public interface MessageProducer {
    
    /**
     * 发送消息
     * <p>
     * 向指定的 topic 和 tag 发送消息，消息会被序列化为 JSON 字符串
     * </p>
     * 
     * @param topic 消息主题，用于消息分类
     * @param tag 消息标签，用于消息过滤，可以为空字符串
     * @param message 消息对象，会被序列化为 JSON 字符串
     * @throws RuntimeException 如果消息发送失败
     */
    void send(String topic, String tag, Object message);

    /**
     * 发送延迟消息
     * <p>
     * 向指定的 topic 和 tag 发送延迟消息，消息会在指定延迟级别后消费
     * </p>
     * <p>
     * RocketMQ 延迟级别说明：
     * <ul>
     *   <li>1: 1秒</li>
     *   <li>2: 5秒</li>
     *   <li>3: 10秒</li>
     *   <li>4: 30秒</li>
     *   <li>5: 1分钟</li>
     *   <li>6: 2分钟</li>
     *   <li>7: 3分钟</li>
     *   <li>8: 4分钟</li>
     *   <li>9: 5分钟</li>
     *   <li>10: 6分钟</li>
     *   <li>11: 7分钟</li>
     *   <li>12: 8分钟</li>
     *   <li>13: 9分钟</li>
     *   <li>14: 10分钟</li>
     *   <li>15: 20分钟</li>
     *   <li>16: 30分钟</li>
     *   <li>17: 1小时</li>
     *   <li>18: 2小时</li>
     * </ul>
     * </p>
     * 
     * @param topic 消息主题
     * @param tag 消息标签，可以为空字符串
     * @param message 消息对象，会被序列化为 JSON 字符串
     * @param delayLevel 延迟级别（1-18），对应不同的延迟时间
     * @throws RuntimeException 如果消息发送失败
     */
    void sendDelay(String topic, String tag, Object message, long delayLevel);
}

