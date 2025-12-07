# 分布式追踪组件 (Component Trace)

## 概述

Component Trace 是一个分布式追踪组件，提供跨服务链路追踪和日志关联功能。支持基于 MDC 的简单实现和 Spring Cloud Sleuth 集成，适用于微服务架构中的请求链路追踪、日志关联和问题排查。

## 功能特性

### 核心功能

- **TraceId 管理**：追踪ID，用于标识一次完整的请求链路
- **SpanId 管理**：Span ID，用于标识请求链路中的一个操作
- **Span 管理**：支持创建和结束 Span，形成树形结构
- **标签管理**：为 Span 添加标签，用于记录额外的元数据
- **日志关联**：自动在日志中包含追踪信息

### 技术特性

- 基于 SLF4J MDC，自动在日志中包含追踪信息
- 支持 Span 嵌套，形成树形结构
- 线程安全，使用 ThreadLocal 存储 Span 栈
- 支持 Spring Cloud Sleuth 集成
- 轻量级，无需额外依赖（基础实现）

## 技术栈

- Spring Boot 2.7.18
- SLF4J / Logback
- Lombok 1.18.28
- Spring Cloud Sleuth（可选）

## 快速开始

### 1. 添加依赖

在需要使用追踪组件的服务中，添加以下依赖：

```xml
<dependency>
    <groupId>com.example</groupId>
    <artifactId>component-trace</artifactId>
    <version>1.0.0</version>
</dependency>
```

### 2. 配置日志

在 `logback-spring.xml` 中配置日志格式，包含追踪信息：

```xml
<configuration>
    <appender name="CONSOLE" class="ch.qos.logback.core.ConsoleAppender">
        <encoder>
            <pattern>%d{yyyy-MM-dd HH:mm:ss.SSS} [%thread] %-5level [%X{traceId},%X{spanId}] %logger{36} - %msg%n</pattern>
        </encoder>
    </appender>
    
    <root level="INFO">
        <appender-ref ref="CONSOLE" />
    </root>
</configuration>
```

### 3. 启用组件

确保 Spring Boot 能够扫描到追踪组件：

```java
@SpringBootApplication
@ComponentScan(basePackages = {"com.example.yourservice", "com.example.trace"})
public class YourApplication {
    public static void main(String[] args) {
        SpringApplication.run(YourApplication.class, args);
    }
}
```

### 4. 使用追踪服务

```java
@Service
@RequiredArgsConstructor
public class UserService {
    
    private final TraceService traceService;
    
    public User getUser(Long userId) {
        // 创建 Span
        TraceContext span = traceService.createSpan("user-service.getUser");
        try {
            // 添加标签
            traceService.addTag("userId", String.valueOf(userId));
            
            // 执行业务逻辑
            return userRepository.findById(userId);
        } finally {
            // 结束 Span
            traceService.finishSpan();
        }
    }
}
```

## 使用指南

### 获取追踪信息

#### 获取 TraceId

```java
@Service
@RequiredArgsConstructor
public class OrderService {
    
    private final TraceService traceService;
    
    public void processOrder(Order order) {
        // 获取当前 TraceId
        String traceId = traceService.getTraceId();
        log.info("处理订单: orderId={}, traceId={}", order.getId(), traceId);
        
        // 执行业务逻辑
        // ...
    }
}
```

#### 获取 SpanId

```java
@Service
@RequiredArgsConstructor
public class OrderService {
    
    private final TraceService traceService;
    
    public void processOrder(Order order) {
        // 获取当前 SpanId
        String spanId = traceService.getSpanId();
        log.info("处理订单: orderId={}, spanId={}", order.getId(), spanId);
        
        // 执行业务逻辑
        // ...
    }
}
```

### 创建和管理 Span

#### 基本用法

```java
@Service
@RequiredArgsConstructor
public class UserService {
    
    private final TraceService traceService;
    
    public User getUser(Long userId) {
        // 创建 Span
        TraceContext span = traceService.createSpan("user-service.getUser");
        try {
            // 执行业务逻辑
            return userRepository.findById(userId);
        } finally {
            // 结束 Span（确保在 finally 中调用）
            traceService.finishSpan();
        }
    }
}
```

#### 嵌套 Span

```java
@Service
@RequiredArgsConstructor
public class OrderService {
    
    private final TraceService traceService;
    private final UserService userService;
    
    public Order createOrder(OrderDTO orderDTO) {
        // 创建根 Span
        TraceContext rootSpan = traceService.createSpan("order-service.createOrder");
        try {
            // 添加标签
            traceService.addTag("orderType", orderDTO.getOrderType());
            
            // 创建嵌套 Span
            TraceContext userSpan = traceService.createSpan("order-service.getUser");
            try {
                User user = userService.getUser(orderDTO.getUserId());
            } finally {
                traceService.finishSpan();
            }
            
            // 创建另一个嵌套 Span
            TraceContext productSpan = traceService.createSpan("order-service.getProduct");
            try {
                Product product = productService.getProduct(orderDTO.getProductId());
            } finally {
                traceService.finishSpan();
            }
            
            // 创建订单
            Order order = new Order();
            // ...
            return orderRepository.save(order);
        } finally {
            // 结束根 Span
            traceService.finishSpan();
        }
    }
}
```

