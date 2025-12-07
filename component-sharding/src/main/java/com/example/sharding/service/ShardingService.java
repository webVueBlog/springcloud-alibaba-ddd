package com.example.sharding.service;

/**
 * 分片服务接口
 * <p>
 * 提供分库分表的核心功能，包括：
 * <ul>
 *   <li>根据分片键计算分片索引</li>
 *   <li>生成分片表名</li>
 *   <li>生成分片数据库名</li>
 * </ul>
 * </p>
 * <p>
 * 使用场景：
 * <ul>
 *   <li>水平分表：将大表按分片键拆分成多个表</li>
 *   <li>水平分库：将数据按分片键分布到多个数据库</li>
 *   <li>读写分离：结合分片实现读写分离</li>
 * </ul>
 * </p>
 * <p>
 * 使用示例：
 * <pre>
 * // 注入分片服务
 * @Autowired
 * private ShardingService shardingService;
 * 
 * // 计算分片索引
 * int shardIndex = shardingService.calculateShardIndex(userId, 4);
 * 
 * // 生成分片表名
 * String tableName = shardingService.generateShardTableName("t_order", orderId, 8);
 * // 结果：t_order_0, t_order_1, ..., t_order_7
 * 
 * // 生成分片数据库名
 * String dbName = shardingService.generateShardDbName("db", userId, 4);
 * // 结果：db0, db1, db2, db3
 * </pre>
 * </p>
 * 
 * @author system
 * @version 1.0.0
 * @since 1.0.0
 */
public interface ShardingService {
    
    /**
     * 根据分片键计算分片索引
     * <p>
     * 使用默认策略（取模策略）计算分片索引
     * </p>
     * 
     * @param shardingKey 分片键，可以是 Long、String、Number 等类型
     * @param shardCount 分片数量，必须大于 0
     * @return 分片索引，范围 [0, shardCount-1]
     * @throws IllegalArgumentException 如果 shardCount <= 0 或 shardingKey 为 null
     */
    int calculateShardIndex(Object shardingKey, int shardCount);
    
    /**
     * 根据分片键计算分片索引（使用指定策略）
     * <p>
     * 支持多种分片策略：
     * <ul>
     *   <li>modulo：取模策略（默认）</li>
     *   <li>hash：哈希策略</li>
     * </ul>
     * </p>
     * 
     * @param shardingKey 分片键，可以是 Long、String、Number 等类型
     * @param shardCount 分片数量，必须大于 0
     * @param strategyName 策略名称，如 "modulo"、"hash"
     * @return 分片索引，范围 [0, shardCount-1]
     * @throws IllegalArgumentException 如果 shardCount <= 0 或 shardingKey 为 null
     */
    int calculateShardIndex(Object shardingKey, int shardCount, String strategyName);
    
    /**
     * 生成分片表名
     * <p>
     * 根据表前缀、分片键和分片数量生成分片表名
     * 格式：{tablePrefix}_{shardIndex}
     * </p>
     * <p>
     * 示例：
     * <pre>
     * generateShardTableName("t_order", 12345L, 8)
     * // 假设 12345 % 8 = 1，返回 "t_order_1"
     * </pre>
     * </p>
     * 
     * @param tablePrefix 表前缀，如 "t_order"、"t_user"
     * @param shardingKey 分片键，用于计算分片索引
     * @param shardCount 分片数量，必须大于 0
     * @return 分片表名，格式：{tablePrefix}_{shardIndex}
     * @throws IllegalArgumentException 如果参数无效
     */
    String generateShardTableName(String tablePrefix, Object shardingKey, int shardCount);
    
    /**
     * 生成分片数据库名
     * <p>
     * 根据数据库前缀、分片键和分片数量生成分片数据库名
     * 格式：{dbPrefix}{shardIndex}
     * </p>
     * <p>
     * 示例：
     * <pre>
     * generateShardDbName("db", 12345L, 4)
     * // 假设 12345 % 4 = 1，返回 "db1"
     * </pre>
     * </p>
     * 
     * @param dbPrefix 数据库前缀，如 "db"、"shard_db"
     * @param shardingKey 分片键，用于计算分片索引
     * @param shardCount 分片数量，必须大于 0
     * @return 分片数据库名，格式：{dbPrefix}{shardIndex}
     * @throws IllegalArgumentException 如果参数无效
     */
    String generateShardDbName(String dbPrefix, Object shardingKey, int shardCount);
}

