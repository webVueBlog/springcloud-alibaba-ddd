# 限流组件 (Component Rate Limit)

## 概述

Component Rate Limit 是一个限流组件，提供方法级别的限流控制功能。支持令牌桶算法和滑动窗口算法，基于缓存服务（Redis 或 Caffeine）实现，适用于 API 接口限流、用户行为限流等场景。

## 功能特性

### 核心功能

- **注解式限流**：使用 `@RateLimit` 注解轻松实现方法级别限流
- **令牌桶算法**：支持平滑限流，允许突发流量
- **滑动窗口算法**：支持固定时间窗口内的请求数限制
- **自动 key 生成**：自动使用 URI + IP 或方法签名作为限流 key
- **SpEL 表达式支持**：支持在 key 中使用 SpEL 表达式

### 技术特性

- 基于 AOP 实现，无侵入
- 支持 Redis 和 Caffeine 缓存
- 完善的异常处理和日志记录
- 自动配置，开箱即用

## 技术栈

- Spring Boot 2.7.18
- Spring AOP
- AspectJ
- Component Cache（Redis/Caffeine）
- Lombok 1.18.28

## 快速开始

### 1. 添加依赖

在需要使用限流组件的服务中，添加以下依赖：

```xml
<dependency>
    <groupId>com.example</groupId>
    <artifactId>component-rate-limit</artifactId>
    <version>1.0.0</version>
</dependency>
```

### 2. 配置缓存服务

限流组件依赖缓存服务，需要配置 Redis 或 Caffeine：

```yaml
# 方式1：使用 Redis（推荐用于分布式环境）
spring:
  redis:
    enabled: true
    host: localhost
    port: 6379

# 方式2：使用 Caffeine（推荐用于单机环境）
cache:
  local:
    enabled: true
```

### 3. 启用组件

确保 Spring Boot 能够扫描到限流组件：

```java
@SpringBootApplication
@ComponentScan(basePackages = {"com.example.yourservice", "com.example.ratelimit"})
public class YourApplication {
    public static void main(String[] args) {
        SpringApplication.run(YourApplication.class, args);
    }
}
```

### 4. 使用限流注解

```java
@RestController
@RequestMapping("/api/user")
public class UserController {
    
    @RateLimit(limit = 100, window = 60)  // 每分钟最多100次请求
    @PostMapping("/login")
    public Result<String> login(@RequestBody LoginDTO loginDTO) {
        // 登录逻辑
    }
}
```

## 使用指南

### 基本使用

#### 方法级别限流

```java
@RestController
@RequestMapping("/api/order")
public class OrderController {
    
    /**
     * 限制每个IP每分钟最多100次请求
     */
    @RateLimit(limit = 100, window = 60)
    @PostMapping
    public Result<Order> createOrder(@RequestBody OrderDTO orderDTO) {
        // 创建订单逻辑
        return Result.success(order);
    }
    
    /**
     * 限制每个IP每小时最多1000次请求
     */
    @RateLimit(limit = 1000, window = 3600)
    @GetMapping("/list")
    public Result<List<Order>> getOrderList() {
        // 查询订单列表
        return Result.success(orders);
    }
}
```

#### 指定限流 key

```java
@RestController
@RequestMapping("/api/user")
public class UserController {
    
    /**
     * 限制每个用户每分钟最多10次登录请求
     * 注意：需要支持 SpEL 表达式解析
     */
    @RateLimit(key = "login:#{#loginDTO.userId}", limit = 10, window = 60)
    @PostMapping("/login")
    public Result<String> login(@RequestBody LoginDTO loginDTO) {
        // 登录逻辑
    }
    
    /**
     * 使用固定 key
     */
    @RateLimit(key = "api:register", limit = 50, window = 60)
    @PostMapping("/register")
    public Result<String> register(@RequestBody RegisterDTO registerDTO) {
        // 注册逻辑
    }
}
```

### 编程式限流

#### 使用令牌桶算法

```java
@Service
@RequiredArgsConstructor
public class OrderService {
    
    private final RateLimitService rateLimitService;
    
    public void createOrder(OrderDTO orderDTO) {
        // 尝试获取令牌：key=用户ID, permits=1, rate=10/秒, capacity=100
        boolean allowed = rateLimitService.tryAcquire(
            "user:" + orderDTO.getUserId(), 
            1, 
            10, 
            100
        );
        
        if (!allowed) {
            throw new BusinessException(429, "请求过于频繁，请稍后再试");
        }
        
        // 创建订单逻辑
    }
}
```

#### 使用滑动窗口算法

```java
@Service
@RequiredArgsConstructor
public class UserService {
    
    private final RateLimitService rateLimitService;
    
    public void sendVerifyCode(String phone) {
        // 限制每个手机号每分钟最多发送3次验证码
        boolean allowed = rateLimitService.slidingWindowLimit(
            "verify_code:" + phone,
            3,
            60
        );
        
        if (!allowed) {
            throw new BusinessException(429, "验证码发送过于频繁，请稍后再试");
        }
        
        // 发送验证码逻辑
    }
}
```

