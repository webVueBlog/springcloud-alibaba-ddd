package com.example.lock.config;

import org.redisson.Redisson;
import org.redisson.api.RedissonClient;
import org.redisson.config.Config;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Redisson 配置类（用于分布式锁组件）
 * <p>
 * 配置 Redisson 客户端，用于实现分布式锁功能
 * </p>
 * <p>
 * 配置说明：
 * <ul>
 *   <li>只有当 spring.redis.enabled=true 时才会生效（默认启用）</li>
 *   <li>优先使用 redisson.address 配置，如果没有配置则使用 spring.redis 配置</li>
 *   <li>支持单机模式和集群模式（当前实现为单机模式）</li>
 * </ul>
 * </p>
 * 
 * @author system
 * @version 1.0.0
 * @since 1.0.0
 */
@Configuration
@ConditionalOnProperty(name = "spring.redis.enabled", havingValue = "true", matchIfMissing = true)
public class RedissonConfig {

    /** Redisson 连接地址（可选，格式：redis://host:port） */
    @Value("${redisson.address:}")
    private String redissonAddress;

    /** Redis 主机地址，默认 localhost */
    @Value("${spring.redis.host:localhost}")
    private String host;

    /** Redis 端口，默认 6379 */
    @Value("${spring.redis.port:6379}")
    private int port;

    /** Redis 密码，默认空 */
    @Value("${spring.redis.password:}")
    private String password;

    /** Redis 数据库索引，默认 0 */
    @Value("${spring.redis.database:0}")
    private int database;

    /**
     * 创建 Redisson 客户端
     * <p>
     * 配置说明：
     * <ul>
     *   <li>优先使用 redisson.address 配置，如果没有配置则使用 spring.redis 配置</li>
     *   <li>使用单机模式（SingleServerConfig）</li>
     *   <li>如果配置了密码，则设置密码</li>
     * </ul>
     * </p>
     * 
     * @return Redisson 客户端实例
     */
    @Bean
    public RedissonClient redissonClient() {
        Config config = new Config();
        String address;
        
        // 优先使用 redisson.address，如果没有配置则使用 spring.redis 配置
        if (redissonAddress != null && !redissonAddress.isEmpty()) {
            address = redissonAddress;
        } else {
            address = "redis://" + host + ":" + port;
        }
        
        // 配置单机模式
        config.useSingleServer()
                .setAddress(address)
                .setDatabase(database);
        
        // 如果配置了密码，则设置密码
        if (password != null && !password.isEmpty()) {
            config.useSingleServer().setPassword(password);
        }
        
        return Redisson.create(config);
    }
}

