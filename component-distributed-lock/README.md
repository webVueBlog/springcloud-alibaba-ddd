# 分布式锁组件 (Component Distributed Lock)

## 概述

Component Distributed Lock 是一个基于 Redisson 的分布式锁组件，提供统一的分布式锁接口，支持注解和编程两种使用方式。在分布式环境下，确保同一时刻只有一个线程能够执行被锁保护的代码块。

## 功能特性

### 核心功能

- **分布式锁**：基于 Redisson 实现，支持可重入锁
- **注解方式**：使用 `@DistributedLockAnnotation` 注解，自动为方法添加分布式锁
- **编程方式**：提供 `DistributedLock` 接口，支持手动加锁和释放锁
- **SpEL 表达式**：支持在注解中使用 SpEL 表达式动态生成锁的 key
- **自动释放**：锁有过期时间，防止死锁
- **异常处理**：完善的异常处理和日志记录

### 技术特性

- 基于 Redisson 和 Redis
- 支持可重入锁
- 支持锁的自动续期
- 支持等待时间和过期时间配置
- 使用 AOP 实现注解方式的分布式锁

## 技术栈

- Spring Boot 2.7.18
- Redisson 3.23.3
- Redis
- AspectJ
- Spring Expression Language (SpEL)

## 快速开始

### 1. 添加依赖

在需要使用分布式锁组件的服务中，添加以下依赖：

```xml
<dependency>
    <groupId>com.example</groupId>
    <artifactId>component-distributed-lock</artifactId>
    <version>1.0.0</version>
</dependency>
```

### 2. 配置 Redis

在 `application.yml` 中配置 Redis 连接信息：

```yaml
spring:
  redis:
    enabled: true  # 启用 Redis（默认启用）
    host: localhost
    port: 6379
    password: ""  # 如果有密码，填写密码
    database: 0

# 可选：Redisson 连接地址（如果不配置，则使用 spring.redis 配置）
redisson:
  address: redis://localhost:6379
```

### 3. 启用组件

确保 Spring Boot 能够扫描到分布式锁组件：

```java
@SpringBootApplication
@ComponentScan(basePackages = {"com.example.yourservice", "com.example.lock"})
public class YourApplication {
    public static void main(String[] args) {
        SpringApplication.run(YourApplication.class, args);
    }
}
```

或者导入配置类：

```java
@SpringBootApplication
@Import(com.example.lock.config.LockConfig.class)
public class YourApplication {
    // ...
}
```

### 4. 使用分布式锁

#### 方式1：使用注解（推荐）

```java
@Service
public class OrderService {
    
    @DistributedLockAnnotation(key = "lock:order:#{#orderId}", waitTime = 5, leaseTime = 10)
    public void processOrder(Long orderId) {
        // 业务逻辑
    }
}
```

#### 方式2：编程方式

```java
@Service
@RequiredArgsConstructor
public class OrderService {
    
    private final DistributedLock distributedLock;
    
    public void processOrder(Long orderId) {
        String lockKey = "lock:order:" + orderId;
        distributedLock.executeWithLock(lockKey, 5, 10, TimeUnit.SECONDS, () -> {
            // 业务逻辑
            return null;
        });
    }
}
```

## 使用指南

### 注解方式（推荐）

#### 基本使用

```java
@Service
public class UserService {
    
    /**
     * 更新用户信息（带分布式锁）
     */
    @DistributedLockAnnotation(key = "lock:user:#{#id}", waitTime = 5, leaseTime = 10)
    public void updateUser(Long id, UserDTO userDTO) {
        // 业务逻辑
        User user = userRepository.findById(id);
        user.setUsername(userDTO.getUsername());
        userRepository.save(user);
    }
}
```

#### SpEL 表达式

支持在 key 中使用 SpEL 表达式，从方法参数中获取值：

