# 消息队列组件 (Component Message)

## 概述

Component Message 是一个基于 RocketMQ 的消息队列组件，提供统一的消息发送和消费接口。支持普通消息和延迟消息的发送，支持分布式消息队列，适用于异步处理、服务解耦、削峰填谷等场景。

## 功能特性

### 核心功能

- **消息发送**：支持发送普通消息和延迟消息
- **消息消费**：支持消息监听和消费
- **自动序列化**：消息对象自动序列化为 JSON 字符串
- **延迟消息**：支持 18 个延迟级别（1秒到2小时）
- **消息过滤**：支持通过 topic 和 tag 进行消息过滤

### 技术特性

- 基于 RocketMQ 和 RocketMQ Spring Boot Starter
- 使用 FastJSON2 进行消息序列化
- 自动配置，条件加载（RocketMQ 可用时才加载）
- 完善的异常处理和日志记录

## 技术栈

- Spring Boot 2.7.18
- RocketMQ Spring Boot Starter 2.2.3
- FastJSON2 2.0.60
- Lombok 1.18.28

## 快速开始

### 1. 添加依赖

在需要使用消息队列组件的服务中，添加以下依赖：

```xml
<dependency>
    <groupId>com.example</groupId>
    <artifactId>component-message</artifactId>
    <version>1.0.0</version>
</dependency>
```

### 2. 配置 RocketMQ

在 `application.yml` 中配置 RocketMQ 连接信息：

```yaml
rocketmq:
  name-server: localhost:9876  # RocketMQ NameServer 地址
  producer:
    group: default-producer-group  # 生产者组
    send-message-timeout: 3000  # 发送消息超时时间（毫秒）
```

### 3. 启用组件

确保 Spring Boot 能够扫描到消息队列组件：

```java
@SpringBootApplication
@ComponentScan(basePackages = {"com.example.yourservice", "com.example.message"})
public class YourApplication {
    public static void main(String[] args) {
        SpringApplication.run(YourApplication.class, args);
    }
}
```

### 4. 使用消息生产者

```java
@Service
@RequiredArgsConstructor
public class OrderService {
    
    @Autowired(required = false)
    private MessageProducer messageProducer;
    
    public void createOrder(OrderDTO orderDTO) {
        // 创建订单
        Order order = orderRepository.save(new Order(orderDTO));
        
        // 发送消息通知
        if (messageProducer != null) {
            messageProducer.send("order-topic", "create", order);
        }
    }
}
```

## 使用指南

### 消息发送

#### 发送普通消息

```java
@Service
@RequiredArgsConstructor
public class OrderService {
    
    @Autowired(required = false)
    private MessageProducer messageProducer;
    
    /**
     * 创建订单并发送消息
     */
    public void createOrder(OrderDTO orderDTO) {
        // 创建订单
        Order order = orderRepository.save(new Order(orderDTO));
        
        // 发送消息
        if (messageProducer != null) {
            messageProducer.send("order-topic", "create", order);
        }
    }
    
    /**
     * 更新订单并发送消息
     */
    public void updateOrder(Long id, OrderDTO orderDTO) {
        Order order = orderRepository.findById(id);
        order.setStatus(orderDTO.getStatus());
        orderRepository.save(order);
        
        // 发送消息
        if (messageProducer != null) {
            messageProducer.send("order-topic", "update", order);
        }
    }
}
```

#### 发送延迟消息

```java
@Service
@RequiredArgsConstructor
public class OrderService {
    
    @Autowired(required = false)
    private MessageProducer messageProducer;
    
    /**
     * 创建订单，30分钟后自动取消未支付订单
     */
    public void createOrder(OrderDTO orderDTO) {
        Order order = orderRepository.save(new Order(orderDTO));
        
        // 发送延迟消息（延迟级别 16，约 30 分钟后消费）
        if (messageProducer != null) {
            messageProducer.sendDelay("order-topic", "cancel", order, 16);
        }
    }
    
    /**
     * 发送验证码，5分钟后过期
     */
    public void sendVerifyCode(String phone, String code) {
        VerifyCodeDTO dto = new VerifyCodeDTO();
        dto.setPhone(phone);
        dto.setCode(code);
        
        // 发送延迟消息（延迟级别 5，约 1 分钟后消费）
        if (messageProducer != null) {
            messageProducer.sendDelay("verify-code-topic", "expire", dto, 5);
        }
    }
}
```

