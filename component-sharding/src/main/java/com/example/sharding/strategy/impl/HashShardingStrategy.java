package com.example.sharding.strategy.impl;

import com.example.sharding.strategy.ShardingStrategy;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;

/**
 * 哈希分片策略
 * <p>
 * 使用哈希算法计算分片索引，适合字符串类型的分片键
 * </p>
 * <p>
 * 算法原理：
 * <ul>
 *   <li>对于字符串类型的分片键，使用 hashCode() 方法计算哈希值</li>
 *   <li>对于其他类型，也使用 hashCode() 方法</li>
 *   <li>对哈希值取绝对值后取模：shardIndex = Math.abs(hashCode) % shardCount</li>
 * </ul>
 * </p>
 * <p>
 * 优点：
 * <ul>
 *   <li>适合字符串类型的分片键</li>
 *   <li>数据分布相对均匀</li>
 *   <li>计算效率高</li>
 * </ul>
 * </p>
 * <p>
 * 缺点：
 * <ul>
 *   <li>可能存在哈希冲突</li>
 *   <li>当分片数量变化时，需要数据迁移</li>
 * </ul>
 * </p>
 * <p>
 * 注意：此策略与取模策略类似，主要区别在于对字符串类型的处理方式
 * </p>
 * 
 * @author system
 * @version 1.0.0
 * @since 1.0.0
 */
@Slf4j
@Component
public class HashShardingStrategy implements ShardingStrategy {
    
    /**
     * 计算分片索引（哈希算法）
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
            int hashCode;
            
            // 根据分片键类型计算哈希值
            if (shardingKey instanceof String) {
                // 字符串类型，使用 hashCode
                hashCode = ((String) shardingKey).hashCode();
            } else {
                // 其他类型，使用 hashCode
                hashCode = shardingKey.hashCode();
            }
            
            // 取绝对值后取模计算分片索引
            int shardIndex = Math.abs(hashCode) % shardCount;
            log.debug("哈希分片计算: key={}, hashCode={}, shardCount={}, shardIndex={}", 
                    shardingKey, hashCode, shardCount, shardIndex);
            return shardIndex;
        } catch (Exception e) {
            log.error("哈希分片计算失败: key={}, shardCount={}", shardingKey, shardCount, e);
            throw new RuntimeException("哈希分片计算失败: " + e.getMessage(), e);
        }
    }
    
    /**
     * 获取策略名称
     * 
     * @return 策略名称 "hash"
     */
    @Override
    public String getStrategyName() {
        return "hash";
    }
}

