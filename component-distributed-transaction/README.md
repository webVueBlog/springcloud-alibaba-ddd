# 分布式事务组件 (Component Distributed Transaction)

## 概述

Component Distributed Transaction 是一个基于 Seata 的分布式事务管理组件，提供统一的分布式事务接口，支持注解和编程两种使用方式。在分布式环境下，确保跨多个服务的事务操作能够保持一致性。

## 功能特性

### 核心功能

- **分布式事务管理**：基于 Seata AT 模式实现
- **注解方式**：使用 `@GlobalTransactional` 注解，自动开启分布式事务
- **编程方式**：提供 `DistributedTransactionService` 接口，支持编程式事务管理
- **自动数据源代理**：自动代理数据源，无需手动配置
- **自动回滚**：发生异常时自动回滚所有操作
- **事务工具类**：提供事务相关的工具方法

### 技术特性

- 基于 Seata AT 模式
- 支持跨多个微服务的事务操作
- 自动生成回滚日志
- 支持 Nacos 配置和注册
- 完善的异常处理和日志记录

## 技术栈

- Spring Boot 2.7.18
- Seata 1.6.1
- Nacos（用于 Seata 配置和注册）
- MySQL（支持其他数据库）

## 快速开始

### 1. 添加依赖

在需要使用分布式事务组件的服务中，添加以下依赖：

```xml
<dependency>
    <groupId>com.example</groupId>
    <artifactId>component-distributed-transaction</artifactId>
    <version>1.0.0</version>
</dependency>
```

### 2. 配置 Seata

在 `application.yml` 中配置 Seata 相关属性：

```yaml
seata:
  enabled: true  # 启用 Seata（默认不启用）
  application-id: ${spring.application.name}
  tx-service-group: my_test_tx_group  # 事务分组，所有参与事务的服务必须使用相同的分组
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
```

### 3. 启用分布式事务

在启动类上添加注解：

```java
@SpringBootApplication
@EnableDistributedTransaction
public class Application {
    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }
}
```

### 4. 使用分布式事务

#### 方式1：使用注解（推荐）

```java
import io.seata.spring.annotation.GlobalTransactional;

@Service
public class OrderService {
    
    @GlobalTransactional(rollbackFor = Exception.class)
    public Order createOrder(OrderDTO orderDTO) {
        // 业务逻辑
        Order order = orderRepository.save(new Order(orderDTO));
        // 调用其他服务
        userService.updateUser(order.getUserId());
        return order;
    }
}
```

#### 方式2：使用服务接口

```java
@Service
@RequiredArgsConstructor
public class OrderService {
    
    private final DistributedTransactionService transactionService;
    
    public Order createOrder(OrderDTO orderDTO) {
        return transactionService.execute(() -> {
            Order order = orderRepository.save(new Order(orderDTO));
            userService.updateUser(order.getUserId());
            return order;
        });
    }
}
```

## 使用指南

### 注解方式（推荐）

#### 基本使用

```java
import io.seata.spring.annotation.GlobalTransactional;

@Service
public class OrderService {
    
    /**
     * 创建订单（带分布式事务）
     */
    @GlobalTransactional(rollbackFor = Exception.class)
    public Order createOrder(OrderDTO orderDTO) {
        // 1. 创建订单
        Order order = orderRepository.save(new Order(orderDTO));
        
        // 2. 调用其他服务
        userService.updateUser(order.getUserId());
        inventoryService.deductStock(order.getProductId(), order.getQuantity());
        
        // 如果任何一步失败，所有操作都会回滚
        return order;
    }
}
```

#### 配置回滚规则

```java
// 所有异常都回滚（推荐）
@GlobalTransactional(rollbackFor = Exception.class)

// 只有 RuntimeException 回滚
@GlobalTransactional(rollbackFor = RuntimeException.class)

// 指定多个异常类型
@GlobalTransactional(rollbackFor = {BusinessException.class, RuntimeException.class})

// 不回滚指定异常
@GlobalTransactional(noRollbackFor = BusinessException.class)
```

#### 配置超时时间