### 添加标签

```java
@Service
@RequiredArgsConstructor
public class OrderService {
    
    private final TraceService traceService;
    
    public Order createOrder(OrderDTO orderDTO) {
        TraceContext span = traceService.createSpan("order-service.createOrder");
        try {
            // 添加标签
            traceService.addTag("userId", String.valueOf(orderDTO.getUserId()));
            traceService.addTag("orderType", orderDTO.getOrderType());
            traceService.addTag("amount", String.valueOf(orderDTO.getAmount()));
            
            // 执行业务逻辑
            // ...
        } finally {
            traceService.finishSpan();
        }
    }
}
```

### 使用工具类

```java
public class TraceHelper {
    
    /**
     * 获取当前 TraceId（静态方法）
     */
    public static String getCurrentTraceId() {
        return TraceUtil.getTraceId();
    }
    
    /**
     * 设置 TraceId（静态方法）
     */
    public static void setTraceId(String traceId) {
        TraceUtil.setTraceId(traceId);
    }
    
    /**
     * 生成新的 TraceId
     */
    public static String generateTraceId() {
        return TraceUtil.generateTraceId();
    }
}
```

## 完整示例

### 示例1：在 Controller 中创建 Span

```java
@RestController
@RequestMapping("/api/user")
@RequiredArgsConstructor
@Slf4j
public class UserController {
    
    private final UserService userService;
    private final TraceService traceService;
    
    @GetMapping("/{id}")
    public Result<User> getUser(@PathVariable Long id) {
        // 创建 Span
        TraceContext span = traceService.createSpan("user-controller.getUser");
        try {
            // 添加标签
            traceService.addTag("userId", String.valueOf(id));
            traceService.addTag("endpoint", "/api/user/{id}");
            
            // 调用服务
            User user = userService.getUser(id);
            
            return Result.success(user);
        } finally {
            traceService.finishSpan();
        }
    }
}
```

### 示例2：在 Service 中使用追踪

```java
@Service
@RequiredArgsConstructor
@Slf4j
public class OrderService {
    
    private final TraceService traceService;
    private final OrderRepository orderRepository;
    private final UserService userService;
    
    public Order createOrder(OrderDTO orderDTO) {
        // 创建 Span
        TraceContext span = traceService.createSpan("order-service.createOrder");
        try {
            // 添加标签
            traceService.addTag("userId", String.valueOf(orderDTO.getUserId()));
            traceService.addTag("orderType", orderDTO.getOrderType());
            
            // 获取用户信息（嵌套 Span）
            TraceContext userSpan = traceService.createSpan("order-service.getUser");
            try {
                User user = userService.getUser(orderDTO.getUserId());
                traceService.addTag("userName", user.getUsername());
            } finally {
                traceService.finishSpan();
            }
            
            // 创建订单
            Order order = new Order();
            order.setUserId(orderDTO.getUserId());
            order.setAmount(orderDTO.getAmount());
            order = orderRepository.save(order);
            
            log.info("订单创建成功: orderId={}, traceId={}", 
                    order.getId(), traceService.getTraceId());
            
            return order;
        } finally {
            traceService.finishSpan();
        }
    }
}
```

### 示例3：在拦截器中设置 TraceId

```java
@Component
@RequiredArgsConstructor
@Slf4j
public class TraceInterceptor implements HandlerInterceptor {
    
    private final TraceService traceService;
    
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        // 从请求头获取 TraceId（如果存在）
        String traceId = request.getHeader("X-Trace-Id");
        
        if (StringUtils.hasText(traceId)) {
            // 使用传入的 TraceId
            TraceUtil.setTraceId(traceId);
        } else {
            // 生成新的 TraceId
            traceId = TraceUtil.generateTraceId();
            TraceUtil.setTraceId(traceId);
        }
        
        // 将 TraceId 添加到响应头
        response.setHeader("X-Trace-Id", traceId);
        
        log.debug("设置 TraceId: {}", traceId);
        return true;
    }
    
    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) {
        // 清理追踪信息
        TraceUtil.clearTrace();
    }
}
```

### 示例4：在异步任务中传递追踪信息

```java
@Service
@RequiredArgsConstructor
@Slf4j
public class AsyncService {
    
    private final TraceService traceService;
    
    @Async
    public void processAsync(String data) {
        // 在异步任务中，需要手动设置 TraceId
        String traceId = traceService.getTraceId();
        if (traceId != null) {
            TraceUtil.setTraceId(traceId);
        }
        
        try {
            log.info("处理异步任务: data={}, traceId={}", data, traceId);
            // 执行业务逻辑
            // ...
        } finally {
            TraceUtil.clearTrace();
        }
    }
}
```

## 配置说明

### 日志配置

在 `logback-spring.xml` 中配置日志格式，包含追踪信息：