#### 延迟级别说明

RocketMQ 支持 18 个延迟级别：

| 延迟级别 | 延迟时间 | 使用场景 |
|---------|---------|---------|
| 1 | 1秒 | 快速重试 |
| 2 | 5秒 | 短期延迟 |
| 3 | 10秒 | 默认延迟 |
| 4 | 30秒 | 短期任务 |
| 5 | 1分钟 | 验证码过期 |
| 6 | 2分钟 | 短期任务 |
| 7 | 3分钟 | 短期任务 |
| 8 | 4分钟 | 短期任务 |
| 9 | 5分钟 | 短期任务 |
| 10 | 6分钟 | 短期任务 |
| 11 | 7分钟 | 短期任务 |
| 12 | 8分钟 | 短期任务 |
| 13 | 9分钟 | 短期任务 |
| 14 | 10分钟 | 中期任务 |
| 15 | 20分钟 | 中期任务 |
| 16 | 30分钟 | 订单自动取消 |
| 17 | 1小时 | 长期任务 |
| 18 | 2小时 | 长期任务 |

### 消息消费

#### 创建消息消费者

```java
@Component
@Slf4j
@RocketMQMessageListener(
    topic = "order-topic",
    consumerGroup = "order-consumer-group",
    consumeMode = org.apache.rocketmq.common.protocol.heartbeat.MessageModel.CLUSTERING
)
public class OrderMessageConsumer implements RocketMQListener<String> {
    
    private final OrderService orderService;
    
    public OrderMessageConsumer(OrderService orderService) {
        this.orderService = orderService;
    }
    
    @Override
    public void onMessage(String message) {
        try {
            log.info("收到订单消息: {}", message);
            
            // 反序列化消息
            OrderDTO orderDTO = JSON.parseObject(message, OrderDTO.class);
            
            // 处理消息
            orderService.processOrder(orderDTO);
            
            log.info("订单消息处理成功: orderId={}", orderDTO.getId());
        } catch (Exception e) {
            log.error("订单消息处理失败: message={}", message, e);
            // 抛出异常会导致消息重新投递
            throw new RuntimeException("消息处理失败", e);
        }
    }
}
```

#### 按 Tag 过滤消息

```java
@Component
@Slf4j
@RocketMQMessageListener(
    topic = "order-topic",
    selectorExpression = "create",  // 只消费 tag 为 "create" 的消息
    consumerGroup = "order-create-consumer-group"
)
public class OrderCreateConsumer implements RocketMQListener<String> {
    
    @Override
    public void onMessage(String message) {
        log.info("收到订单创建消息: {}", message);
        // 处理订单创建逻辑
    }
}
```

#### 消费模式

RocketMQ Spring Boot Starter 支持两种消费模式：

1. **CONCURRENTLY（并发消费）**：多个线程并发消费消息，提高吞吐量（默认，推荐）
2. **ORDERLY（顺序消费）**：单线程顺序消费消息，保证消息顺序

```java
import org.apache.rocketmq.spring.annotation.ConsumeMode;

@RocketMQMessageListener(
    topic = "order-topic",
    consumerGroup = "order-consumer-group",
    consumeMode = ConsumeMode.CONCURRENTLY  // 并发消费模式（默认）
)

// 如果需要顺序消费
@RocketMQMessageListener(
    topic = "order-topic",
    consumerGroup = "order-consumer-group",
    consumeMode = ConsumeMode.ORDERLY  // 顺序消费模式
)
```

**注意**：
- `ConsumeMode.CONCURRENTLY`：适用于大多数场景，提高吞吐量
- `ConsumeMode.ORDERLY`：适用于需要保证消息顺序的场景（如订单状态变更）

## 完整示例

### 示例1：订单创建和支付

