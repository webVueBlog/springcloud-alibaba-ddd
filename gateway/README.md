# 网关服务（Gateway Service）

## 概述

网关服务是基于 Spring Cloud Gateway 的 API 网关，提供统一入口、路由转发、认证授权、跨域处理等功能。集成了 SSO 单点登录和 Nacos 服务发现，适用于微服务架构中的统一网关入口。

## 技术栈

- **Spring Boot**: 2.7.18
- **Spring Cloud Gateway**: 3.1.8
- **Spring Cloud Alibaba**: 2021.0.5.0
- **Nacos**: 服务注册与发现、配置中心（可选）
- **SSO**: 单点登录（基于 JWT + Redis，可选）

## 项目结构

```
gateway/
├── filter/                      # 过滤器
│   ├── AuthFilter.java          # 认证过滤器
│   ├── LoggingFilter.java       # 日志过滤器
│   └── ErrorFilter.java         # 全局异常过滤器
├── config/                      # 配置类
│   └── GatewayConfig.java       # 网关配置（限流Key解析器）
├── GatewayApplication.java      # 启动类
└── application.yml              # 配置文件
```

## 功能特性

### 1. 路由转发

- ✅ **服务路由**：支持动态路由配置
- ✅ **负载均衡**：集成 Spring Cloud LoadBalancer
- ✅ **服务发现**：基于 Nacos 自动发现服务（可选）
- ✅ **直接路由**：支持直接使用 HTTP 地址路由（不依赖服务发现）

### 2. 认证授权

- ✅ **Token 验证**：基于 JWT Token 的认证
- ✅ **白名单机制**：支持配置免认证路径
- ✅ **用户信息传递**：自动将用户ID添加到请求头
- ✅ **可选认证**：如果 SSO 服务未配置，自动跳过认证

### 3. 跨域处理

- ✅ **CORS 配置**：支持跨域请求
- ✅ **灵活配置**：可配置允许的源、方法、头部
- ✅ **凭证支持**：支持携带凭证的跨域请求

### 4. 日志和监控

- ✅ **请求日志**：记录所有请求和响应
- ✅ **性能监控**：记录请求耗时
- ✅ **异常处理**：全局异常捕获和统一响应

### 5. 限流支持

- ✅ **IP 限流**：按 IP 地址限流（KeyResolver 已配置）
- ✅ **用户限流**：按用户ID限流（KeyResolver 已配置）

## 路由配置

### 当前路由