```java
@GlobalTransactional(
    rollbackFor = Exception.class,
    timeoutMills = 30000  // 超时时间：30秒
)
public Order createOrder(OrderDTO orderDTO) {
    // ...
}
```

### 编程方式

#### 方式1：使用 execute（带返回值）

```java
@Service
@RequiredArgsConstructor
public class OrderService {
    
    private final DistributedTransactionService transactionService;
    
    public Order createOrder(OrderDTO orderDTO) {
        return transactionService.execute(() -> {
            // 业务逻辑
            Order order = orderRepository.save(new Order(orderDTO));
            userService.updateUser(order.getUserId());
            inventoryService.deductStock(order.getProductId(), order.getQuantity());
            return order;
        });
    }
}
```

#### 方式2：使用 executeWithoutResult（无返回值）

```java
@Service
@RequiredArgsConstructor
public class OrderService {
    
    private final DistributedTransactionService transactionService;
    
    public void createOrder(OrderDTO orderDTO) {
        transactionService.executeWithoutResult(() -> {
            // 业务逻辑
            Order order = orderRepository.save(new Order(orderDTO));
            userService.updateUser(order.getUserId());
            inventoryService.deductStock(order.getProductId(), order.getQuantity());
        });
    }
}
```

### 工具类使用

#### 获取当前事务 XID

```java
import com.example.transaction.util.TransactionUtil;

@Service
public class OrderService {
    
    public void createOrder(OrderDTO orderDTO) {
        // 获取当前事务 XID
        String xid = TransactionUtil.getCurrentXid();
        log.info("当前事务 XID: {}", xid);
        
        // 业务逻辑...
    }
}
```

#### 判断是否存在全局事务

```java
import com.example.transaction.util.TransactionUtil;

@Service
public class OrderService {
    
    public void createOrder(OrderDTO orderDTO) {
        // 判断是否存在全局事务
        if (TransactionUtil.hasGlobalTransaction()) {
            log.info("当前存在全局事务");
        }
        
        // 业务逻辑...
    }
}
```

#### 使用服务接口获取 XID

```java
@Service
@RequiredArgsConstructor
public class OrderService {
    
    private final DistributedTransactionService transactionService;
    
    public void createOrder(OrderDTO orderDTO) {
        // 获取当前事务 XID
        String xid = transactionService.getCurrentXid();
        
        // 判断是否存在全局事务
        if (transactionService.isGlobalTransaction()) {
            log.info("当前存在全局事务: {}", xid);
        }
        
        // 业务逻辑...
    }
}
```

## 完整示例

### 示例1：订单创建场景

```java
@Service
@RequiredArgsConstructor
@Slf4j
public class OrderService {
    
    private final OrderRepository orderRepository;
    private final UserService userService;
    private final InventoryService inventoryService;
    
    /**
     * 创建订单（使用注解方式）
     */
    @GlobalTransactional(rollbackFor = Exception.class)
    public Order createOrder(OrderDTO orderDTO) {
        log.info("开始创建订单: {}", orderDTO);
        
        // 1. 创建订单
        Order order = new Order();
        order.setUserId(orderDTO.getUserId());
        order.setProductId(orderDTO.getProductId());
        order.setQuantity(orderDTO.getQuantity());
        order.setAmount(orderDTO.getAmount());
        order = orderRepository.save(order);
        log.info("订单创建成功: orderId={}", order.getId());
        
        // 2. 扣减库存（调用库存服务）
        inventoryService.deductStock(order.getProductId(), order.getQuantity());
        log.info("库存扣减成功: productId={}, quantity={}", order.getProductId(), order.getQuantity());
        
        // 3. 更新用户积分（调用用户服务）
        userService.addPoints(order.getUserId(), order.getAmount());
        log.info("用户积分更新成功: userId={}, points={}", order.getUserId(), order.getAmount());
        
        // 如果任何一步失败，所有操作都会回滚
        return order;
    }
    
    /**
     * 创建订单（使用编程方式）
     */
    public Order createOrderWithService(OrderDTO orderDTO) {
        DistributedTransactionService transactionService = ...; // 注入服务
        
        return transactionService.execute(() -> {
            log.info("开始创建订单: {}", orderDTO);
            
            // 1. 创建订单
            Order order = orderRepository.save(new Order(orderDTO));
            
            // 2. 扣减库存
            inventoryService.deductStock(order.getProductId(), order.getQuantity());
            
            // 3. 更新用户积分
            userService.addPoints(order.getUserId(), order.getAmount());
            
            return order;
        });
    }
}
```

