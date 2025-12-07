# 监控组件 (Component Monitor)

## 概述

Component Monitor 是一个应用监控组件，提供应用健康检查、指标收集、系统信息等功能。基于 Spring Boot Actuator 实现，支持通过 REST API 获取应用的监控信息。

## 功能特性

### 核心功能

- **健康检查**：提供应用健康状态检查，包括各个组件的健康状态
- **指标收集**：收集 JVM、HTTP、系统等各类指标
- **系统信息**：获取应用信息、Java 信息、操作系统信息
- **REST API**：提供 RESTful API 接口查询监控信息

### 技术特性

- 基于 Spring Boot Actuator
- 支持 JMX 获取 JVM 和系统信息
- 完善的异常处理和日志记录
- 自动配置，开箱即用

## 技术栈

- Spring Boot 2.7.18
- Spring Boot Actuator
- Spring Cloud Alibaba Sentinel（可选）
- Lombok 1.18.28

## 快速开始

### 1. 添加依赖

在需要使用监控组件的服务中，添加以下依赖：

```xml
<dependency>
    <groupId>com.example</groupId>
    <artifactId>component-monitor</artifactId>
    <version>1.0.0</version>
</dependency>
```

### 2. 配置 Actuator

在 `application.yml` 中配置 Actuator 端点：

```yaml
management:
  endpoints:
    web:
      exposure:
        include: health,metrics,info  # 暴露的端点
  endpoint:
    health:
      show-details: when-authorized  # 健康检查详情显示策略
```

### 3. 启用组件

确保 Spring Boot 能够扫描到监控组件：

```java
@SpringBootApplication
@ComponentScan(basePackages = {"com.example.yourservice", "com.example.monitor"})
public class YourApplication {
    public static void main(String[] args) {
        SpringApplication.run(YourApplication.class, args);
    }
}
```

### 4. 使用监控服务

```java
@Service
@RequiredArgsConstructor
public class HealthCheckService {
    
    private final MonitorService monitorService;
    
    public Map<String, Object> checkHealth() {
        return monitorService.getHealth();
    }
}
```

## 使用指南

### 健康检查

#### 通过服务接口获取

```java
@Service
@RequiredArgsConstructor
public class HealthService {
    
    private final MonitorService monitorService;
    
    public boolean isHealthy() {
        Map<String, Object> health = monitorService.getHealth();
        String status = (String) health.get("status");
        return "UP".equals(status);
    }
}
```

#### 通过 REST API 获取

```bash
# 获取健康状态
curl http://localhost:8080/api/monitor/health

# 响应示例
{
  "code": 200,
  "message": "操作成功",
  "data": {
    "status": "UP",
    "components": {
      "db": {
        "status": "UP"
      },
      "diskSpace": {
        "status": "UP"
      }
    }
  }
}
```

### 指标收集

#### 通过服务接口获取

```java
@Service
@RequiredArgsConstructor
public class MetricsService {
    
    private final MonitorService monitorService;
    
    public Map<String, Object> getMetrics() {
        return monitorService.getMetrics();
    }
}
```

#### 通过 REST API 获取

```bash
# 获取应用指标
curl http://localhost:8080/api/monitor/metrics

# 响应示例
{
  "code": 200,
  "message": "操作成功",
  "data": {
    "jvm": {
      "memory": {
        "heapUsed": 104857600,
        "heapMax": 2147483648,
        "heapCommitted": 268435456
      },
      "thread": {
        "threadCount": 25,
        "peakThreadCount": 30
      },
      "runtime": {
        "uptime": 3600000,
        "startTime": 1699000000000
      }
    },
    "http": {
      "COUNT": 1000,
      "TOTAL_TIME": 50000
    },
    "system": {
      "processors": 8,
      "totalMemory": 4294967296,
      "freeMemory": 2147483648,
      "maxMemory": 8589934592
    }
  }
}
```

### 系统信息

#### 通过服务接口获取