```xml
<configuration>
    <appender name="CONSOLE" class="ch.qos.logback.core.ConsoleAppender">
        <encoder>
            <!-- 使用 %X{traceId} 和 %X{spanId} 输出追踪信息 -->
            <pattern>%d{yyyy-MM-dd HH:mm:ss.SSS} [%thread] %-5level [%X{traceId},%X{spanId}] %logger{36} - %msg%n</pattern>
        </encoder>
    </appender>
    
    <root level="INFO">
        <appender-ref ref="CONSOLE" />
    </root>
</configuration>
```

### Sleuth 集成（可选）

如果需要使用 Spring Cloud Sleuth，添加依赖：

```xml
<dependency>
    <groupId>org.springframework.cloud</groupId>
    <artifactId>spring-cloud-starter-sleuth</artifactId>
</dependency>
```

在 `application.yml` 中配置：

```yaml
spring:
  sleuth:
    enabled: true
    sampler:
      probability: 1.0  # 采样率，1.0 表示 100% 采样
```

## 最佳实践

### 1. 在请求入口设置 TraceId

在拦截器或过滤器中设置 TraceId，确保整个请求链路使用相同的 TraceId：

```java
@Override
public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
    String traceId = request.getHeader("X-Trace-Id");
    if (!StringUtils.hasText(traceId)) {
        traceId = TraceUtil.generateTraceId();
    }
    TraceUtil.setTraceId(traceId);
    response.setHeader("X-Trace-Id", traceId);
    return true;
}
```

### 2. 使用 try-finally 确保 Span 正确结束

```java
TraceContext span = traceService.createSpan("operation");
try {
    // 执行业务逻辑
} finally {
    traceService.finishSpan();  // 确保在 finally 中调用
}
```

### 3. 在服务间传递 TraceId

在 HTTP 请求头中传递 TraceId：

```java
@Service
@RequiredArgsConstructor
public class OrderService {
    
    private final RestTemplate restTemplate;
    private final TraceService traceService;
    
    public User getUser(Long userId) {
        // 获取当前 TraceId
        String traceId = traceService.getTraceId();
        
        // 在请求头中传递 TraceId
        HttpHeaders headers = new HttpHeaders();
        headers.set("X-Trace-Id", traceId);
        HttpEntity<?> entity = new HttpEntity<>(headers);
        
        // 发送请求
        return restTemplate.exchange(
            "http://user-service/api/user/" + userId,
            HttpMethod.GET,
            entity,
            User.class
        ).getBody();
    }
}
```

### 4. 在异步任务中传递追踪信息

```java
@Async
public void processAsync(String data) {
    // 获取当前 TraceId
    String traceId = TraceUtil.getTraceId();
    
    // 设置到异步线程的 MDC
    TraceUtil.setTraceId(traceId);
    
    try {
        // 执行业务逻辑
    } finally {
        TraceUtil.clearTrace();
    }
}
```

### 5. 添加有意义的标签

```java
// ✅ 推荐：添加有意义的标签
traceService.addTag("userId", String.valueOf(userId));
traceService.addTag("orderType", orderType);
traceService.addTag("operation", "createOrder");

// ❌ 不推荐：添加无意义的标签
traceService.addTag("data", "some data");
```

### 6. 清理追踪信息

在请求处理完成后清理追踪信息，避免泄露到其他请求：

```java
@Override
public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) {
    TraceUtil.clearTrace();
}
```

## 常见问题

### 1. TraceId 在日志中不显示？

**答**：检查以下几点：
- 日志配置中是否包含 `%X{traceId}`
- 是否在请求入口设置了 TraceId
- MDC 是否正确配置

### 2. 如何在服务间传递 TraceId？

**答**：
- 在 HTTP 请求头中传递：`X-Trace-Id: {traceId}`
- 在消息队列的消息头中传递
- 在 RPC 调用的上下文中传递

### 3. 异步任务中 TraceId 丢失？

**答**：在异步任务开始时，手动设置 TraceId：

```java
@Async
public void processAsync(String data) {
    String traceId = TraceUtil.getTraceId();
    TraceUtil.setTraceId(traceId);
    try {
        // 执行业务逻辑
    } finally {
        TraceUtil.clearTrace();
    }
}
```

### 4. 如何与 Sleuth 集成？

**答**：
- 添加 `spring-cloud-starter-sleuth` 依赖
- 在 `application.yml` 中配置 `spring.sleuth.enabled: true`
- 组件会自动使用 `SleuthTraceService`

### 5. Span 栈溢出？

**答**：确保每个 `createSpan()` 都有对应的 `finishSpan()`，使用 try-finally 确保正确结束。

### 6. 如何自定义 TraceId 格式？

**答**：可以通过 `TraceUtil.generateTraceId()` 方法自定义生成逻辑，或使用 `TraceUtil.setTraceId()` 设置自定义的 TraceId。

## 版本历史

- **v1.0.0** (2024-01-01)
  - 初始版本
  - 提供 TraceId 和 SpanId 管理功能
  - 提供 Span 创建和结束功能
  - 提供标签管理功能
  - 支持基于 MDC 的简单实现
  - 支持 Spring Cloud Sleuth 集成

## 许可证

本项目采用 MIT 许可证。

## 联系方式

如有问题或建议，请联系开发团队。