```java
@Service
@RequiredArgsConstructor
@Slf4j
public class OrderService {
    
    private final OrderRepository orderRepository;
    private final PaymentService paymentService;
    
    @Autowired(required = false)
    private MessageProducer messageProducer;
    
    /**
     * 创建订单
     */
    public Order createOrder(OrderDTO orderDTO) {
        Order order = new Order();
        order.setUserId(orderDTO.getUserId());
        order.setProductId(orderDTO.getProductId());
        order.setAmount(orderDTO.getAmount());
        order.setStatus(OrderStatus.CREATED);
        order = orderRepository.save(order);
        
        // 发送订单创建消息
        if (messageProducer != null) {
            messageProducer.send("order-topic", "create", order);
            log.info("订单创建消息已发送: orderId={}", order.getId());
        }
        
        // 发送延迟消息：30分钟后自动取消未支付订单
        if (messageProducer != null) {
            messageProducer.sendDelay("order-topic", "auto-cancel", order, 16);
            log.info("订单自动取消延迟消息已发送: orderId={}, delayLevel=16", order.getId());
        }
        
        return order;
    }
}

/**
 * 订单创建消息消费者
 */
@Component
@Slf4j
@RocketMQMessageListener(
    topic = "order-topic",
    selectorExpression = "create",
    consumerGroup = "order-create-consumer-group"
)
public class OrderCreateConsumer implements RocketMQListener<String> {
    
    private final InventoryService inventoryService;
    private final NotificationService notificationService;
    
    public OrderCreateConsumer(InventoryService inventoryService, 
                              NotificationService notificationService) {
        this.inventoryService = inventoryService;
        this.notificationService = notificationService;
    }
    
    @Override
    public void onMessage(String message) {
        try {
            log.info("收到订单创建消息: {}", message);
            
            OrderDTO order = JSON.parseObject(message, OrderDTO.class);
            
            // 1. 扣减库存
            inventoryService.deductStock(order.getProductId(), order.getQuantity());
            
            // 2. 发送通知
            notificationService.sendOrderCreatedNotification(order.getUserId(), order.getId());
            
            log.info("订单创建消息处理成功: orderId={}", order.getId());
        } catch (Exception e) {
            log.error("订单创建消息处理失败: message={}", message, e);
            throw new RuntimeException("消息处理失败", e);
        }
    }
}

/**
 * 订单自动取消消息消费者
 */
@Component
@Slf4j
@RocketMQMessageListener(
    topic = "order-topic",
    selectorExpression = "auto-cancel",
    consumerGroup = "order-auto-cancel-consumer-group"
)
public class OrderAutoCancelConsumer implements RocketMQListener<String> {
    
    private final OrderService orderService;
    
    public OrderAutoCancelConsumer(OrderService orderService) {
        this.orderService = orderService;
    }
    
    @Override
    public void onMessage(String message) {
        try {
            log.info("收到订单自动取消消息: {}", message);
            
            OrderDTO order = JSON.parseObject(message, OrderDTO.class);
            
            // 检查订单状态，如果未支付则取消
            Order currentOrder = orderService.getOrderById(order.getId());
            if (currentOrder != null && currentOrder.getStatus() == OrderStatus.CREATED) {
                orderService.cancelOrder(order.getId(), "超时未支付，自动取消");
                log.info("订单自动取消成功: orderId={}", order.getId());
            } else {
                log.info("订单已支付或已取消，跳过自动取消: orderId={}", order.getId());
            }
        } catch (Exception e) {
            log.error("订单自动取消消息处理失败: message={}", message, e);
            throw new RuntimeException("消息处理失败", e);
        }
    }
}
```

### 示例2：用户注册和欢迎邮件

