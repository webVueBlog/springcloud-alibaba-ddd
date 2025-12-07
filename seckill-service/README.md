# 秒杀服务（Seckill Service）

## 概述

秒杀服务是基于 DDD（领域驱动设计）架构的微服务，负责处理秒杀活动的创建、库存管理、秒杀下单等业务功能。集成了分布式锁、限流、缓存等组件，适用于高并发的秒杀场景。

## 技术栈

- **Spring Boot**: 2.7.18
- **Spring Cloud Alibaba**: 2021.0.5.0
- **Redis**: 库存管理和分布式锁
- **Redisson**: 分布式锁实现
- **Nacos**: 服务注册与发现（可选）

## 项目结构

```
seckill-service/
├── domain/                      # 领域层
│   ├── model/                  # 领域模型
│   │   └── SeckillActivity.java # 秒杀活动领域模型
│   └── service/                # 领域服务
│       ├── SeckillDomainService.java
│       └── impl/
│           └── SeckillDomainServiceImpl.java
├── application/                # 应用层
│   ├── service/                # 应用服务
│   │   └── SeckillService.java # 秒杀应用服务
│   └── dto/                     # 数据传输对象
│       ├── SeckillRequest.java
│       ├── SeckillActivityDTO.java
│       └── SeckillResult.java
├── interfaces/                  # 接口层
│   └── controller/              # REST控制器
│       └── SeckillController.java
└── SeckillApplication.java      # 启动类
```

## DDD 架构说明

### 分层职责

1. **领域层（domain）**
   - 领域模型：秒杀活动聚合根，包含业务属性
   - 领域服务：业务规则验证（活动有效性、库存检查等）

2. **应用层（application）**
   - 应用服务：协调领域对象完成业务用例
   - DTO：数据传输对象，用于接口层和领域层的数据传输

3. **接口层（interfaces）**
   - Controller：REST API 接口，接收 HTTP 请求

## 功能特性

### 1. 秒杀下单

- ✅ **高并发支持**：使用 Redis 缓存管理库存，支持高并发
- ✅ **分布式锁**：使用分布式锁防止超卖
- ✅ **限流保护**：使用限流组件保护系统
- ✅ **防重复下单**：防止用户重复参与秒杀
- ✅ **原子操作**：使用 Redis 的 decrement 操作保证库存扣减的原子性

### 2. 库存管理

- ✅ **Redis 缓存**：使用 Redis 管理秒杀库存，提高性能
- ✅ **库存初始化**：支持在活动开始前初始化库存
- ✅ **库存查询**：支持查询剩余库存
- ✅ **自动过期**：库存数据自动过期，避免数据残留

### 3. 业务规则

- ✅ **活动有效性检查**：验证活动状态和时间
- ✅ **库存充足性检查**：验证库存是否充足
- ✅ **用户参与检查**：防止用户重复参与

## 秒杀流程

### 秒杀下单流程

```
1. 请求进入
   ↓
2. 限流检查（@RateLimit）
   ↓
3. 获取分布式锁（@DistributedLockAnnotation）
   ↓
4. 检查用户是否已参与
   ↓
5. 检查库存是否充足
   ↓
6. 扣减库存（原子操作）
   ↓
7. 生成订单号
   ↓
8. 记录秒杀订单
   ↓
9. 释放分布式锁
   ↓
10. 返回结果
```

## API 接口

### 1. 秒杀下单

**POST** `/api/seckill/{activityId}?userId={userId}`

**路径参数：**
- `activityId`：活动ID（Long 类型，必须大于 0）

**请求参数：**
- `userId`：用户ID（Long 类型，必须大于 0）

**响应示例（成功）：**
```json
{
  "code": 200,
  "message": "操作成功",
  "data": {
    "success": true,
    "message": "秒杀成功",
    "remainingStock": 99,
    "orderNo": "SK110012345678901234567890ABCDEFGH"
  }
}
```

**响应示例（失败）：**
```json
{
  "code": 500,
  "message": "库存不足",
  "data": null
}
```

**使用示例：**
```bash
curl -X POST "http://localhost:8084/api/seckill/1?userId=123"
```

### 2. 初始化秒杀活动库存

**POST** `/api/seckill/init/{activityId}?stock={stock}`

**路径参数：**
- `activityId`：活动ID（Long 类型，必须大于 0）

**请求参数：**
- `stock`：库存数量（Integer 类型，必须大于 0）

**响应：**
```json
{
  "code": 200,
  "message": "操作成功",
  "data": null
}
```