```java
@Service
@RequiredArgsConstructor
public class SystemInfoService {
    
    private final MonitorService monitorService;
    
    public Map<String, Object> getSystemInfo() {
        return monitorService.getSystemInfo();
    }
}
```

#### 通过 REST API 获取

```bash
# 获取系统信息
curl http://localhost:8080/api/monitor/info

# 响应示例
{
  "code": 200,
  "message": "操作成功",
  "data": {
    "application": {
      "name": "user-service"
    },
    "java": {
      "version": "1.8.0_281",
      "vendor": "Oracle Corporation",
      "vmName": "Java HotSpot(TM) 64-Bit Server VM"
    },
    "os": {
      "name": "Windows 10",
      "version": "10.0",
      "arch": "amd64"
    }
  }
}
```

## 完整示例

### 示例1：健康检查端点

```java
@RestController
@RequestMapping("/api/monitor")
@RequiredArgsConstructor
@Slf4j
public class MonitorController {
    
    private final MonitorService monitorService;
    
    /**
     * 健康检查接口（用于负载均衡器）
     */
    @GetMapping("/health")
    public Result<Map<String, Object>> getHealth() {
        Map<String, Object> health = monitorService.getHealth();
        String status = (String) health.get("status");
        
        if ("UP".equals(status)) {
            return Result.success(health);
        } else {
            return Result.error(503, "服务不健康");
        }
    }
}
```

### 示例2：指标监控和告警

```java
@Service
@RequiredArgsConstructor
@Slf4j
public class MetricsMonitorService {
    
    private final MonitorService monitorService;
    
    /**
     * 检查 JVM 内存使用率
     */
    @Scheduled(fixedRate = 60000)  // 每分钟检查一次
    public void checkMemoryUsage() {
        Map<String, Object> metrics = monitorService.getMetrics();
        Map<String, Object> jvm = (Map<String, Object>) metrics.get("jvm");
        Map<String, Object> memory = (Map<String, Object>) jvm.get("memory");
        
        Long heapUsed = (Long) memory.get("heapUsed");
        Long heapMax = (Long) memory.get("heapMax");
        double usageRate = (double) heapUsed / heapMax * 100;
        
        if (usageRate > 80) {
            log.warn("JVM 内存使用率过高: {}%", usageRate);
            // 发送告警通知
            // alertService.sendAlert("JVM内存使用率过高: " + usageRate + "%");
        }
    }
    
    /**
     * 检查线程数量
     */
    @Scheduled(fixedRate = 60000)
    public void checkThreadCount() {
        Map<String, Object> metrics = monitorService.getMetrics();
        Map<String, Object> jvm = (Map<String, Object>) metrics.get("jvm");
        Map<String, Object> thread = (Map<String, Object>) jvm.get("thread");
        
        Integer threadCount = (Integer) thread.get("threadCount");
        
        if (threadCount > 200) {
            log.warn("线程数量过多: {}", threadCount);
            // 发送告警通知
        }
    }
}
```

### 示例3：系统信息展示

```java
@RestController
@RequestMapping("/api/admin")
@RequiredArgsConstructor
@Slf4j
public class AdminController {
    
    private final MonitorService monitorService;
    
    /**
     * 获取系统信息（管理后台）
     */
    @GetMapping("/system/info")
    public Result<Map<String, Object>> getSystemInfo() {
        Map<String, Object> info = monitorService.getSystemInfo();
        return Result.success(info);
    }
    
    /**
     * 获取应用指标（管理后台）
     */
    @GetMapping("/system/metrics")
    public Result<Map<String, Object>> getMetrics() {
        Map<String, Object> metrics = monitorService.getMetrics();
        return Result.success(metrics);
    }
}
```

## 配置说明

### Actuator 端点配置

在 `application.yml` 中配置 Actuator：

```yaml
management:
  endpoints:
    web:
      exposure:
        include: health,metrics,info  # 暴露的端点
        exclude: shutdown  # 排除的端点
  endpoint:
    health:
      show-details: when-authorized  # 健康检查详情显示策略
      probes:
        enabled: true  # 启用探针
  metrics:
    export:
      prometheus:
        enabled: true  # 启用 Prometheus 导出（可选）
```