## 完整示例

### 示例1：API 接口限流

```java
@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {
    
    private final AuthService authService;
    
    /**
     * 登录接口：限制每个IP每分钟最多10次请求
     */
    @RateLimit(limit = 10, window = 60)
    @PostMapping("/login")
    public Result<LoginResultDTO> login(@RequestBody LoginDTO loginDTO) {
        LoginResultDTO result = authService.login(loginDTO);
        return Result.success(result);
    }
    
    /**
     * 注册接口：限制每个IP每小时最多5次请求
     */
    @RateLimit(limit = 5, window = 3600)
    @PostMapping("/register")
    public Result<String> register(@RequestBody RegisterDTO registerDTO) {
        authService.register(registerDTO);
        return Result.success("注册成功");
    }
    
    /**
     * 发送验证码：限制每个手机号每分钟最多1次请求
     */
    @RateLimit(key = "verify_code:#{#phone}", limit = 1, window = 60)
    @PostMapping("/verify-code")
    public Result<String> sendVerifyCode(@RequestParam String phone) {
        authService.sendVerifyCode(phone);
        return Result.success("验证码已发送");
    }
}
```

### 示例2：用户行为限流

```java
@Service
@RequiredArgsConstructor
public class UserService {
    
    private final RateLimitService rateLimitService;
    
    /**
     * 用户点赞：限制每个用户每天最多100次点赞
     */
    public void like(Long userId, Long targetId) {
        String key = "like:user:" + userId + ":day:" + LocalDate.now();
        boolean allowed = rateLimitService.slidingWindowLimit(key, 100, 86400);
        
        if (!allowed) {
            throw new BusinessException(429, "今日点赞次数已达上限");
        }
        
        // 点赞逻辑
    }
    
    /**
     * 用户评论：限制每个用户每分钟最多5条评论
     */
    public void comment(Long userId, String content) {
        String key = "comment:user:" + userId;
        boolean allowed = rateLimitService.slidingWindowLimit(key, 5, 60);
        
        if (!allowed) {
            throw new BusinessException(429, "评论过于频繁，请稍后再试");
        }
        
        // 评论逻辑
    }
}
```

### 示例3：不同限流策略

```java
@RestController
@RequestMapping("/api/product")
@RequiredArgsConstructor
public class ProductController {
    
    private final ProductService productService;
    
    /**
     * 商品查询：使用令牌桶算法，允许突发流量
     */
    @GetMapping("/{id}")
    public Result<Product> getProduct(@PathVariable Long id) {
        RateLimitService rateLimitService = ...;
        
        // 令牌桶：rate=100/秒, capacity=1000，允许突发1000个请求
        boolean allowed = rateLimitService.tryAcquire(
            "product:query:" + id,
            1,
            100,
            1000
        );
        
        if (!allowed) {
            throw new BusinessException(429, "请求过于频繁，请稍后再试");
        }
        
        return Result.success(productService.getProduct(id));
    }
    
    /**
     * 商品购买：使用滑动窗口，严格限制购买频率
     */
    @RateLimit(key = "purchase:#{#userId}", limit = 10, window = 60)
    @PostMapping("/{id}/purchase")
    public Result<Order> purchase(@PathVariable Long id, @RequestParam Long userId) {
        // 购买逻辑
    }
}
```

## 算法说明

### 令牌桶算法

**原理**：
- 系统以固定速率生成令牌并放入桶中
- 请求需要获取令牌才能通过
- 如果桶中有足够的令牌，请求通过并消耗令牌
- 如果桶中没有足够的令牌，请求被限流
- 桶的容量限制了突发流量的上限

**适用场景**：
- 需要平滑限流，允许突发流量
- 需要控制平均速率和峰值速率
- 适合流量波动较大的场景

**参数说明**：
- `rate`：令牌生成速率（每秒生成的令牌数）
- `capacity`：令牌桶容量（最大令牌数）

**示例**：
```java
// 每秒生成10个令牌，桶容量100，允许突发100个请求
rateLimitService.tryAcquire("key", 1, 10, 100);
```

### 滑动窗口算法

**原理**：
- 将时间划分为多个时间窗口
- 统计每个时间窗口内的请求数
- 如果当前窗口内的请求数超过限制，则限流
- 窗口会随着时间滑动，自动清理过期数据

**适用场景**：
- 需要限制固定时间窗口内的请求数
- API 接口限流
- 用户行为限流（如登录、注册等）

**参数说明**：
- `limit`：时间窗口内的最大请求数
- `windowSeconds`：时间窗口大小（秒）

**示例**：
```java
// 每分钟最多100次请求
rateLimitService.slidingWindowLimit("key", 100, 60);
```

