package com.example.sharding.util;

import lombok.extern.slf4j.Slf4j;

/**
 * 分库分表工具类
 * <p>
 * 提供分片计算、表名生成、数据库名生成等静态工具方法
 * 可以在不依赖 Spring 容器的场景下使用
 * </p>
 * <p>
 * 使用示例：
 * <pre>
 * // 计算分片索引
 * int shardIndex = ShardingUtil.calculateShardIndex(userId, 4);
 * 
 * // 生成分片表名
 * String tableName = ShardingUtil.generateShardTableName("t_order", shardIndex);
 * // 结果：t_order_0, t_order_1, ...
 * 
 * // 生成分片数据库名
 * String dbName = ShardingUtil.generateShardDbName("db", shardIndex);
 * // 结果：db0, db1, ...
 * </pre>
 * </p>
 * 
 * @author system
 * @version 1.0.0
 * @since 1.0.0
 */
@Slf4j
public class ShardingUtil {
    
    /**
     * 根据分片键计算分片索引（取模算法，Long 类型）
     * <p>
     * 使用取模算法计算分片索引：shardIndex = Math.abs(shardingKey) % shardCount
     * </p>
     * 
     * @param shardingKey 分片键（Long 类型）
     * @param shardCount 分片数量，必须大于 0
     * @return 分片索引，范围 [0, shardCount-1]
     * @throws IllegalArgumentException 如果 shardCount <= 0
     */
    public static int calculateShardIndex(long shardingKey, int shardCount) {
        if (shardCount <= 0) {
            throw new IllegalArgumentException("分片数量必须大于0");
        }
        int shardIndex = (int) (Math.abs(shardingKey) % shardCount);
        log.debug("计算分片索引: key={}, shardCount={}, shardIndex={}", shardingKey, shardCount, shardIndex);
        return shardIndex;
    }
    
    /**
     * 根据分片键计算分片索引（取模算法，String 类型）
     * <p>
     * 先计算字符串的 hashCode，再取模：shardIndex = Math.abs(hashCode) % shardCount
     * </p>
     * 
     * @param shardingKey 分片键（String 类型），不能为 null 或空字符串
     * @param shardCount 分片数量，必须大于 0
     * @return 分片索引，范围 [0, shardCount-1]
     * @throws IllegalArgumentException 如果 shardingKey 为 null 或空，或 shardCount <= 0
     */
    public static int calculateShardIndex(String shardingKey, int shardCount) {
        if (shardingKey == null || shardingKey.isEmpty()) {
            throw new IllegalArgumentException("分片键不能为空");
        }
        if (shardCount <= 0) {
            throw new IllegalArgumentException("分片数量必须大于0");
        }
        int hashCode = shardingKey.hashCode();
        int shardIndex = Math.abs(hashCode) % shardCount;
        log.debug("计算分片索引: key={}, hashCode={}, shardCount={}, shardIndex={}", 
                shardingKey, hashCode, shardCount, shardIndex);
        return shardIndex;
    }
    
    /**
     * 根据分片键计算分片索引（取模算法，int 类型）
     * <p>
     * 使用取模算法计算分片索引：shardIndex = Math.abs(shardingKey) % shardCount
     * </p>
     * 
     * @param shardingKey 分片键（int 类型）
     * @param shardCount 分片数量，必须大于 0
     * @return 分片索引，范围 [0, shardCount-1]
     * @throws IllegalArgumentException 如果 shardCount <= 0
     */
    public static int calculateShardIndex(int shardingKey, int shardCount) {
        if (shardCount <= 0) {
            throw new IllegalArgumentException("分片数量必须大于0");
        }
        int shardIndex = Math.abs(shardingKey) % shardCount;
        log.debug("计算分片索引: key={}, shardCount={}, shardIndex={}", shardingKey, shardCount, shardIndex);
        return shardIndex;
    }
    
    /**
     * 生成分片表名（带下划线分隔符）
     * <p>
     * 格式：{tablePrefix}_{shardIndex}
     * 示例：t_order_0, t_order_1, t_user_2
     * </p>
     * 
     * @param tablePrefix 表前缀，如 "t_order"、"t_user"，不能为 null 或空字符串
     * @param shardIndex 分片索引，必须 >= 0
     * @return 分片表名，格式：{tablePrefix}_{shardIndex}
     * @throws IllegalArgumentException 如果参数无效
     */
    public static String generateShardTableName(String tablePrefix, int shardIndex) {
        if (tablePrefix == null || tablePrefix.isEmpty()) {
            throw new IllegalArgumentException("表前缀不能为空");
        }
        if (shardIndex < 0) {
            throw new IllegalArgumentException("分片索引不能为负数");
        }
        String tableName = tablePrefix + "_" + shardIndex;
        log.debug("生成分片表名: prefix={}, index={}, tableName={}", tablePrefix, shardIndex, tableName);
        return tableName;
    }
    
