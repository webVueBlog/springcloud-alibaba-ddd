package com.example.transaction.annotation;

import com.example.transaction.config.SeataConfig;
import org.springframework.context.annotation.Import;

import java.lang.annotation.*;

/**
 * 启用分布式事务支持
 * <p>
 * 在启动类或配置类上使用此注解以启用 Seata 分布式事务
 * 此注解会自动导入 {@link SeataConfig} 配置类，启用 Seata 的自动数据源代理
 * </p>
 * <p>
 * 使用前提：
 * <ul>
 *   <li>需要在 application.yml 中配置 Seata 相关属性</li>
 *   <li>需要启动 Seata Server</li>
 *   <li>需要配置 Nacos（用于 Seata 的配置和注册）</li>
 * </ul>
 * </p>
 * <p>
 * 使用示例：
 * <pre>
 * &#64;SpringBootApplication
 * &#64;EnableDistributedTransaction
 * public class Application {
 *     public static void main(String[] args) {
 *         SpringApplication.run(Application.class, args);
 *     }
 * }
 * </pre>
 * </p>
 * 
 * @author system
 * @version 1.0.0
 * @since 1.0.0
 * @see SeataConfig
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Import(SeataConfig.class)
public @interface EnableDistributedTransaction {
}

