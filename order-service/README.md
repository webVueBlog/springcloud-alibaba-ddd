# 订单服务（Order Service）

## 概述

订单服务是基于 DDD（领域驱动设计）架构的微服务，负责处理订单的创建、查询、状态更新等业务功能。集成了分布式事务（Seata）和消息队列（RocketMQ）功能，适用于电商、订单管理等业务场景。

## 技术栈

- **Spring Boot**: 2.7.18
- **Spring Cloud Alibaba**: 2021.0.5.0
- **MyBatis Plus**: 3.5.3.1
- **MySQL**: 8.0.33
- **Seata**: 分布式事务（AT模式，可选）
- **RocketMQ**: 消息队列（可选）
- **Nacos**: 服务注册与发现（可选）

## 项目结构

```
order-service/
├── domain/                      # 领域层
│   ├── model/                  # 领域模型
│   │   └── Order.java          # 订单领域模型（聚合根）
│   └── repository/             # 仓储接口
│       └── OrderRepository.java # 订单仓储接口
├── application/                # 应用层
│   ├── service/                # 应用服务
│   │   └── OrderService.java   # 订单应用服务
│   └── dto/                     # 数据传输对象
│       └── OrderDTO.java        # 创建订单DTO
├── infrastructure/             # 基础设施层
│   ├── repository/              # 仓储实现
│   │   └── OrderRepositoryImpl.java
│   ├── mapper/                  # MyBatis Mapper
│   │   └── OrderMapper.java
│   └── po/                      # 持久化对象
│       └── OrderPO.java
├── interfaces/                  # 接口层
│   └── controller/              # REST控制器
│       └── OrderController.java
└── OrderApplication.java        # 启动类
```

## DDD 架构说明

### 分层职责

1. **领域层（domain）**
   - 领域模型：包含业务逻辑和业务规则
   - 仓储接口：定义数据访问接口，不依赖具体实现

2. **应用层（application）**
   - 应用服务：协调领域对象完成业务用例
   - DTO：数据传输对象，用于接口层和领域层的数据传输

3. **基础设施层（infrastructure）**
   - 仓储实现：实现领域层的仓储接口
   - Mapper：MyBatis Plus Mapper 接口
   - PO：持久化对象，对应数据库表

4. **接口层（interfaces）**
   - Controller：REST API 接口，接收 HTTP 请求

## 功能特性

### 1. 订单管理

- ✅ **创建订单**：支持创建订单，使用分布式事务保证数据一致性
- ✅ **查询订单**：支持根据ID、订单号、用户ID查询订单
- ✅ **更新订单状态**：支持更新订单状态（待支付、已支付、已发货、已完成、已取消）
- ✅ **消息通知**：订单创建后自动发送消息到 RocketMQ

### 2. 分布式事务

- ✅ 使用 Seata AT 模式实现分布式事务
- ✅ 支持跨服务事务管理
- ✅ 自动回滚机制

### 3. 消息队列

- ✅ 订单创建后自动发送消息
- ✅ 支持延迟消息
- ✅ 消息发送失败不影响订单创建

## 订单状态说明

| 状态值 | 状态名称 | 说明 |
|--------|----------|------|
| 1 | 待支付 | 订单已创建，等待用户支付 |
| 2 | 已支付 | 用户已支付，等待发货 |
| 3 | 已发货 | 订单已发货，等待确认收货 |
| 4 | 已完成 | 订单已完成，交易成功 |
| 5 | 已取消 | 订单已取消 |

## API 接口

### 1. 创建订单

**POST** `/api/order/create`

**请求头：**
```
Content-Type: application/json
Authorization: Bearer {token}  # 可选，如果网关已认证则不需要
```

**请求体：**
```json
{
  "userId": 1,
  "productId": 100,
  "quantity": 2,
  "price": 99.99,
  "remark": "备注信息"
}
```

**响应：**
```json
{
  "code": 200,
  "message": "操作成功",
  "data": {
    "id": 1,
    "orderNo": "ORD1704067200000ABCDEFGH",
    "userId": 1,
    "productId": 100,
    "quantity": 2,
    "amount": 199.98,
    "status": 1,
    "remark": "备注信息",
    "createTime": "2024-01-01T10:00:00",
    "updateTime": "2024-01-01T10:00:00"
  }
}
```