### 端点说明

| 端点 | 说明 | 默认启用 |
|------|------|----------|
| `/actuator/health` | 健康检查 | 是 |
| `/actuator/metrics` | 应用指标 | 是 |
| `/actuator/info` | 应用信息 | 是 |
| `/actuator/env` | 环境变量 | 否 |
| `/actuator/loggers` | 日志配置 | 否 |
| `/actuator/beans` | Spring Bean | 否 |

### 自定义端点

组件提供的自定义端点：

| 端点 | 说明 |
|------|------|
| `/api/monitor/health` | 健康检查（统一响应格式） |
| `/api/monitor/metrics` | 应用指标（统一响应格式） |
| `/api/monitor/info` | 系统信息（统一响应格式） |

## 最佳实践

### 1. 配置访问权限

监控接口应该配置访问权限，避免敏感信息泄露：

```java
@Configuration
public class SecurityConfig {
    
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            .authorizeRequests()
            .antMatchers("/api/monitor/**").hasRole("ADMIN")  // 需要管理员权限
            .antMatchers("/actuator/**").hasRole("ADMIN")
            .anyRequest().permitAll();
        return http.build();
    }
}
```

### 2. 使用健康检查

在负载均衡器或服务注册中心使用健康检查：

```yaml
# Nacos 健康检查
spring:
  cloud:
    nacos:
      discovery:
        health-check-url: http://localhost:8080/api/monitor/health
```

### 3. 定期监控指标

使用定时任务定期检查指标，及时发现问题：

```java
@Scheduled(fixedRate = 60000)  // 每分钟检查一次
public void monitorMetrics() {
    Map<String, Object> metrics = monitorService.getMetrics();
    // 检查指标并发送告警
}
```

### 4. 集成监控系统

将指标导出到 Prometheus、Grafana 等监控系统：

```yaml
management:
  metrics:
    export:
      prometheus:
        enabled: true
```

### 5. 自定义健康检查

实现自定义的健康检查：

```java
@Component
public class CustomHealthIndicator implements HealthIndicator {
    
    @Override
    public Health health() {
        // 检查自定义组件
        if (isCustomComponentHealthy()) {
            return Health.up().withDetail("custom", "正常").build();
        } else {
            return Health.down().withDetail("custom", "异常").build();
        }
    }
    
    private boolean isCustomComponentHealthy() {
        // 自定义健康检查逻辑
        return true;
    }
}
```

## 常见问题

### 1. 监控服务注入为 null？

**答**：检查以下几点：
- Spring Boot 是否能够扫描到 `com.example.monitor` 包
- HealthEndpoint 和 MetricsEndpoint 是否可用（需要 Actuator 依赖）

### 2. 健康检查返回 UNKNOWN？

**答**：可能的原因：
- HealthEndpoint 未正确配置
- 健康检查组件异常
- 检查 Actuator 配置是否正确

### 3. 指标获取失败？

**答**：检查以下几点：
- MetricsEndpoint 是否可用
- 指标名称是否正确
- 是否有权限访问指标

### 4. 如何禁用监控组件？

**答**：不配置 Actuator 端点，或者不扫描 `com.example.monitor` 包。

### 5. 如何自定义监控指标？

**答**：可以通过以下方式：
- 实现自定义 HealthIndicator
- 使用 Micrometer 注册自定义指标
- 在业务代码中记录指标

### 6. 如何集成 Prometheus？

**答**：
1. 添加 Prometheus 依赖
2. 配置 `management.metrics.export.prometheus.enabled=true`
3. 访问 `/actuator/prometheus` 获取指标

## 版本历史

- **v1.0.0** (2024-01-01)
  - 初始版本
  - 提供健康检查功能
  - 提供指标收集功能
  - 提供系统信息查询功能

## 许可证

本项目采用 MIT 许可证。

## 联系方式

如有问题或建议，请联系开发团队。

