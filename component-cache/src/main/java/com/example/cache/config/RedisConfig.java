package com.example.cache.config;

import com.alibaba.fastjson2.support.spring.data.redis.FastJsonRedisSerializer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.RedisStandaloneConfiguration;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.StringRedisSerializer;

/**
 * Redis 配置类
 * <p>
 * 配置 Redis 连接和 RedisTemplate
 * </p>
 * <p>
 * 配置说明：
 * <ul>
 *   <li>只有当 spring.redis.enabled=true 时才会生效（默认启用）</li>
 *   <li>如果 Spring Boot 的 Redis 自动配置已经创建了 RedisConnectionFactory，则不会重复创建</li>
 *   <li>使用 FastJSON2 作为值的序列化器，支持复杂对象的序列化和反序列化</li>
 *   <li>使用 StringRedisSerializer 作为 key 的序列化器</li>
 * </ul>
 * </p>
 * 
 * @author system
 * @version 1.0.0
 * @since 1.0.0
 */
@Configuration
@ConditionalOnProperty(name = "spring.redis.enabled", havingValue = "true", matchIfMissing = true)
public class RedisConfig {

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
     * 创建 Redis 连接工厂
     * <p>
     * 如果 Spring Boot 的 Redis 自动配置已经创建了 RedisConnectionFactory，则不会重复创建
     * </p>
     * 
     * @return Redis 连接工厂
     */
    @Bean
    public RedisConnectionFactory redisConnectionFactory() {
        RedisStandaloneConfiguration config = new RedisStandaloneConfiguration();
        config.setHostName(host);
        config.setPort(port);
        if (password != null && !password.isEmpty()) {
            config.setPassword(password);
        }
        config.setDatabase(database);
        return new LettuceConnectionFactory(config);
    }

    /**
     * 创建 RedisTemplate
     * <p>
     * 配置说明：
     * <ul>
     *   <li>Key 序列化器：StringRedisSerializer（字符串）</li>
     *   <li>Value 序列化器：FastJsonRedisSerializer（JSON，支持复杂对象）</li>
     *   <li>Hash Key 序列化器：StringRedisSerializer</li>
     *   <li>Hash Value 序列化器：FastJsonRedisSerializer</li>
     * </ul>
     * </p>
     * 
     * @param connectionFactory Redis 连接工厂
     * @return RedisTemplate 实例
     */
    @Bean
    public RedisTemplate<String, Object> redisTemplate(RedisConnectionFactory connectionFactory) {
        RedisTemplate<String, Object> template = new RedisTemplate<>();
        template.setConnectionFactory(connectionFactory);

        // 使用 FastJSON2 序列化器，支持复杂对象
        FastJsonRedisSerializer<Object> serializer = new FastJsonRedisSerializer<>(Object.class);
        // 使用字符串序列化器作为 key 序列化器
        StringRedisSerializer stringSerializer = new StringRedisSerializer();

        // 设置序列化器
        template.setKeySerializer(stringSerializer);
        template.setHashKeySerializer(stringSerializer);
        template.setValueSerializer(serializer);
        template.setHashValueSerializer(serializer);
        template.afterPropertiesSet();

        return template;
    }
}

