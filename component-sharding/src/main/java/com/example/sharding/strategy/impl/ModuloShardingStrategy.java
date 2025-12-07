package com.example.sharding.strategy.impl;

import com.example.sharding.strategy.ShardingStrategy;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;

/**
 * 取模分片策略
 * <p>
 * 使用取模算法计算分片索引，是最常用的分片策略
 * </p>
 * <p>
 * 算法原理：
 * <ul>
 *   <li>对于数字类型的分片键，直接取模：shardIndex = shardingKey % shardCount</li>
 *   <li>对于字符串类型的分片键，先计算 hashCode，再取模</li>
 *   <li>对于其他类型，使用 hashCode 取模</li>
 * </ul>
 * </p>
 * <p>
 * 优点：
 * <ul>
 *   <li>算法简单，计算效率高</li>
 *   <li>数据分布相对均匀</li>
 *   <li>适合大多数场景</li>
 * </ul>
 * </p>
 * <p>
 * 缺点：
 * <ul>
 *   <li>当分片数量变化时，需要数据迁移</li>
 *   <li>对于字符串类型，可能存在哈希冲突</li>
 * </ul>
 * </p>
 * 
 * @author system
 * @version 1.0.0
 * @since 1.0.0
 */
@Slf4j
@Component
public class ModuloShardingStrategy implements ShardingStrategy {
    
    /**
     * 计算分片索引（取模算法）
     * 
     * @param shardingKey 分片键
     * @param shardCount 分片数量
     * @return 分片索引
     */
    @Override
    public int calculateShardIndex(Object shardingKey, int shardCount) {
        Assert.notNull(shardingKey, "分片键不能为空");
        Assert.isTrue(shardCount > 0, "分片数量必须大于0");
        
        try {
            long keyValue;
            
            // 根据分片键类型计算数值
            if (shardingKey instanceof Number) {
                // 数字类型，直接使用
                keyValue = ((Number) shardingKey).longValue();
            } else if (shardingKey instanceof String) {
                // 字符串类型，使用 hashCode
                keyValue = ((String) shardingKey).hashCode();
            } else {
                // 其他类型，使用 hashCode
                keyValue = shardingKey.hashCode();
            }
            
            // 取模计算分片索引
            int shardIndex = (int) (Math.abs(keyValue) % shardCount);
            log.debug("取模分片计算: key={}, keyValue={}, shardCount={}, shardIndex={}", 
                    shardingKey, keyValue, shardCount, shardIndex);
            return shardIndex;
        } catch (Exception e) {
            log.error("取模分片计算失败: key={}, shardCount={}", shardingKey, shardCount, e);
            throw new RuntimeException("取模分片计算失败: " + e.getMessage(), e);
        }
    }
    
    /**
     * 获取策略名称
     * 
     * @return 策略名称 "modulo"
     */
    @Override
    public String getStrategyName() {
        return "modulo";
    }
}

