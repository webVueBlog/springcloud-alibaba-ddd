package com.example.sharding.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 分片键注解
 * <p>
 * 用于标识实体类中的分片字段，标记该字段作为分片键使用
 * </p>
 * <p>
 * 使用场景：
 * <ul>
 *   <li>在实体类中标记分片字段，如 userId、orderId 等</li>
 *   <li>配合分片服务使用，自动计算分片索引</li>
 *   <li>用于代码文档，明确标识分片字段</li>
 * </ul>
 * </p>
 * <p>
 * 使用示例：
 * <pre>
 * public class Order {
 *     @ShardingKey(strategy = "modulo")
 *     private Long orderId;
 *     
 *     private String orderNo;
 *     // ...
 * }
 * </pre>
 * </p>
 * <p>
 * 注意：
 * <ul>
 *   <li>此注解主要用于文档和代码标识，实际分片计算需要通过 ShardingService</li>
 *   <li>可以通过反射获取注解信息，实现自动分片功能</li>
 * </ul>
 * </p>
 * 
 * @author system
 * @version 1.0.0
 * @since 1.0.0
 */
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface ShardingKey {
    /**
     * 分片策略名称
     * <p>
     * 指定该分片键使用的分片策略，如 "modulo"、"hash" 等
     * 如果不指定，使用默认策略
     * </p>
     * 
     * @return 策略名称，默认为 "standard"
     */
    String strategy() default "standard";
}