    /**
     * 生成分片表名（自定义分隔符）
     * <p>
     * 格式：{tablePrefix}{separator}{shardIndex}
     * 示例：如果 separator 为 "-"，则生成 t_order-0, t_order-1
     * </p>
     * 
     * @param tablePrefix 表前缀，不能为 null 或空字符串
     * @param shardIndex 分片索引，必须 >= 0
     * @param separator 分隔符，如 "_"、"-"、""
     * @return 分片表名，格式：{tablePrefix}{separator}{shardIndex}
     * @throws IllegalArgumentException 如果参数无效
     */
    public static String generateShardTableName(String tablePrefix, int shardIndex, String separator) {
        if (tablePrefix == null || tablePrefix.isEmpty()) {
            throw new IllegalArgumentException("表前缀不能为空");
        }
        if (shardIndex < 0) {
            throw new IllegalArgumentException("分片索引不能为负数");
        }
        if (separator == null) {
            separator = "_";
        }
        String tableName = tablePrefix + separator + shardIndex;
        log.debug("生成分片表名: prefix={}, index={}, separator={}, tableName={}", 
                tablePrefix, shardIndex, separator, tableName);
        return tableName;
    }
    
    /**
     * 生成分片数据库名（无分隔符）
     * <p>
     * 格式：{dbPrefix}{shardIndex}
     * 示例：db0, db1, db2
     * </p>
     * 
     * @param dbPrefix 数据库前缀，如 "db"、"shard_db"，不能为 null 或空字符串
     * @param shardIndex 分片索引，必须 >= 0
     * @return 分片数据库名，格式：{dbPrefix}{shardIndex}
     * @throws IllegalArgumentException 如果参数无效
     */
    public static String generateShardDbName(String dbPrefix, int shardIndex) {
        if (dbPrefix == null || dbPrefix.isEmpty()) {
            throw new IllegalArgumentException("数据库前缀不能为空");
        }
        if (shardIndex < 0) {
            throw new IllegalArgumentException("分片索引不能为负数");
        }
        String dbName = dbPrefix + shardIndex;
        log.debug("生成分片数据库名: prefix={}, index={}, dbName={}", dbPrefix, shardIndex, dbName);
        return dbName;
    }
    
    /**
     * 生成分片数据库名（带下划线分隔符）
     * <p>
     * 格式：{dbPrefix}_{shardIndex}
     * 示例：db_0, db_1, db_2
     * </p>
     * 
     * @param dbPrefix 数据库前缀，不能为 null 或空字符串
     * @param shardIndex 分片索引，必须 >= 0
     * @return 分片数据库名，格式：{dbPrefix}_{shardIndex}
     * @throws IllegalArgumentException 如果参数无效
     */
    public static String generateShardDbNameWithSeparator(String dbPrefix, int shardIndex) {
        if (dbPrefix == null || dbPrefix.isEmpty()) {
            throw new IllegalArgumentException("数据库前缀不能为空");
        }
        if (shardIndex < 0) {
            throw new IllegalArgumentException("分片索引不能为负数");
        }
        String dbName = dbPrefix + "_" + shardIndex;
        log.debug("生成分片数据库名: prefix={}, index={}, dbName={}", dbPrefix, shardIndex, dbName);
        return dbName;
    }
    
    /**
     * 根据用户ID计算分片索引（适用于按用户分片）
     * <p>
     * 这是一个便捷方法，专门用于按用户ID分片的场景
     * </p>
     * 
     * @param userId 用户ID，不能为 null
     * @param shardCount 分片数量，必须大于 0
     * @return 分片索引，范围 [0, shardCount-1]
     * @throws IllegalArgumentException 如果 userId 为 null 或 shardCount <= 0
     */
    public static int calculateShardIndexByUserId(Long userId, int shardCount) {
        if (userId == null) {
            throw new IllegalArgumentException("用户ID不能为空");
        }
        return calculateShardIndex(userId, shardCount);
    }
    
    /**
     * 根据订单ID计算分片索引（适用于按订单分片）
     * <p>
     * 这是一个便捷方法，专门用于按订单ID分片的场景
     * </p>
     * 
     * @param orderId 订单ID，不能为 null
     * @param shardCount 分片数量，必须大于 0
     * @return 分片索引，范围 [0, shardCount-1]
     * @throws IllegalArgumentException 如果 orderId 为 null 或 shardCount <= 0
     */
    public static int calculateShardIndexByOrderId(Long orderId, int shardCount) {
        if (orderId == null) {
            throw new IllegalArgumentException("订单ID不能为空");
        }
        return calculateShardIndex(orderId, shardCount);
    }
}