### 示例2：跨服务转账场景

```java
@Service
@RequiredArgsConstructor
@Slf4j
public class TransferService {
    
    private final AccountRepository accountRepository;
    private final AccountService accountService;
    
    /**
     * 跨服务转账
     */
    @GlobalTransactional(rollbackFor = Exception.class)
    public void transfer(Long fromUserId, Long toUserId, BigDecimal amount) {
        log.info("开始转账: fromUserId={}, toUserId={}, amount={}", fromUserId, toUserId, amount);
        
        // 1. 从源账户扣款（本地服务）
        Account fromAccount = accountRepository.findByUserId(fromUserId);
        if (fromAccount.getBalance().compareTo(amount) < 0) {
            throw new BusinessException("余额不足");
        }
        fromAccount.setBalance(fromAccount.getBalance().subtract(amount));
        accountRepository.save(fromAccount);
        log.info("源账户扣款成功: userId={}, amount={}", fromUserId, amount);
        
        // 2. 向目标账户转账（调用其他服务）
        accountService.addBalance(toUserId, amount);
        log.info("目标账户入账成功: userId={}, amount={}", toUserId, amount);
        
        // 如果任何一步失败，所有操作都会回滚
    }
}
```

### 示例3：使用工具类

```java
@Service
@RequiredArgsConstructor
@Slf4j
public class OrderService {
    
    private final OrderRepository orderRepository;
    
    @GlobalTransactional(rollbackFor = Exception.class)
    public Order createOrder(OrderDTO orderDTO) {
        // 获取当前事务 XID
        String xid = TransactionUtil.getCurrentXid();
        log.info("当前事务 XID: {}", xid);
        
        // 判断是否存在全局事务
        if (TransactionUtil.hasGlobalTransaction()) {
            log.info("当前存在全局事务");
        }
        
        // 业务逻辑
        Order order = orderRepository.save(new Order(orderDTO));
        
        // 如果需要，可以手动回滚（通常不需要）
        // TransactionUtil.rollback();
        
        return order;
    }
}
```

## 配置说明

### 必需配置

- **Seata Server**：需要启动 Seata Server
- **Nacos**：需要配置 Nacos（用于 Seata 配置和注册）
- **数据源**：需要配置数据源（Seata 会自动代理）

### 配置项说明

| 配置项 | 说明 | 默认值 | 是否必需 |
|--------|------|--------|----------|
| `seata.enabled` | 是否启用 Seata | false | 是 |
| `seata.application-id` | 应用ID | ${spring.application.name} | 是 |
| `seata.tx-service-group` | 事务分组 | - | 是 |
| `seata.config.type` | 配置中心类型 | nacos | 是 |
| `seata.config.nacos.server-addr` | Nacos 地址 | localhost:8848 | 是 |
| `seata.registry.type` | 注册中心类型 | nacos | 是 |
| `seata.registry.nacos.server-addr` | Nacos 地址 | localhost:8848 | 是 |

### 配置示例

```yaml
seata:
  enabled: true
  application-id: ${spring.application.name}
  tx-service-group: my_test_tx_group  # 所有参与事务的服务必须使用相同的分组
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
```

## 最佳实践

### 1. 使用注解方式

推荐使用 `@GlobalTransactional` 注解，更简洁：

```java
// ✅ 推荐：注解方式
@GlobalTransactional(rollbackFor = Exception.class)
public Order createOrder(OrderDTO orderDTO) {
    // ...
}

// ⚠️ 也可以：编程方式（需要更多代码）
public Order createOrder(OrderDTO orderDTO) {
    return transactionService.execute(() -> {
        // ...
        return order;
    });
}
```