**使用示例：**
```bash
curl -X POST "http://localhost:8084/api/seckill/init/1?stock=100"
```

### 3. 获取剩余库存

**GET** `/api/seckill/stock/{activityId}`

**路径参数：**
- `activityId`：活动ID（Long 类型，必须大于 0）

**响应：**
```json
{
  "code": 200,
  "message": "操作成功",
  "data": 99
}
```

**使用示例：**
```bash
curl -X GET "http://localhost:8084/api/seckill/stock/1"
```

### 4. 检查用户是否已参与

**GET** `/api/seckill/check/{activityId}?userId={userId}`

**路径参数：**
- `activityId`：活动ID（Long 类型，必须大于 0）

**请求参数：**
- `userId`：用户ID（Long 类型，必须大于 0）

**响应：**
```json
{
  "code": 200,
  "message": "操作成功",
  "data": true
}
```

**使用示例：**
```bash
curl -X GET "http://localhost:8084/api/seckill/check/1?userId=123"
```

## 配置说明

### application.yml

```yaml
server:
  port: 8084

spring:
  application:
    name: seckill-service
  cloud:
    nacos:
      discovery:
        server-addr: localhost:8848
        enabled: false  # 如果 Nacos 未启动，设置为 false
        fail-fast: false
  # Redis 配置（必需）
  redis:
    enabled: true
    host: localhost
    port: 6379
    password: 
    database: 0

# Redisson 配置（用于分布式锁）
redisson:
  address: redis://localhost:6379
  password: 
```

### Redis Key 设计

| Key | 说明 | 过期时间 |
|-----|------|----------|
| `seckill:stock:{activityId}` | 秒杀活动库存 | 24小时 |
| `seckill:order:{activityId}:{userId}` | 用户秒杀订单 | 1小时 |
| `seckill:order:detail:{orderNo}` | 订单详情 | 24小时 |

## 启动说明

### 1. 依赖服务

- **Redis**：数据库服务（必需）
  - 用于库存管理和分布式锁
  - 确保 Redis 已启动并正确配置
- **Nacos**：服务注册与发现（可选）
  - 如果未启动，设置 `spring.cloud.nacos.discovery.enabled: false`

### 2. 启动服务

```bash
# 使用 Maven
mvn spring-boot:run

# 或打包后运行
mvn clean package
java -jar target/seckill-service-1.0.0.jar
```

### 3. 初始化库存

在秒杀活动开始前，需要初始化库存：

```bash
curl -X POST "http://localhost:8084/api/seckill/init/1?stock=100"
```

### 4. 验证服务

- 访问 `http://localhost:8084/actuator/health` 检查服务状态
- 访问 `http://localhost:8084/api/seckill/stock/1` 查询库存

## 依赖组件

- **component-distributed-lock**: 分布式锁组件（Redisson）
- **component-cache**: 缓存组件（Redis）
- **component-rate-limit**: 限流组件
- **common**: 公共模块（实体类、异常处理、统一响应）

## 使用示例

### 示例1：完整的秒杀流程

```java
// 1. 初始化库存（活动开始前）
POST /api/seckill/init/1?stock=100

// 2. 查询库存
GET /api/seckill/stock/1
// 响应：100

// 3. 执行秒杀
POST /api/seckill/1?userId=123
// 响应：{"success": true, "orderNo": "SK1...", "remainingStock": 99}

// 4. 再次查询库存
GET /api/seckill/stock/1
// 响应：99

// 5. 检查用户是否已参与
GET /api/seckill/check/1?userId=123
// 响应：true
```

### 示例2：在代码中使用

```java
@Service
@RequiredArgsConstructor
public class SeckillService {
    
    private final SeckillService seckillService;
    
    public void processSeckill(Long activityId, Long userId) {
        // 初始化库存
        seckillService.initStock(activityId, 100);
        
        // 执行秒杀
        SeckillResult result = seckillService.seckill(activityId, userId);
        
        if (result.getSuccess()) {
            // 秒杀成功，处理订单
            String orderNo = result.getOrderNo();
            // ...
        } else {
            // 秒杀失败，返回错误信息
            String message = result.getMessage();
            // ...
        }
    }
}
```

## 最佳实践

### 1. 库存管理

- **使用 Redis 缓存**：将库存存储在 Redis 中，提高性能
- **原子操作**：使用 Redis 的 decrement 操作保证库存扣减的原子性
- **设置过期时间**：库存数据设置过期时间，避免数据残留