```java
@Service
@RequiredArgsConstructor
@Slf4j
public class UserService {
    
    private final UserRepository userRepository;
    
    @Autowired(required = false)
    private MessageProducer messageProducer;
    
    /**
     * 用户注册
     */
    public User registerUser(RegisterDTO registerDTO) {
        User user = new User();
        user.setUsername(registerDTO.getUsername());
        user.setEmail(registerDTO.getEmail());
        user = userRepository.save(user);
        
        // 发送用户注册消息
        if (messageProducer != null) {
            messageProducer.send("user-topic", "register", user);
            log.info("用户注册消息已发送: userId={}", user.getId());
        }
        
        return user;
    }
}

/**
 * 用户注册消息消费者
 */
@Component
@Slf4j
@RocketMQMessageListener(
    topic = "user-topic",
    selectorExpression = "register",
    consumerGroup = "user-register-consumer-group"
)
public class UserRegisterConsumer implements RocketMQListener<String> {
    
    private final EmailService emailService;
    private final SmsService smsService;
    
    public UserRegisterConsumer(EmailService emailService, SmsService smsService) {
        this.emailService = emailService;
        this.smsService = smsService;
    }
    
    @Override
    public void onMessage(String message) {
        try {
            log.info("收到用户注册消息: {}", message);
            
            User user = JSON.parseObject(message, User.class);
            
            // 发送欢迎邮件
            emailService.sendWelcomeEmail(user.getEmail(), user.getUsername());
            
            // 发送欢迎短信（可选）
            if (user.getPhone() != null) {
                smsService.sendWelcomeSms(user.getPhone());
            }
            
            log.info("用户注册消息处理成功: userId={}", user.getId());
        } catch (Exception e) {
            log.error("用户注册消息处理失败: message={}", message, e);
            throw new RuntimeException("消息处理失败", e);
        }
    }
}
```

### 示例3：库存扣减和补偿

```java
@Service
@RequiredArgsConstructor
@Slf4j
public class InventoryService {
    
    private final InventoryRepository inventoryRepository;
    
    @Autowired(required = false)
    private MessageProducer messageProducer;
    
    /**
     * 扣减库存
     */
    public void deductStock(Long productId, Integer quantity) {
        Inventory inventory = inventoryRepository.findByProductId(productId);
        if (inventory.getStock() < quantity) {
            throw new BusinessException("库存不足");
        }
        
        inventory.setStock(inventory.getStock() - quantity);
        inventoryRepository.save(inventory);
        
        // 发送库存变更消息
        if (messageProducer != null) {
            InventoryChangeDTO change = new InventoryChangeDTO();
            change.setProductId(productId);
            change.setQuantity(-quantity);
            change.setType("DEDUCT");
            messageProducer.send("inventory-topic", "change", change);
        }
    }
    
    /**
     * 回滚库存（补偿）
     */
    public void rollbackStock(Long productId, Integer quantity) {
        Inventory inventory = inventoryRepository.findByProductId(productId);
        inventory.setStock(inventory.getStock() + quantity);
        inventoryRepository.save(inventory);
        
        // 发送库存回滚消息
        if (messageProducer != null) {
            InventoryChangeDTO change = new InventoryChangeDTO();
            change.setProductId(productId);
            change.setQuantity(quantity);
            change.setType("ROLLBACK");
            messageProducer.send("inventory-topic", "rollback", change);
        }
    }
}
```

## 配置说明

### 必需配置

- **RocketMQ NameServer**：必须配置 RocketMQ NameServer 地址

### 可选配置

| 配置项 | 说明 | 默认值 | 是否必需 |
|--------|------|--------|----------|
| `rocketmq.name-server` | RocketMQ NameServer 地址 | - | 是 |
| `rocketmq.producer.group` | 生产者组 | default-producer-group | 否 |
| `rocketmq.producer.send-message-timeout` | 发送消息超时时间（毫秒） | 3000 | 否 |
| `rocketmq.consumer.group` | 消费者组 | - | 否（在注解中指定） |

### 配置示例

```yaml
rocketmq:
  name-server: localhost:9876
  producer:
    group: my-producer-group
    send-message-timeout: 5000
    max-message-size: 4194304  # 4MB
    compress-message-body-threshold: 4096
  consumer:
    pull-threshold-for-queue: 1000
    consume-message-batch-max-size: 1
```

## 最佳实践

### 1. 处理消息生产者不可用的情况

使用 `@Autowired(required = false)` 注入，并检查 null：

```java
// ✅ 正确
@Autowired(required = false)
private MessageProducer messageProducer;

public void doSomething() {
    if (messageProducer != null) {
        messageProducer.send("topic", "tag", data);
    } else {
        log.warn("消息生产者未配置，跳过消息发送");
    }
}

// ❌ 错误
@Autowired
private MessageProducer messageProducer;  // 如果 RocketMQ 未配置，会报错
```

### 2. 实现消息消费的幂等性

确保消息重复消费不会产生问题：