**使用示例：**
```bash
curl -X POST http://localhost:8083/api/order/create \
  -H "Content-Type: application/json" \
  -d '{
    "userId": 1,
    "productId": 100,
    "quantity": 2,
    "price": 99.99,
    "remark": "备注信息"
  }'
```

### 2. 根据ID查询订单

**GET** `/api/order/{id}`

**路径参数：**
- `id`：订单ID（Long 类型，必须大于 0）

**响应：**
```json
{
  "code": 200,
  "message": "操作成功",
  "data": {
    "id": 1,
    "orderNo": "ORD1704067200000ABCDEFGH",
    "userId": 1,
    "productId": 100,
    "quantity": 2,
    "amount": 199.98,
    "status": 1,
    "remark": "备注信息",
    "createTime": "2024-01-01T10:00:00",
    "updateTime": "2024-01-01T10:00:00"
  }
}
```

**使用示例：**
```bash
curl -X GET http://localhost:8083/api/order/1
```

### 3. 根据订单号查询订单

**GET** `/api/order/orderNo/{orderNo}`

**路径参数：**
- `orderNo`：订单号（String 类型，不能为空）

**使用示例：**
```bash
curl -X GET http://localhost:8083/api/order/orderNo/ORD1704067200000ABCDEFGH
```

### 4. 根据用户ID查询订单列表

**GET** `/api/order/user/{userId}`

**路径参数：**
- `userId`：用户ID（Long 类型，必须大于 0）

**响应：**
```json
{
  "code": 200,
  "message": "操作成功",
  "data": [
    {
      "id": 1,
      "orderNo": "ORD1704067200000ABCDEFGH",
      "userId": 1,
      "productId": 100,
      "quantity": 2,
      "amount": 199.98,
      "status": 1,
      "remark": "备注信息",
      "createTime": "2024-01-01T10:00:00",
      "updateTime": "2024-01-01T10:00:00"
    }
  ]
}
```

**使用示例：**
```bash
curl -X GET http://localhost:8083/api/order/user/1
```

### 5. 更新订单状态

**PUT** `/api/order/{id}/status?status={status}`

**路径参数：**
- `id`：订单ID（Long 类型，必须大于 0）

**请求参数：**
- `status`：订单状态（Integer 类型，1-待支付，2-已支付，3-已发货，4-已完成，5-已取消）

**使用示例：**
```bash
curl -X PUT "http://localhost:8083/api/order/1/status?status=2"
```

## 数据库设计

### 订单表（t_order）

| 字段名 | 类型 | 说明 | 约束 |
|--------|------|------|------|
| id | BIGINT | 主键ID | PRIMARY KEY, AUTO_INCREMENT |
| order_no | VARCHAR(64) | 订单号（唯一） | UNIQUE, NOT NULL |
| user_id | BIGINT | 用户ID | NOT NULL, INDEX |
| product_id | BIGINT | 商品ID | NOT NULL |
| quantity | INT | 数量 | NOT NULL |
| amount | DECIMAL(10,2) | 订单金额 | NOT NULL |
| status | INT | 订单状态 | NOT NULL, DEFAULT 1 |
| remark | VARCHAR(500) | 备注 | NULL |
| create_time | DATETIME | 创建时间 | NOT NULL, INDEX |
| update_time | DATETIME | 更新时间 | NOT NULL |
| deleted | INT | 删除标志 | NOT NULL, DEFAULT 0 |

**索引：**
- 主键：`PRIMARY KEY (id)`
- 唯一索引：`UNIQUE KEY uk_order_no (order_no)`
- 普通索引：`KEY idx_user_id (user_id)`
- 普通索引：`KEY idx_create_time (create_time)`

### 建表 SQL

```sql
CREATE TABLE `t_order` (
  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `order_no` VARCHAR(64) NOT NULL COMMENT '订单号',
  `user_id` BIGINT NOT NULL COMMENT '用户ID',
  `product_id` BIGINT NOT NULL COMMENT '商品ID',
  `quantity` INT NOT NULL COMMENT '数量',
  `amount` DECIMAL(10,2) NOT NULL COMMENT '订单金额',
  `status` INT NOT NULL DEFAULT 1 COMMENT '订单状态：1-待支付，2-已支付，3-已发货，4-已完成，5-已取消',
  `remark` VARCHAR(500) DEFAULT NULL COMMENT '备注',
  `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `deleted` INT NOT NULL DEFAULT 0 COMMENT '删除标志：0-未删除，1-已删除',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_order_no` (`order_no`),
  KEY `idx_user_id` (`user_id`),
  KEY `idx_create_time` (`create_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='订单表';