### 2. 配置回滚规则

使用 `rollbackFor = Exception.class` 确保所有异常都会回滚：

```java
// ✅ 推荐：所有异常都回滚
@GlobalTransactional(rollbackFor = Exception.class)

// ⚠️ 不推荐：只有 RuntimeException 回滚
@GlobalTransactional(rollbackFor = RuntimeException.class)
```

### 3. 确保事务分组一致

所有参与事务的服务必须使用相同的 `tx-service-group`：

```yaml
# 服务A
seata:
  tx-service-group: my_test_tx_group

# 服务B（必须相同）
seata:
  tx-service-group: my_test_tx_group
```

### 4. 跨服务调用时传递 XID

Seata 会自动处理 XID 的传递，但需要确保：
- 使用支持 Seata 的 HTTP 客户端（如 Feign）
- 在请求头中传递 XID（Seata 会自动处理）

### 5. 避免长时间事务

设置合理的超时时间，避免事务长时间占用资源：

```java
@GlobalTransactional(
    rollbackFor = Exception.class,
    timeoutMills = 30000  // 30秒超时
)
```

### 6. 处理分布式事务服务不可用的情况

```java
@Autowired(required = false)
private DistributedTransactionService transactionService;

public void doSomething() {
    if (transactionService != null) {
        transactionService.execute(() -> {
            // ...
            return null;
        });
    } else {
        // 降级处理
        // ...
    }
}
```

## 注意事项

### 1. 数据源配置

- Seata 会自动代理数据源，无需手动配置
- 确保数据源已正确配置

### 2. Seata Server

- 需要启动 Seata Server
- 需要将 Seata Server 配置到 Nacos

### 3. 事务分组

- 所有参与事务的服务必须使用相同的 `tx-service-group`
- 不同的事务分组之间不会相互影响

### 4. 异常处理

- 使用 `@GlobalTransactional(rollbackFor = Exception.class)` 确保所有异常都会触发回滚
- 不要捕获异常后不抛出，否则事务不会回滚

### 5. 跨服务调用

- 确保在跨服务调用时传递 XID（Seata 会自动处理）
- 使用支持 Seata 的 HTTP 客户端

### 6. 数据库支持

- Seata AT 模式支持 MySQL、PostgreSQL、Oracle 等数据库
- 需要数据库支持本地事务

## 常见问题

### 1. 分布式事务服务注入为 null？

**答**：检查以下几点：
- `seata.enabled` 是否为 `true`
- 是否在启动类上添加了 `@EnableDistributedTransaction` 注解
- Spring Boot 是否能够扫描到 `com.example.transaction` 包

### 2. 事务不回滚？

**答**：可能的原因：
- 没有配置 `rollbackFor = Exception.class`
- 异常被捕获后没有重新抛出
- 事务分组不一致
- Seata Server 未启动或配置错误

### 3. 跨服务调用时事务不生效？

**答**：检查以下几点：
- 所有服务是否使用相同的事务分组
- 是否使用支持 Seata 的 HTTP 客户端
- XID 是否正确传递（Seata 会自动处理）

### 4. 如何禁用分布式事务？

**答**：设置 `seata.enabled=false`，分布式事务服务将不会被创建。

### 5. 事务超时？

**答**：可以配置超时时间：

```java
@GlobalTransactional(
    rollbackFor = Exception.class,
    timeoutMills = 30000  // 30秒
)
```

## 相关文档

- [Seata 官方文档](https://seata.io/zh-cn/docs/overview/what-is-seata.html)
- [Seata AT 模式](https://seata.io/zh-cn/docs/dev/mode/at-mode.html)
- [项目架构说明](../../项目架构说明.md)

## 版本历史

- **v1.0.0** (2024-01-01)
  - 初始版本
  - 提供分布式事务接口和实现
  - 支持注解方式和编程方式
  - 提供事务工具类

## 许可证

本项目采用 MIT 许可证。

## 联系方式

如有问题或建议，请联系开发团队。
