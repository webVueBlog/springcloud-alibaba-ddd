package com.example.sharding.service.impl;

import com.example.sharding.service.ShardingService;
import com.example.sharding.strategy.ShardingStrategy;
import com.example.sharding.util.ShardingUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 分片服务实现
 * <p>
 * 提供分库分表的核心功能实现，支持多种分片策略
 * </p>
 * <p>
 * 特性：
 * <ul>
 *   <li>支持多种分片策略（取模、哈希等）</li>
 *   <li>自动注册和发现分片策略</li>
 *   <li>默认使用取模策略</li>
 *   <li>线程安全</li>
 * </ul>
 * </p>
 * 
 * @author system
 * @version 1.0.0
 * @since 1.0.0
 */
@Slf4j
@Service
public class ShardingServiceImpl implements ShardingService {
    
    /** 分片策略列表（自动注入） */
    @Autowired(required = false)
    private List<ShardingStrategy> shardingStrategies;
    
    /** 策略名称到策略实现的映射 */
    private final Map<String, ShardingStrategy> strategyMap = new ConcurrentHashMap<>();
    
    /**
     * 初始化策略映射
     * <p>
     * 将 Spring 容器中的所有 ShardingStrategy 实现注册到策略映射中
     * 使用懒加载方式，首次调用时初始化
     * </p>
     */
    private void initStrategyMap() {
        if (strategyMap.isEmpty() && shardingStrategies != null) {
            synchronized (strategyMap) {
                if (strategyMap.isEmpty() && shardingStrategies != null) {
                    for (ShardingStrategy strategy : shardingStrategies) {
                        String strategyName = strategy.getStrategyName();
                        strategyMap.put(strategyName, strategy);
                        log.debug("注册分片策略: name={}, class={}", strategyName, strategy.getClass().getName());
                    }
                    log.info("分片策略初始化完成: count={}", strategyMap.size());
                }
            }
        }
    }
    
    /**
     * 根据分片键计算分片索引（使用默认策略）
     * 
     * @param shardingKey 分片键
     * @param shardCount 分片数量
     * @return 分片索引
     */
    @Override
    public int calculateShardIndex(Object shardingKey, int shardCount) {
        return calculateShardIndex(shardingKey, shardCount, "modulo");
    }
    
    /**
     * 根据分片键计算分片索引（使用指定策略）
     * 
     * @param shardingKey 分片键
     * @param shardCount 分片数量
     * @param strategyName 策略名称
     * @return 分片索引
     */
    @Override
    public int calculateShardIndex(Object shardingKey, int shardCount, String strategyName) {
        Assert.notNull(shardingKey, "分片键不能为空");
        Assert.isTrue(shardCount > 0, "分片数量必须大于0");
        
        initStrategyMap();
        
        // 查找指定策略
        ShardingStrategy strategy = strategyMap.get(strategyName);
        if (strategy != null) {
            try {
                return strategy.calculateShardIndex(shardingKey, shardCount);
            } catch (Exception e) {
                log.error("计算分片索引失败: strategy={}, shardingKey={}, shardCount={}", 
                        strategyName, shardingKey, shardCount, e);
                throw new RuntimeException("计算分片索引失败: " + e.getMessage(), e);
            }
        }
        
        // 未找到指定策略，使用默认取模策略
        log.warn("未找到分片策略: {}，使用默认取模策略", strategyName);
        try {
            if (shardingKey instanceof Long) {
                return ShardingUtil.calculateShardIndex((Long) shardingKey, shardCount);
            } else if (shardingKey instanceof String) {
                return ShardingUtil.calculateShardIndex((String) shardingKey, shardCount);
            } else if (shardingKey instanceof Number) {
                return ShardingUtil.calculateShardIndex(((Number) shardingKey).longValue(), shardCount);
            } else {
                return ShardingUtil.calculateShardIndex(shardingKey.toString(), shardCount);
            }
        } catch (Exception e) {
            log.error("使用默认策略计算分片索引失败: shardingKey={}, shardCount={}", 
                    shardingKey, shardCount, e);
            throw new RuntimeException("计算分片索引失败: " + e.getMessage(), e);
        }
    }
    
    /**
     * 生成分片表名
     * 
     * @param tablePrefix 表前缀
     * @param shardingKey 分片键
     * @param shardCount 分片数量
     * @return 分片表名
     */
    @Override
    public String generateShardTableName(String tablePrefix, Object shardingKey, int shardCount) {
        Assert.hasText(tablePrefix, "表前缀不能为空");
        Assert.notNull(shardingKey, "分片键不能为空");
        Assert.isTrue(shardCount > 0, "分片数量必须大于0");
        
        try {
            int shardIndex = calculateShardIndex(shardingKey, shardCount);
            String tableName = ShardingUtil.generateShardTableName(tablePrefix, shardIndex);
            log.debug("生成分片表名: prefix={}, key={}, index={}, tableName={}", 
                    tablePrefix, shardingKey, shardIndex, tableName);
            return tableName;
        } catch (Exception e) {
            log.error("生成分片表名失败: prefix={}, key={}, shardCount={}", 
                    tablePrefix, shardingKey, shardCount, e);
            throw new RuntimeException("生成分片表名失败: " + e.getMessage(), e);
        }
    }
    
    /**
     * 生成分片数据库名
     * 
     * @param dbPrefix 数据库前缀
     * @param shardingKey 分片键
     * @param shardCount 分片数量
     * @return 分片数据库名
     */
    @Override
    public String generateShardDbName(String dbPrefix, Object shardingKey, int shardCount) {
        Assert.hasText(dbPrefix, "数据库前缀不能为空");
        Assert.notNull(shardingKey, "分片键不能为空");
        Assert.isTrue(shardCount > 0, "分片数量必须大于0");
        
        try {
            int shardIndex = calculateShardIndex(shardingKey, shardCount);
            String dbName = ShardingUtil.generateShardDbName(dbPrefix, shardIndex);
            log.debug("生成分片数据库名: prefix={}, key={}, index={}, dbName={}", 
                    dbPrefix, shardingKey, shardIndex, dbName);
            return dbName;
        } catch (Exception e) {
            log.error("生成分片数据库名失败: prefix={}, key={}, shardCount={}", 
                    dbPrefix, shardingKey, shardCount, e);
            throw new RuntimeException("生成分片数据库名失败: " + e.getMessage(), e);
        }
    }
}