```

## 配置说明

### application.yml

```yaml
server:
  port: 8083

spring:
  application:
    name: order-service
  cloud:
    nacos:
      discovery:
        server-addr: localhost:8848
        enabled: false  # 如果 Nacos 未启动，设置为 false
        fail-fast: false
  datasource:
    driver-class-name: com.mysql.cj.jdbc.Driver
    url: jdbc:mysql://localhost:3306/order_db?useUnicode=true&characterEncoding=utf8&serverTimezone=Asia/Shanghai&allowPublicKeyRetrieval=true
    username: root
    password: password
    hikari:
      minimum-idle: 5
      maximum-pool-size: 20
      connection-timeout: 30000
      idle-timeout: 600000
      max-lifetime: 1800000

# MyBatis Plus 配置
mybatis-plus:
  configuration:
    map-underscore-to-camel-case: true
    log-impl: org.apache.ibatis.logging.stdout.StdOutImpl
  global-config:
    db-config:
      id-type: assign_id  # ID 生成策略：使用雪花算法
      logic-delete-field: deleted  # 逻辑删除字段
      logic-delete-value: 1  # 逻辑删除值
      logic-not-delete-value: 0  # 逻辑未删除值

# Seata 配置（可选）
seata:
  enabled: false  # 如果 Seata Server 未启动，设置为 false
  application-id: order-service
  tx-service-group: my_test_tx_group
  config:
    type: nacos
    nacos:
      server-addr: localhost:8848
      group: SEATA_GROUP
      namespace: ""
  registry:
    type: nacos
    nacos:
      application: seata-server
      server-addr: localhost:8848
      group: SEATA_GROUP
      namespace: ""

# RocketMQ 配置（可选）
# rocketmq:
#   name-server: localhost:9876
#   producer:
#     group: order-producer-group
#     send-message-timeout: 3000
#     retry-times-when-send-failed: 2
```

## 启动说明

### 1. 依赖服务（可选）

- **MySQL**：数据库服务（必需）
  - 确保数据库已创建
  - 执行建表 SQL 创建订单表
- **Nacos**：服务注册与发现（可选）
  - 如果未启动，设置 `spring.cloud.nacos.discovery.enabled: false`
- **Seata**：分布式事务（可选）
  - 如果未启动，设置 `seata.enabled: false`
- **RocketMQ**：消息队列（可选）
  - 如果未启动，消息发送功能会被禁用，但不影响订单创建

### 2. 数据库准备

执行建表 SQL 创建订单表（参考上面的建表 SQL）。

### 3. 启动服务

```bash
# 使用 Maven
mvn spring-boot:run

# 或打包后运行
mvn clean package
java -jar target/order-service-1.0.0.jar
```

### 4. 验证服务

- 访问 `http://localhost:8083/actuator/health` 检查服务状态
- 访问 `http://localhost:8083/api/order/1` 测试查询接口

## 依赖组件

- **component-distributed-transaction**: 分布式事务组件（Seata，可选）
- **component-message**: 消息队列组件（RocketMQ，可选）
- **common**: 公共模块（实体类、异常处理、统一响应）

## 使用示例

### 示例1：创建订单

```java
@RestController
@RequestMapping("/api/order")
@RequiredArgsConstructor
public class OrderController {
    
    private final OrderService orderService;
    
    @PostMapping("/create")
    public Result<Order> createOrder(@Valid @RequestBody OrderDTO orderDTO) {
        Order order = orderService.createOrder(orderDTO);
        return Result.success(order);
    }
}
```

### 示例2：查询订单

```java
@Service
@RequiredArgsConstructor
public class OrderService {
    
    private final OrderRepository orderRepository;
    
    public Order getOrderById(Long id) {
        return orderRepository.findById(id);
    }
}
```