```java
// 单个参数
@DistributedLockAnnotation(key = "lock:user:#{#id}")
public void updateUser(Long id) {
    // ...
}

// 多个参数
@DistributedLockAnnotation(key = "lock:order:#{#orderId}:user:#{#userId}")
public void processOrder(Long orderId, Long userId) {
    // ...
}

// 对象属性
@DistributedLockAnnotation(key = "lock:order:#{#order.id}")
public void processOrder(Order order) {
    // ...
}
```

#### 配置等待时间和过期时间

```java
@DistributedLockAnnotation(
    key = "lock:user:#{#id}",
    waitTime = 10,    // 等待时间：10秒
    leaseTime = 30    // 锁过期时间：30秒
)
public void updateUser(Long id) {
    // ...
}
```

### 编程方式

#### 方式1：使用 executeWithLock（推荐）

```java
@Service
@RequiredArgsConstructor
public class OrderService {
    
    private final DistributedLock distributedLock;
    
    public void processOrder(Long orderId) {
        String lockKey = "lock:order:" + orderId;
        distributedLock.executeWithLock(lockKey, 5, 10, TimeUnit.SECONDS, () -> {
            // 业务逻辑
            Order order = orderRepository.findById(orderId);
            order.setStatus(OrderStatus.PROCESSING);
            orderRepository.save(order);
            return null;
        });
    }
}
```

#### 方式2：手动加锁和释放锁

```java
@Service
@RequiredArgsConstructor
public class OrderService {
    
    private final DistributedLock distributedLock;
    
    public void processOrder(Long orderId) {
        String lockKey = "lock:order:" + orderId;
        
        // 尝试获取锁（等待5秒，锁过期时间10秒）
        if (distributedLock.tryLock(lockKey, 5, 10, TimeUnit.SECONDS)) {
            try {
                // 业务逻辑
                Order order = orderRepository.findById(orderId);
                order.setStatus(OrderStatus.PROCESSING);
                orderRepository.save(order);
            } finally {
                // 释放锁
                distributedLock.unlock(lockKey);
            }
        } else {
            throw new BusinessException("获取锁失败，请稍后重试");
        }
    }
}
```

#### 方式3：立即返回（不等待）

```java
public void quickProcess(Long orderId) {
    String lockKey = "lock:order:" + orderId;
    
    // 尝试获取锁，如果锁被占用，立即返回 false
    if (distributedLock.tryLock(lockKey)) {
        try {
            // 业务逻辑
        } finally {
            distributedLock.unlock(lockKey);
        }
    } else {
        log.warn("获取锁失败，订单正在处理中: orderId={}", orderId);
    }
}
```

## 完整示例

### 示例1：秒杀场景

```java
@Service
@RequiredArgsConstructor
@Slf4j
public class SeckillService {
    
    private final ProductRepository productRepository;
    private final OrderRepository orderRepository;
    
    @Autowired(required = false)
    private DistributedLock distributedLock;
    
    /**
     * 秒杀下单（使用注解方式）
     */
    @DistributedLockAnnotation(key = "lock:seckill:#{#productId}", waitTime = 3, leaseTime = 10)
    public Order seckill(Long userId, Long productId) {
        // 检查库存
        Product product = productRepository.findById(productId);
        if (product.getStock() <= 0) {
            throw new BusinessException("商品已售罄");
        }
        
        // 扣减库存
        product.setStock(product.getStock() - 1);
        productRepository.save(product);
        
        // 创建订单
        Order order = new Order();
        order.setUserId(userId);
        order.setProductId(productId);
        order.setStatus(OrderStatus.CREATED);
        return orderRepository.save(order);
    }
    
    /**
     * 秒杀下单（使用编程方式）
     */
    public Order seckillWithLock(Long userId, Long productId) {
        if (distributedLock == null) {
            throw new BusinessException("分布式锁服务未配置");
        }
        
        String lockKey = "lock:seckill:" + productId;
        return distributedLock.executeWithLock(lockKey, 3, 10, TimeUnit.SECONDS, () -> {
            // 检查库存
            Product product = productRepository.findById(productId);
            if (product.getStock() <= 0) {
                throw new BusinessException("商品已售罄");
            }
            
            // 扣减库存
            product.setStock(product.getStock() - 1);
            productRepository.save(product);
            
            // 创建订单
            Order order = new Order();
            order.setUserId(userId);
            order.setProductId(productId);
            order.setStatus(OrderStatus.CREATED);
            return orderRepository.save(order);
        });
    }
}
```

