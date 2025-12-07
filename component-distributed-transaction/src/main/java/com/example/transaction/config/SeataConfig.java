package com.example.transaction.config;

import io.seata.spring.annotation.datasource.EnableAutoDataSourceProxy;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Configuration;

/**
 * Seata 分布式事务配置类
 * <p>
 * 启用 Seata 的自动数据源代理功能，自动代理所有数据源以支持分布式事务
 * </p>
 * <p>
 * 配置说明：
 * <ul>
 *   <li>只有当 seata.enabled=true 时才会生效（默认不启用）</li>
 *   <li>使用 {@link EnableAutoDataSourceProxy} 启用自动数据源代理</li>
 *   <li>Seata 的具体配置通过 application.yml 进行配置</li>
 * </ul>
 * </p>
 * <p>
 * 需要在 application.yml 中配置以下属性：
 * <pre>
 * seata:
 *   enabled: true
 *   application-id: ${spring.application.name}
 *   tx-service-group: my_test_tx_group
 *   config:
 *     type: nacos
 *     nacos:
 *       server-addr: localhost:8848
 *       group: SEATA_GROUP
 *       namespace: ""
 *   registry:
 *     type: nacos
 *     nacos:
 *       application: seata-server
 *       server-addr: localhost:8848
 *       group: SEATA_GROUP
 *       namespace: ""
 * </pre>
 * </p>
 * 
 * @author system
 * @version 1.0.0
 * @since 1.0.0
 * @see EnableAutoDataSourceProxy
 */
@Configuration
@EnableAutoDataSourceProxy
@ConditionalOnProperty(name = "seata.enabled", havingValue = "true", matchIfMissing = false)
public class SeataConfig {
    // Seata 配置通过 application.yml 进行配置
    // 此配置类主要用于启用 Seata 的自动数据源代理
    // 自动数据源代理会拦截所有数据库操作，自动生成回滚日志
}