| 路由ID | 服务名 | 路径 | URI | 说明 |
|--------|--------|------|-----|------|
| auth-service | auth-service | /api/auth/** | http://localhost:8081 | 认证服务 |
| user-service | user-service | /api/user/** | http://localhost:8082 | 用户服务 |
| order-service | order-service | /api/order/** | http://localhost:8083 | 订单服务 |
| seckill-service | seckill-service | /api/seckill/** | http://localhost:8084 | 秒杀服务 |

### 白名单路径

以下路径不需要认证即可访问：

- `/api/auth/login` - 登录接口
- `/api/auth/verify-code` - 验证码接口
- `/api/auth/register` - 注册接口
- `/actuator/**` - Actuator 端点（健康检查等）

## API 使用示例

### 1. 登录（无需Token）

```bash
POST http://localhost:8080/api/auth/login
Content-Type: application/json

{
  "username": "admin",
  "password": "123456"
}
```

**响应示例**：
```json
{
  "code": 200,
  "message": "登录成功",
  "data": {
    "token": "eyJhbGciOiJIUzUxMiJ9...",
    "userId": 1,
    "username": "admin"
  }
}
```

### 2. 访问需要认证的接口

```bash
GET http://localhost:8080/api/order/user/1
Authorization: Bearer eyJhbGciOiJIUzUxMiJ9...
```

网关会自动：
1. 验证 Token 有效性
2. 从 Token 中提取用户ID
3. 将用户ID添加到请求头 `X-User-Id`
4. 将 Token 添加到请求头 `X-Token`
5. 转发请求到对应的微服务

### 3. 访问白名单接口（无需Token）

```bash
GET http://localhost:8080/api/auth/verify-code?phone=13800138000
```

## 配置说明

### application.yml

```yaml
server:
  port: 8080

spring:
  application:
    name: gateway-service
  main:
    web-application-type: reactive  # 强制使用响应式 Web 应用类型
  cloud:
    nacos:
      discovery:
        server-addr: localhost:8848
        enabled: false  # 如果 Nacos 未启动，设置为 false
        fail-fast: false
      config:
        server-addr: localhost:8848
        file-extension: yml
        enabled: false  # 如果 Nacos Config 未启动，设置为 false
    gateway:
      discovery:
        locator:
          enabled: false  # 禁用服务发现路由
      routes:
        - id: auth-service
          uri: http://localhost:8081  # 直接使用HTTP地址
          predicates:
            - Path=/api/auth/**
          filters:
            - StripPrefix=0
      globalcors:
        cors-configurations:
          '[/**]':
            allowedOriginPatterns: "*"
            allowedMethods: "*"
            allowedHeaders: "*"
            allowCredentials: true
            maxAge: 3600

# Redis 配置（可选）
spring:
  redis:
    enabled: false  # 如果 Redis 未启动，设置为 false
    host: localhost
    port: 6379
    password: ""
    database: 0

# JWT 配置（用于 SSO）
jwt:
  secret: springcloud-alibaba-ddd-secret-key-for-jwt-token-generation
  expiration: 86400  # 24小时（秒）
```

### 使用服务发现（可选）

如果使用 Nacos 服务发现，可以配置：

```yaml
spring:
  cloud:
    nacos:
      discovery:
        enabled: true  # 启用服务发现
    gateway:
      routes:
        - id: auth-service
          uri: lb://auth-service  # 使用 lb:// 前缀启用负载均衡
          predicates:
            - Path=/api/auth/**
```

### JWT 配置

```yaml
jwt:
  secret: your-secret-key-should-be-at-least-256-bits-long-and-secure
  expiration: 86400  # Token 过期时间（秒），默认 24 小时
```

### Redis 配置（可选）

如果 Redis 未启动，可以设置 `spring.redis.enabled: false`，SSO 功能会被禁用，但网关仍可正常路由请求。

## 过滤器说明

### 过滤器执行顺序

1. **ErrorFilter** (Order: HIGHEST_PRECEDENCE) - 全局异常处理
2. **LoggingFilter** (Order: -200) - 请求日志记录
3. **AuthFilter** (Order: -100) - 认证验证

### AuthFilter（认证过滤器）

**功能**：
- 验证 JWT Token
- 提取用户ID并添加到请求头
- 白名单路径直接放行

**请求头**：
- 输入：`Authorization: Bearer {token}`
- 输出：`X-User-Id: {userId}`, `X-Token: {token}`

**配置**：
- 白名单路径在代码中配置（`WHITE_LIST`）
- SSO 服务可选，如果未配置则跳过认证

### LoggingFilter（日志过滤器）

**功能**：
- 记录请求信息（方法、路径、客户端IP）
- 记录响应信息（状态码、耗时）

**日志格式**：
```
网关请求: method=GET, path=/api/order/user/1, remoteAddress=127.0.0.1
网关响应: method=GET, path=/api/order/user/1, duration=45ms, status=200
```

### ErrorFilter（异常过滤器）

**功能**：
- 捕获过滤器链中的所有异常
- 返回统一的错误响应格式

**响应格式**：
```json
{
  "code": 500,
  "message": "错误消息",
  "timestamp": 1234567890
}
```

## 限流配置

### 使用 IP 限流

在路由配置中添加限流过滤器：

```yaml
spring:
  cloud:
    gateway:
      routes:
        - id: auth-service
          uri: http://localhost:8081
          predicates:
            - Path=/api/auth/**
          filters:
            - name: RequestRateLimiter
              args:
                key-resolver: "#{@ipKeyResolver}"
                redis-rate-limiter.replenishRate: 10  # 每秒允许的请求数
                redis-rate-limiter.burstCapacity: 20   # 突发容量
```

### 使用用户限流

```yaml
filters:
  - name: RequestRateLimiter
    args:
      key-resolver: "#{@userKeyResolver}"
      redis-rate-limiter.replenishRate: 5
      redis-rate-limiter.burstCapacity: 10
```

**注意**：使用限流功能需要配置 Redis。

## 启动说明

### 1. 依赖服务（可选）

- **Nacos**：服务注册与发现（可选）
  - 如果未启动，设置 `spring.cloud.nacos.discovery.enabled: false`
- **Redis**：SSO Token 存储和限流（可选）
  - 如果未启动，设置 `spring.redis.enabled: false`

### 2. 启动服务

```bash
# 使用 Maven
mvn spring-boot:run

# 或打包后运行
mvn clean package
java -jar target/gateway-1.0.0.jar
```

### 3. 验证服务

- 访问 `http://localhost:8080/actuator/health` 检查服务状态
- 访问 `http://localhost:8080/api/auth/login` 测试路由

## 依赖组件

- **component-sso**: 单点登录组件（JWT + Redis）
- **component-cache**: 缓存组件（Redis，可选）

## 最佳实践

### 1. 白名单配置

将不需要认证的路径添加到白名单：

```java
private static final List<String> WHITE_LIST = Arrays.asList(
    "/api/auth/login",
    "/api/auth/register",
    "/actuator/**"
);
```

### 2. 错误处理

使用 `ErrorFilter` 统一处理异常，返回统一的错误响应格式。

### 3. 日志记录

使用 `LoggingFilter` 记录所有请求和响应，便于问题排查和性能监控。

### 4. 用户信息传递

在 `AuthFilter` 中将用户ID添加到请求头，下游服务可以直接使用：

```java
ServerHttpRequest modifiedRequest = request.mutate()
    .header("X-User-Id", String.valueOf(userId))
    .header("X-Token", token)
    .build();
```

### 5. 服务发现 vs 直接路由

- **服务发现**：适合生产环境，支持负载均衡和服务动态发现
- **直接路由**：适合开发环境，不依赖服务注册中心

### 6. 安全配置

- **JWT 密钥**：使用足够长且安全的密钥（建议至少 256 位）
- **Token 过期时间**：根据业务需求设置合理的过期时间
- **HTTPS**：生产环境建议使用 HTTPS

## 常见问题

### 1. 网关无法启动？

**答**：检查以下几点：
- 端口是否被占用（默认 8080）
- 是否排除了 Spring MVC 自动配置
- 是否设置了 `web-application-type: reactive`

### 2. 路由不生效？

**答**：检查以下几点：
- 路由配置是否正确
- 目标服务是否启动
- 路径匹配是否正确（注意大小写）

### 3. Token 验证失败？

**答**：检查以下几点：
- Token 格式是否正确（Bearer {token}）
- JWT 密钥是否配置正确
- Token 是否已过期
- Redis 是否启动（如果使用缓存服务）

### 4. 跨域请求失败？

**答**：检查以下几点：
- CORS 配置是否正确
- 是否允许了相应的源、方法、头部
- 是否设置了 `allowCredentials: true`

### 5. 服务发现不工作？

**答**：检查以下几点：
- Nacos 是否启动
- 服务是否注册到 Nacos
- 是否使用了 `lb://` 前缀

### 6. 限流不生效？

**答**：检查以下几点：
- Redis 是否启动
- KeyResolver 是否正确配置
- 限流配置是否正确

## 开发规范

1. **过滤器顺序**：使用 `Ordered` 接口定义过滤器执行顺序
2. **异常处理**：使用 `ErrorFilter` 统一处理异常
3. **日志记录**：使用 `LoggingFilter` 记录请求日志
4. **认证机制**：使用 `AuthFilter` 进行统一认证
5. **配置管理**：使用配置文件管理路由和参数
6. **代码注释**：为所有类和方法添加 JavaDoc 注释

## 后续优化

- [ ] 添加限流功能（Sentinel）
- [ ] 添加熔断降级（Sentinel）
- [ ] 添加请求重试机制
- [ ] 添加请求/响应日志持久化
- [ ] 添加 API 文档集成（Swagger）
- [ ] 添加请求链路追踪（Sleuth）
- [ ] 添加请求/响应体记录（可配置）
- [ ] 添加动态路由配置（从数据库或配置中心加载）

## 版本历史

- **v1.0.0** (2024-01-01)
  - 初始版本
  - 提供路由转发功能
  - 提供认证授权功能
  - 提供日志记录功能
  - 提供异常处理功能
  - 提供限流 Key 解析器

## 许可证

本项目采用 MIT 许可证。

## 联系方式

如有问题或建议，请联系开发团队。