### 示例3：更新订单状态

```java
@PutMapping("/{id}/status")
public Result<Void> updateOrderStatus(
        @PathVariable Long id, 
        @RequestParam Integer status) {
    orderService.updateOrderStatus(id, status);
    return Result.success();
}
```

## 最佳实践

### 1. DDD 分层架构

严格按照 DDD 分层架构组织代码：
- **领域层**：只包含业务逻辑，不依赖任何框架
- **应用层**：协调领域对象完成业务用例
- **基础设施层**：实现技术细节（数据访问、消息发送等）
- **接口层**：处理 HTTP 请求和响应

### 2. 领域模型设计

- 领域模型继承 `BaseEntity`，包含基础字段
- 领域模型不依赖任何框架，保持纯净
- 业务逻辑封装在领域模型中

### 3. 仓储模式

- 仓储接口定义在领域层
- 仓储实现在基础设施层
- 通过仓储模式隔离领域模型和持久化技术

### 4. DTO 使用

- 接口层使用 DTO 进行数据传输
- 不直接暴露领域模型
- 使用 Bean Validation 进行参数校验

### 5. 事务管理

- 使用 `@GlobalTransactional` 管理分布式事务（Seata）
- 使用 `@Transactional` 管理本地事务
- 异常时自动回滚

### 6. 异常处理

- 使用 `BusinessException` 处理业务异常
- 使用全局异常处理器统一处理异常
- 返回统一的错误响应格式

### 7. 消息发送

- 消息发送失败不影响订单创建
- 使用 try-catch 捕获消息发送异常
- 记录日志便于排查问题

## 常见问题

### 1. 订单创建失败？

**答**：检查以下几点：
- 数据库连接是否正常
- 订单表是否已创建
- 参数验证是否通过
- Seata 配置是否正确（如果使用分布式事务）

### 2. 查询订单返回 null？

**答**：检查以下几点：
- 订单ID是否正确
- 订单是否已删除（逻辑删除）
- 数据库查询是否正常

### 3. 分布式事务不生效？

**答**：检查以下几点：
- Seata Server 是否启动
- Seata 配置是否正确
- 是否使用了 `@GlobalTransactional` 注解
- 数据库是否支持 Seata（需要 undo_log 表）

### 4. 消息发送失败？

**答**：检查以下几点：
- RocketMQ 是否启动
- RocketMQ 配置是否正确
- 消息生产者是否已配置
- 注意：消息发送失败不影响订单创建

### 5. 订单号重复？

**答**：订单号生成逻辑使用时间戳 + UUID，理论上不会重复。如果出现重复，检查：
- 系统时间是否正确
- UUID 生成是否正常
- 数据库唯一索引是否生效

### 6. 如何扩展订单功能？

**答**：
- 在领域模型中添加新的业务属性
- 在应用服务中添加新的业务方法
- 在 Controller 中添加新的 API 接口
- 在数据库中添加新的字段（如果需要）

## 开发规范

1. **DDD 分层架构**：严格按照领域层、应用层、基础设施层、接口层分层
2. **领域模型**：领域模型继承 `BaseEntity`，包含基础字段
3. **仓储模式**：使用仓储接口和实现分离，保持领域层纯净
4. **DTO 使用**：接口层使用 DTO 进行数据传输，不直接暴露领域模型
5. **事务管理**：使用 `@GlobalTransactional` 注解管理分布式事务
6. **异常处理**：使用 `BusinessException` 处理业务异常
7. **代码注释**：为所有类和方法添加 JavaDoc 注释

## 后续优化

- [ ] 添加订单分页查询
- [ ] 添加订单统计功能
- [ ] 集成分布式追踪（Sleuth）
- [ ] 添加订单缓存
- [ ] 添加订单状态机
- [ ] 添加订单超时自动取消功能
- [ ] 添加订单退款功能
- [ ] 添加订单评价功能

## 版本历史

- **v1.0.0** (2024-01-01)
  - 初始版本
  - 提供订单创建功能
  - 提供订单查询功能
  - 提供订单状态更新功能
  - 集成分布式事务（Seata）
  - 集成消息队列（RocketMQ）

## 许可证

本项目采用 MIT 许可证。

## 联系方式

如有问题或建议，请联系开发团队。