## 配置说明

### 必需配置

- **缓存服务**：必须配置 Redis 或 Caffeine 缓存服务

### 可选配置

| 配置项 | 说明 | 默认值 | 是否必需 |
|--------|------|--------|----------|
| 缓存服务 | Redis 或 Caffeine | - | 是 |

### 配置示例

```yaml
# Redis 配置（推荐用于分布式环境）
spring:
  redis:
    enabled: true
    host: localhost
    port: 6379

# 或 Caffeine 配置（推荐用于单机环境）
cache:
  local:
    enabled: true
```

## 最佳实践

### 1. 选择合适的限流算法

根据业务场景选择合适的算法：

```java
// ✅ 推荐：API 查询使用令牌桶（允许突发）
rateLimitService.tryAcquire("api:query", 1, 100, 1000);

// ✅ 推荐：用户操作使用滑动窗口（严格限制）
@RateLimit(limit = 10, window = 60)
public void userAction() { }
```

### 2. 合理设置限流参数

根据实际业务需求设置限流参数：

```java
// ✅ 推荐：登录接口，限制每个IP每分钟10次
@RateLimit(limit = 10, window = 60)

// ✅ 推荐：注册接口，限制每个IP每小时5次
@RateLimit(limit = 5, window = 3600)

// ❌ 不推荐：参数设置过小，影响正常用户
@RateLimit(limit = 1, window = 60)
```

### 3. 使用合适的限流 key

根据业务需求选择合适的限流 key：

```java
// ✅ 推荐：按用户限流
@RateLimit(key = "user:#{#userId}", limit = 100, window = 60)

// ✅ 推荐：按IP限流（key 为空，自动使用 IP）
@RateLimit(limit = 100, window = 60)

// ✅ 推荐：全局限流
@RateLimit(key = "global:api", limit = 1000, window = 60)
```

### 4. 处理限流异常

妥善处理限流异常，提供友好的错误提示：

```java
@RestControllerAdvice
public class GlobalExceptionHandler {
    
    @ExceptionHandler(BusinessException.class)
    public Result<?> handleBusinessException(BusinessException e) {
        if (e.getCode() == 429) {
            // 限流异常，返回友好的提示
            return Result.error(429, "请求过于频繁，请稍后再试");
        }
        return Result.error(e.getCode(), e.getMessage());
    }
}
```

### 5. 监控限流效果

记录限流日志，监控限流效果：

```java
// 限流切面会自动记录日志
// 可以通过日志分析限流效果
log.warn("请求被限流: key={}, limit={}, window={}", key, limit, window);
```

### 6. 缓存服务不可用时的处理

如果缓存服务未配置，限流功能会失效（允许所有请求通过）：

```java
// 组件会自动处理缓存服务不可用的情况
if (cacheService == null) {
    log.warn("CacheService未配置，限流功能无法正常工作，允许请求通过");
    return true;  // 允许请求通过
}
```

## 常见问题

### 1. 限流不生效？

**答**：检查以下几点：
- 缓存服务是否已配置（Redis 或 Caffeine）
- Spring Boot 是否能够扫描到 `com.example.ratelimit` 包
- AOP 是否启用（Spring Boot 默认启用）
- 方法是否被正确代理（注意 `@Transactional` 等注解的影响）

### 2. SpEL 表达式不生效？

**答**：
- 确保编译时保留了参数名信息（`-parameters` 编译选项）
- 或者使用 `args[0]`、`args[1]` 等方式访问参数
- 检查表达式语法是否正确

### 3. 如何按用户限流？

**答**：使用 SpEL 表达式指定 key：

```java
@RateLimit(key = "user:#{#userId}", limit = 100, window = 60)
public void userAction(@RequestParam Long userId) { }
```

### 4. 如何按IP限流？

**答**：不指定 key，组件会自动使用 IP 地址：

```java
@RateLimit(limit = 100, window = 60)  // 自动使用 IP 作为 key
public void api() { }
```

### 5. 令牌桶和滑动窗口的区别？

**答**：
- **令牌桶**：允许突发流量，适合流量波动大的场景
- **滑动窗口**：严格限制时间窗口内的请求数，适合需要精确控制的场景

### 6. 如何禁用限流？

**答**：
- 不配置缓存服务，限流功能会自动失效
- 或者移除 `@RateLimit` 注解

### 7. 限流 key 的命名规范？

**答**：建议使用命名空间格式：

```java
// ✅ 推荐
"user:1"
"api:login"
"order:create:user:1"

// ❌ 不推荐
"1"
"login"
"user1"
```

## 版本历史

- **v1.0.0** (2024-01-01)
  - 初始版本
  - 提供令牌桶算法限流
  - 提供滑动窗口算法限流
  - 支持注解式限流

## 许可证

本项目采用 MIT 许可证。

## 联系方式

如有问题或建议，请联系开发团队。