```java
@Override
public void onMessage(String message) {
    OrderDTO order = JSON.parseObject(message, OrderDTO.class);
    
    // 检查是否已处理（实现幂等性）
    if (orderService.isProcessed(order.getId())) {
        log.warn("订单已处理，跳过: orderId={}", order.getId());
        return;
    }
    
    // 处理消息
    orderService.processOrder(order);
    
    // 标记为已处理
    orderService.markAsProcessed(order.getId());
}
```

### 3. 合理使用延迟消息

根据业务需求选择合适的延迟级别：

```java
// 验证码过期：1分钟
messageProducer.sendDelay("verify-code-topic", "expire", dto, 5);

// 订单自动取消：30分钟
messageProducer.sendDelay("order-topic", "auto-cancel", order, 16);

// 定时任务：2小时
messageProducer.sendDelay("task-topic", "execute", task, 18);
```

### 4. 使用 Tag 进行消息过滤

使用 Tag 将不同类型的消息分开处理：

```java
// 发送订单创建消息
messageProducer.send("order-topic", "create", order);

// 发送订单更新消息
messageProducer.send("order-topic", "update", order);

// 发送订单取消消息
messageProducer.send("order-topic", "cancel", order);

// 消费者只消费特定 Tag 的消息
@RocketMQMessageListener(
    topic = "order-topic",
    selectorExpression = "create",  // 只消费 "create" tag
    consumerGroup = "order-create-consumer-group"
)
```

### 5. 处理消息消费异常

妥善处理异常，避免消息丢失：

```java
@Override
public void onMessage(String message) {
    try {
        // 处理消息
        processMessage(message);
    } catch (BusinessException e) {
        // 业务异常，记录日志但不重试
        log.error("业务异常，消息处理失败: message={}", message, e);
        // 不抛出异常，消息不会重新投递
    } catch (Exception e) {
        // 系统异常，抛出异常触发重试
        log.error("系统异常，消息处理失败: message={}", message, e);
        throw new RuntimeException("消息处理失败", e);
    }
}
```

### 6. 选择合适的消费模式

根据业务需求选择合适的消费模式：

```java
import org.apache.rocketmq.spring.annotation.ConsumeMode;

// 并发消费（推荐，提高吞吐量）
@RocketMQMessageListener(
    topic = "order-topic",
    consumerGroup = "order-consumer-group",
    consumeMode = ConsumeMode.CONCURRENTLY
)

// 顺序消费（需要保证消息顺序时使用）
@RocketMQMessageListener(
    topic = "order-topic",
    consumerGroup = "order-consumer-group",
    consumeMode = ConsumeMode.ORDERLY
)
```

## 常见问题

### 1. 消息生产者注入为 null？

**答**：检查以下几点：
- RocketMQ 是否已配置并启动
- `rocketmq.name-server` 是否配置
- 是否使用 `@Autowired(required = false)` 注入
- Spring Boot 是否能够扫描到 `com.example.message` 包

### 2. 消息发送失败？

**答**：检查以下几点：
- RocketMQ NameServer 是否可访问
- Topic 是否已创建
- 消息大小是否超过限制（默认 4MB）
- 网络连接是否正常

### 3. 消息消费不到？

**答**：检查以下几点：
- ConsumerGroup 是否配置正确
- Topic 是否已创建
- 消费者是否正常启动
- Tag 过滤表达式是否正确

### 4. 延迟消息不生效？

**答**：检查以下几点：
- 延迟级别是否在 1-18 范围内
- RocketMQ 是否支持延迟消息（需要配置）
- 消息是否成功发送

### 5. 如何禁用消息队列？

**答**：不配置 `rocketmq.name-server`，消息生产者将不会被创建。

### 6. 消息重复消费？

**答**：
- 实现消息消费的幂等性
- 使用数据库唯一约束防止重复处理
- 使用分布式锁防止并发处理

### 7. 如何监控消息？

**答**：
- 使用 RocketMQ Console 监控消息
- 查看日志中的消息发送和消费记录
- 使用 RocketMQ 的监控指标

## 版本历史

- **v1.0.0** (2024-01-01)
  - 初始版本
  - 提供消息发送和消费功能
  - 支持普通消息和延迟消息
  - 基于 RocketMQ 实现

## 许可证

本项目采用 MIT 许可证。

## 联系方式

如有问题或建议，请联系开发团队。

