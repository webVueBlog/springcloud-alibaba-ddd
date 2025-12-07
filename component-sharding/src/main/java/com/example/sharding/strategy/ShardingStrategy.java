package com.example.sharding.strategy;

/**
 * 分片策略接口
 * <p>
 * 定义分片计算的核心方法，不同的实现类可以提供不同的分片算法
 * </p>
 * <p>
 * 实现类要求：
 * <ul>
 *   <li>使用 @Component 注解，以便 Spring 自动扫描和注册</li>
 *   <li>实现 calculateShardIndex 方法，提供分片索引计算逻辑</li>
 *   <li>实现 getStrategyName 方法，返回唯一的策略名称</li>
 * </ul>
 * </p>
 * <p>
 * 使用示例：
 * <pre>
 * @Component
 * public class CustomShardingStrategy implements ShardingStrategy {
 *     @Override
 *     public int calculateShardIndex(Object shardingKey, int shardCount) {
 *         // 自定义分片逻辑
 *         return ...;
 *     }
 *     
 *     @Override
 *     public String getStrategyName() {
 *         return "custom";
 *     }
 * }
 * </pre>
 * </p>
 * 
 * @author system
 * @version 1.0.0
 * @since 1.0.0
 */
public interface ShardingStrategy {
    
    /**
     * 计算分片索引
     * <p>
     * 根据分片键和分片数量计算分片索引
     * 返回的索引应该在 [0, shardCount-1] 范围内
     * </p>
     * 
     * @param shardingKey 分片键，可以是 Long、String、Number 等类型
     * @param shardCount 分片数量，必须大于 0
     * @return 分片索引，范围 [0, shardCount-1]
     * @throws IllegalArgumentException 如果 shardCount <= 0 或 shardingKey 为 null
     */
    int calculateShardIndex(Object shardingKey, int shardCount);
    
    /**
     * 获取策略名称
     * <p>
     * 策略名称用于在 ShardingService 中查找和选择策略
     * 每个策略应该有唯一的名称
     * </p>
     * 
     * @return 策略名称，如 "modulo"、"hash"、"range" 等
     */
    String getStrategyName();
}