```java
// ✅ 推荐：使用 Redis 原子操作
Long newStock = cacheService.decrement(stockKey);

// ❌ 不推荐：先查询再更新（非原子操作）
Integer stock = cacheService.get(stockKey, Integer.class);
cacheService.set(stockKey, stock - 1);
```

### 2. 分布式锁

- **使用分布式锁**：防止并发请求导致超卖
- **合理设置锁超时时间**：避免死锁
- **快速失败**：设置合理的等待时间

```java
@DistributedLockAnnotation(
    key = "seckill:#{#activityId}", 
    waitTime = 5,      // 等待时间 5 秒
    leaseTime = 10     // 锁持有时间 10 秒
)
```

### 3. 限流保护

- **使用限流**：限制请求频率，保护系统
- **合理设置限流参数**：根据系统容量设置限流阈值

```java
@RateLimit(
    key = "seckill:#{#activityId}", 
    limit = 10,        // 限制 10 次
    window = 60        // 时间窗口 60 秒
)
```

### 4. 防重复下单

- **检查用户参与记录**：在秒杀前检查用户是否已参与
- **记录参与信息**：秒杀成功后记录用户参与信息
- **设置过期时间**：参与记录设置过期时间

```java
// 检查用户是否已参与
String userOrderKey = "seckill:order:" + activityId + ":" + userId;
if (cacheService.exists(userOrderKey)) {
    return SeckillResult.fail("您已经参与过该秒杀活动");
}
```

### 5. 错误处理

- **使用业务异常**：使用 `BusinessException` 处理业务异常
- **记录日志**：记录详细的日志，便于排查问题
- **返回友好错误信息**：返回用户友好的错误信息

```java
try {
    // 秒杀逻辑
} catch (Exception e) {
    log.error("秒杀异常: activityId={}, userId={}", activityId, userId, e);
    return SeckillResult.fail("秒杀失败: " + e.getMessage());
}
```

### 6. 性能优化

- **预热库存**：在活动开始前将库存加载到 Redis
- **异步处理**：订单创建等耗时操作可以异步处理
- **缓存优化**：合理设置缓存过期时间

## 常见问题

### 1. 秒杀失败，提示"系统配置错误"？

**答**：检查以下几点：
- Redis 是否启动
- Redis 配置是否正确
- CacheService 是否已注入

### 2. 秒杀失败，提示"库存不足"？

**答**：检查以下几点：
- 库存是否已初始化
- 库存是否已被抢完
- Redis 中的库存数据是否正确

### 3. 秒杀失败，提示"您已经参与过该秒杀活动"？

**答**：这是正常的防重复机制，每个用户只能参与一次秒杀活动。

### 4. 如何防止超卖？

**答**：
- 使用分布式锁防止并发
- 使用 Redis 原子操作扣减库存
- 扣减后检查库存，如果为负则回滚

### 5. 如何提高秒杀性能？

**答**：
- 使用 Redis 缓存管理库存
- 使用分布式锁保护关键操作
- 使用限流保护系统
- 预热库存到 Redis
- 异步处理订单创建等耗时操作

### 6. 如何重置库存？

**答**：重新调用初始化库存接口：

```bash
curl -X POST "http://localhost:8084/api/seckill/init/1?stock=100"
```

## 开发规范

1. **DDD 分层架构**：严格按照领域层、应用层、接口层分层
2. **领域模型**：领域模型继承 `BaseEntity`，包含基础字段
3. **领域服务**：业务规则验证放在领域服务中
4. **分布式锁**：关键操作使用分布式锁保护
5. **限流保护**：使用限流组件保护系统
6. **异常处理**：使用 `BusinessException` 处理业务异常
7. **代码注释**：为所有类和方法添加 JavaDoc 注释

## 后续优化

- [ ] 添加秒杀活动管理功能（创建、查询、更新活动）
- [ ] 添加订单服务集成（秒杀成功后创建订单）
- [ ] 添加消息队列集成（异步处理订单）
- [ ] 添加秒杀预热功能（提前加载库存）
- [ ] 添加秒杀统计功能（参与人数、成功率等）
- [ ] 添加秒杀限购功能（每人限购数量）
- [ ] 添加秒杀排队功能（使用消息队列）

## 版本历史

- **v1.0.0** (2024-01-01)
  - 初始版本
  - 提供秒杀下单功能
  - 提供库存管理功能
  - 集成分布式锁和限流
  - 集成 Redis 缓存

## 许可证

本项目采用 MIT 许可证。

## 联系方式

如有问题或建议，请联系开发团队。