### 示例2：防止重复提交

```java
@Service
@RequiredArgsConstructor
@Slf4j
public class OrderService {
    
    private final OrderRepository orderRepository;
    
    @Autowired(required = false)
    private DistributedLock distributedLock;
    
    /**
     * 创建订单（防止重复提交）
     */
    @DistributedLockAnnotation(key = "lock:create:order:#{#userId}", waitTime = 2, leaseTime = 5)
    public Order createOrder(Long userId, OrderDTO orderDTO) {
        // 检查是否已存在订单
        Order existingOrder = orderRepository.findByUserIdAndProductId(
            userId, orderDTO.getProductId());
        if (existingOrder != null) {
            throw new BusinessException("订单已存在，请勿重复提交");
        }
        
        // 创建订单
        Order order = new Order();
        order.setUserId(userId);
        order.setProductId(orderDTO.getProductId());
        order.setAmount(orderDTO.getAmount());
        return orderRepository.save(order);
    }
}
```

### 示例3：分布式任务调度

```java
@Service
@RequiredArgsConstructor
@Slf4j
public class TaskService {
    
    private final TaskRepository taskRepository;
    
    @Autowired(required = false)
    private DistributedLock distributedLock;
    
    /**
     * 执行定时任务（确保只在一个节点执行）
     */
    @Scheduled(cron = "0 0 1 * * ?")  // 每天凌晨1点执行
    public void executeDailyTask() {
        if (distributedLock == null) {
            log.warn("分布式锁服务未配置，跳过任务执行");
            return;
        }
        
        String lockKey = "lock:task:daily";
        distributedLock.executeWithLock(lockKey, 5, 60, TimeUnit.SECONDS, () -> {
            log.info("开始执行每日任务");
            
            // 执行任务逻辑
            List<Task> tasks = taskRepository.findPendingTasks();
            for (Task task : tasks) {
                processTask(task);
            }
            
            log.info("每日任务执行完成");
            return null;
        });
    }
    
    private void processTask(Task task) {
        // 处理任务
    }
}
```

### 示例4：更新用户余额

```java
@Service
@RequiredArgsConstructor
@Slf4j
public class UserService {
    
    private final UserRepository userRepository;
    
    /**
     * 更新用户余额（防止并发修改）
     */
    @DistributedLockAnnotation(key = "lock:user:balance:#{#userId}", waitTime = 5, leaseTime = 10)
    public void updateBalance(Long userId, BigDecimal amount) {
        User user = userRepository.findById(userId);
        if (user == null) {
            throw new BusinessException("用户不存在");
        }
        
        // 更新余额
        BigDecimal newBalance = user.getBalance().add(amount);
        if (newBalance.compareTo(BigDecimal.ZERO) < 0) {
            throw new BusinessException("余额不足");
        }
        
        user.setBalance(newBalance);
        userRepository.save(user);
    }
}
```

## 配置说明

### 必需配置

- **Redis 连接**：必须配置 Redis 连接信息

### 可选配置

| 配置项 | 说明 | 默认值 | 是否必需 |
|--------|------|--------|----------|
| `spring.redis.enabled` | 是否启用 Redis | true | 否 |
| `spring.redis.host` | Redis 主机地址 | localhost | 是 |
| `spring.redis.port` | Redis 端口 | 6379 | 是 |
| `spring.redis.password` | Redis 密码 | "" | 否 |
| `spring.redis.database` | Redis 数据库索引 | 0 | 否 |
| `redisson.address` | Redisson 连接地址 | - | 否 |

### 配置示例

```yaml
spring:
  redis:
    enabled: true
    host: localhost
    port: 6379
    password: your_password  # 如果有密码
    database: 0

# 可选：Redisson 连接地址（如果不配置，则使用 spring.redis 配置）
redisson:
  address: redis://localhost:6379
```

## 最佳实践

### 1. 使用命名空间

建议使用命名空间格式的 key，便于管理和查找：

```java
// ✅ 正确
@DistributedLockAnnotation(key = "lock:user:#{#id}")
@DistributedLockAnnotation(key = "lock:order:#{#orderId}")

// ❌ 错误
@DistributedLockAnnotation(key = "#{#id}")
@DistributedLockAnnotation(key = "lock123")
```

### 2. 设置合理的等待时间和过期时间

根据业务需求设置合理的等待时间和过期时间：

```java
// 快速操作：等待时间短，过期时间短
@DistributedLockAnnotation(key = "lock:quick:#{#id}", waitTime = 2, leaseTime = 5)

// 长时间操作：等待时间长，过期时间长
@DistributedLockAnnotation(key = "lock:long:#{#id}", waitTime = 10, leaseTime = 60)
```

### 3. 优先使用注解方式

注解方式更简洁，推荐使用：

```java
// ✅ 推荐：注解方式
@DistributedLockAnnotation(key = "lock:user:#{#id}")
public void updateUser(Long id) {
    // ...
}

// ⚠️ 也可以：编程方式（需要更多代码）
public void updateUser(Long id) {
    distributedLock.executeWithLock("lock:user:" + id, 5, 10, TimeUnit.SECONDS, () -> {
        // ...
        return null;
    });
}
```

### 4. 处理分布式锁服务不可用的情况

使用 `@Autowired(required = false)` 注入，并检查 null：

```java
// ✅ 正确
@Autowired(required = false)
private DistributedLock distributedLock;

public void doSomething() {
    if (distributedLock != null) {
        distributedLock.executeWithLock("lock:key", 5, 10, TimeUnit.SECONDS, () -> {
            // ...
            return null;
        });
    } else {
        // 降级处理
        // ...
    }
}
```

### 5. 使用 executeWithLock 确保锁释放

使用 `executeWithLock` 方法，确保锁一定会被释放：

```java
// ✅ 正确：使用 executeWithLock
distributedLock.executeWithLock("lock:key", 5, 10, TimeUnit.SECONDS, () -> {
    // 业务逻辑
    return result;
});

// ⚠️ 需要小心：手动加锁和释放锁
if (distributedLock.tryLock("lock:key", 5, 10, TimeUnit.SECONDS)) {
    try {
        // 业务逻辑
    } finally {
        distributedLock.unlock("lock:key");  // 必须确保释放锁
    }
}
```

### 6. 避免锁的粒度太大

锁的粒度应该尽可能小，只锁定必要的资源：

```java
// ✅ 正确：锁定特定资源
@DistributedLockAnnotation(key = "lock:user:#{#userId}")

// ❌ 错误：锁定所有用户
@DistributedLockAnnotation(key = "lock:user:all")
```

## 常见问题

### 1. 分布式锁服务注入为 null？

**答**：检查以下几点：
- Redis 是否已配置并启动
- `spring.redis.enabled` 是否为 `true`（默认启用）
- 是否使用 `@Autowired(required = false)` 注入
- Spring Boot 是否能够扫描到 `com.example.lock` 包

### 2. 获取锁失败？

**答**：可能的原因：
- 锁已被其他线程持有
- 等待时间太短
- Redis 连接异常

### 3. 锁不释放？

**答**：锁有过期时间，即使业务异常也会自动释放。如果业务执行时间超过锁的过期时间，锁会自动释放。

### 4. 如何禁用分布式锁？

**答**：设置 `spring.redis.enabled=false`，分布式锁服务将不会被创建。

### 5. SpEL 表达式不生效？

**答**：检查以下几点：
- key 中是否包含 `#`
- 方法参数名是否正确
- 参数是否为 null

## 版本历史

- **v1.0.0** (2024-01-01)
  - 初始版本
  - 提供分布式锁接口和实现
  - 支持注解方式和编程方式
  - 支持 SpEL 表达式

## 许可证

本项目采用 MIT 许可证。

## 联系方式

如有问题或建议，请联系开发团队。

