# 分库分表组件 (Component Sharding)

## 概述

Component Sharding 是一个分库分表组件，提供分片计算、表名生成、数据库名生成等功能。支持多种分片策略（取模、哈希等），适用于水平分表、水平分库等场景。

## 功能特性

### 核心功能

- **分片索引计算**：根据分片键和分片数量计算分片索引
- **分片表名生成**：根据表前缀和分片索引生成分片表名
- **分片数据库名生成**：根据数据库前缀和分片索引生成分片数据库名
- **多种分片策略**：支持取模、哈希等分片策略，可扩展

### 技术特性

- 支持多种分片键类型（Long、String、Number 等）
- 支持自定义分片策略
- 线程安全
- 完善的异常处理和日志记录
- 提供静态工具类，可在非 Spring 环境使用

## 技术栈

- Spring Boot 2.7.18
- Lombok 1.18.28

## 快速开始

### 1. 添加依赖

在需要使用分库分表组件的服务中，添加以下依赖：

```xml
<dependency>
    <groupId>com.example</groupId>
    <artifactId>component-sharding</artifactId>
    <version>1.0.0</version>
</dependency>
```

### 2. 启用组件

确保 Spring Boot 能够扫描到分库分表组件：

```java
@SpringBootApplication
@ComponentScan(basePackages = {"com.example.yourservice", "com.example.sharding"})
public class YourApplication {
    public static void main(String[] args) {
        SpringApplication.run(YourApplication.class, args);
    }
}
```

### 3. 使用分片服务

```java
@Service
@RequiredArgsConstructor
public class OrderService {
    
    private final ShardingService shardingService;
    
    public void createOrder(Order order) {
        // 计算分片索引
        int shardIndex = shardingService.calculateShardIndex(order.getOrderId(), 8);
        
        // 生成分片表名
        String tableName = shardingService.generateShardTableName("t_order", order.getOrderId(), 8);
        // 结果：t_order_0, t_order_1, ..., t_order_7
        
        // 执行数据库操作
        // ...
    }
}
```

## 使用指南

### 计算分片索引

#### 使用默认策略（取模）

```java
@Service
@RequiredArgsConstructor
public class UserService {
    
    private final ShardingService shardingService;
    
    public void saveUser(User user) {
        // 使用默认取模策略计算分片索引
        int shardIndex = shardingService.calculateShardIndex(user.getUserId(), 4);
        // 假设 userId = 12345，shardCount = 4
        // 结果：shardIndex = 12345 % 4 = 1
        
        // 根据分片索引选择数据源或表
        // ...
    }
}
```

#### 使用指定策略

```java
@Service
@RequiredArgsConstructor
public class UserService {
    
    private final ShardingService shardingService;
    
    public void saveUser(User user) {
        // 使用哈希策略计算分片索引
        int shardIndex = shardingService.calculateShardIndex(
            user.getUserId(), 
            4, 
            "hash"
        );
        
        // 根据分片索引选择数据源或表
        // ...
    }
}
```

### 生成分片表名

#### 基本用法

```java
@Service
@RequiredArgsConstructor
public class OrderService {
    
    private final ShardingService shardingService;
    
    public void createOrder(Order order) {
        // 生成分片表名
        String tableName = shardingService.generateShardTableName(
            "t_order", 
            order.getOrderId(), 
            8
        );
        // 假设 orderId = 12345，shardCount = 8
        // 结果：t_order_1（因为 12345 % 8 = 1）
        
        // 使用动态表名执行 SQL
        // String sql = "INSERT INTO " + tableName + " ...";
    }
}
```

#### 使用工具类

```java
public class OrderService {
    
    public void createOrder(Order order) {
        // 先计算分片索引
        int shardIndex = ShardingUtil.calculateShardIndex(order.getOrderId(), 8);
        
        // 生成分片表名
        String tableName = ShardingUtil.generateShardTableName("t_order", shardIndex);
        // 结果：t_order_1
        
        // 使用动态表名执行 SQL
        // ...
    }
}
```

### 生成分片数据库名

```java
@Service
@RequiredArgsConstructor
public class UserService {
    
    private final ShardingService shardingService;
    
    public void saveUser(User user) {
        // 生成分片数据库名
        String dbName = shardingService.generateShardDbName(
            "db", 
            user.getUserId(), 
            4
        );
        // 假设 userId = 12345，shardCount = 4
        // 结果：db1（因为 12345 % 4 = 1）
        
        // 根据数据库名选择数据源
        // DataSource dataSource = getDataSource(dbName);
    }
}
```

### 使用工具类（非 Spring 环境）

```java
public class OrderUtil {
    
    public static String getOrderTableName(Long orderId, int shardCount) {
        // 计算分片索引
        int shardIndex = ShardingUtil.calculateShardIndex(orderId, shardCount);
        
        // 生成分片表名
        return ShardingUtil.generateShardTableName("t_order", shardIndex);
    }
    
    public static String getUserDbName(Long userId, int shardCount) {
        // 计算分片索引
        int shardIndex = ShardingUtil.calculateShardIndexByUserId(userId, shardCount);
        
        // 生成分片数据库名
        return ShardingUtil.generateShardDbName("db", shardIndex);
    }
}
```

## 完整示例

### 示例1：按订单ID分表

```java
@RestController
@RequestMapping("/api/order")
@RequiredArgsConstructor
@Slf4j
public class OrderController {
    
    private final OrderService orderService;
    
    @PostMapping
    public Result<Order> createOrder(@RequestBody OrderDTO orderDTO) {
        Order order = orderService.createOrder(orderDTO);
        return Result.success(order);
    }
}

@Service
@RequiredArgsConstructor
@Slf4j
public class OrderService {
    
    private final ShardingService shardingService;
    private final OrderMapper orderMapper;
    
    private static final int SHARD_COUNT = 8;
    private static final String TABLE_PREFIX = "t_order";
    
    /**
     * 创建订单（自动分表）
     */
    public Order createOrder(OrderDTO orderDTO) {
        Order order = new Order();
        order.setOrderId(IdUtil.nextId()); // 生成订单ID
        order.setUserId(orderDTO.getUserId());
        order.setAmount(orderDTO.getAmount());
        
        // 生成分片表名
        String tableName = shardingService.generateShardTableName(
            TABLE_PREFIX, 
            order.getOrderId(), 
            SHARD_COUNT
        );
        
        log.info("创建订单: orderId={}, tableName={}", order.getOrderId(), tableName);
        
        // 使用动态表名插入数据
        orderMapper.insertOrder(tableName, order);
        
        return order;
    }
    
    /**
     * 查询订单（自动分表）
     */
    public Order getOrder(Long orderId) {
        // 生成分片表名
        String tableName = shardingService.generateShardTableName(
            TABLE_PREFIX, 
            orderId, 
            SHARD_COUNT
        );
        
        log.info("查询订单: orderId={}, tableName={}", orderId, tableName);
        
        // 使用动态表名查询数据
        return orderMapper.selectOrderById(tableName, orderId);
    }
}
```

### 示例2：按用户ID分库

```java
@Service
@RequiredArgsConstructor
@Slf4j
public class UserService {
    
    private final ShardingService shardingService;
    private final Map<String, DataSource> dataSourceMap;
    
    private static final int SHARD_COUNT = 4;
    private static final String DB_PREFIX = "db";
    
    /**
     * 保存用户（自动分库）
     */
    public void saveUser(User user) {
        // 生成分片数据库名
        String dbName = shardingService.generateShardDbName(
            DB_PREFIX, 
            user.getUserId(), 
            SHARD_COUNT
        );
        
        log.info("保存用户: userId={}, dbName={}", user.getUserId(), dbName);
        
        // 根据数据库名选择数据源
        DataSource dataSource = dataSourceMap.get(dbName);
        if (dataSource == null) {
            throw new BusinessException("数据源不存在: " + dbName);
        }
        
        // 使用指定数据源执行操作
        // JdbcTemplate jdbcTemplate = new JdbcTemplate(dataSource);
        // jdbcTemplate.update("INSERT INTO t_user ...", ...);
    }
}
```

### 示例3：使用自定义分片策略

```java
/**
 * 范围分片策略（示例）
 */
@Component
@Slf4j
public class RangeShardingStrategy implements ShardingStrategy {
    
    @Override
    public int calculateShardIndex(Object shardingKey, int shardCount) {
        if (shardingKey instanceof Number) {
            long keyValue = ((Number) shardingKey).longValue();
            // 范围分片：0-1000 -> 0, 1001-2000 -> 1, ...
            return (int) (keyValue / 1000) % shardCount;
        }
        throw new IllegalArgumentException("范围分片策略仅支持数字类型");
    }
    
    @Override
    public String getStrategyName() {
        return "range";
    }
}

@Service
@RequiredArgsConstructor
public class OrderService {
    
    private final ShardingService shardingService;
    
    public void createOrder(Order order) {
        // 使用范围分片策略
        int shardIndex = shardingService.calculateShardIndex(
            order.getOrderId(), 
            4, 
            "range"
        );
        
        // 根据分片索引选择表
        // ...
    }
}
```

### 示例4：MyBatis 动态表名

```java
@Mapper
public interface OrderMapper {
    
    /**
     * 插入订单（使用动态表名）
     */
    @Insert("INSERT INTO ${tableName} (order_id, user_id, amount) VALUES (#{order.orderId}, #{order.userId}, #{order.amount})")
    void insertOrder(@Param("tableName") String tableName, @Param("order") Order order);
    
    /**
     * 查询订单（使用动态表名）
     */
    @Select("SELECT * FROM ${tableName} WHERE order_id = #{orderId}")
    Order selectOrderById(@Param("tableName") String tableName, @Param("orderId") Long orderId);
}

@Service
@RequiredArgsConstructor
public class OrderService {
    
    private final ShardingService shardingService;
    private final OrderMapper orderMapper;
    
    public void createOrder(Order order) {
        // 生成分片表名
        String tableName = shardingService.generateShardTableName(
            "t_order", 
            order.getOrderId(), 
            8
        );
        
        // 使用动态表名插入
        orderMapper.insertOrder(tableName, order);
    }
}
```

## 分片策略说明

### 取模策略（Modulo）

**策略名称**：`modulo`

**算法原理**：
- 对于数字类型：`shardIndex = Math.abs(shardingKey) % shardCount`
- 对于字符串类型：先计算 hashCode，再取模

**优点**：
- 算法简单，计算效率高
- 数据分布相对均匀
- 适合大多数场景

**缺点**：
- 当分片数量变化时，需要数据迁移
- 对于字符串类型，可能存在哈希冲突

**使用示例**：
```java
int shardIndex = shardingService.calculateShardIndex(userId, 4, "modulo");
```

### 哈希策略（Hash）

**策略名称**：`hash`

**算法原理**：
- 计算分片键的 hashCode
- 取绝对值后取模：`shardIndex = Math.abs(hashCode) % shardCount`

**优点**：
- 适合字符串类型的分片键
- 数据分布相对均匀
- 计算效率高

**缺点**：
- 可能存在哈希冲突
- 当分片数量变化时，需要数据迁移

**使用示例**：
```java
int shardIndex = shardingService.calculateShardIndex(userId, 4, "hash");
```

### 自定义策略

实现 `ShardingStrategy` 接口，使用 `@Component` 注解注册：

```java
@Component
public class CustomShardingStrategy implements ShardingStrategy {
    
    @Override
    public int calculateShardIndex(Object shardingKey, int shardCount) {
        // 自定义分片逻辑
        // ...
    }
    
    @Override
    public String getStrategyName() {
        return "custom";
    }
}
```

## 配置说明

### 基本配置

组件默认启用，无需额外配置。确保 Spring Boot 能够扫描到 `com.example.sharding` 包即可。

### 分片数量配置

建议在配置文件中定义分片数量：

```yaml
sharding:
  order:
    shard-count: 8  # 订单表分片数量
  user:
    shard-count: 4  # 用户表分片数量
```

在代码中使用：

```java
@Service
@RequiredArgsConstructor
public class OrderService {
    
    private final ShardingService shardingService;
    
    @Value("${sharding.order.shard-count:8}")
    private int orderShardCount;
    
    public void createOrder(Order order) {
        String tableName = shardingService.generateShardTableName(
            "t_order", 
            order.getOrderId(), 
            orderShardCount
        );
        // ...
    }
}
```

## 最佳实践

### 1. 选择合适的分片键

- **用户ID**：适合按用户分片，数据分布均匀
- **订单ID**：适合按订单分片，避免跨分片查询
- **时间字段**：适合按时间分片，便于数据归档

### 2. 分片数量选择

- **2的幂次**：便于扩展，如 2、4、8、16
- **考虑数据量**：单表数据量建议控制在 500 万以内
- **考虑查询性能**：分片过多会增加查询复杂度

### 3. 避免跨分片查询

```java
// ❌ 不推荐：跨分片查询
public List<Order> getOrdersByUserId(Long userId) {
    // 需要查询所有分片
    List<Order> orders = new ArrayList<>();
    for (int i = 0; i < shardCount; i++) {
        String tableName = "t_order_" + i;
        orders.addAll(orderMapper.selectByUserId(tableName, userId));
    }
    return orders;
}

// ✅ 推荐：使用分片键查询
public Order getOrderByOrderId(Long orderId) {
    // 只查询一个分片
    String tableName = shardingService.generateShardTableName("t_order", orderId, shardCount);
    return orderMapper.selectByOrderId(tableName, orderId);
}
```

### 4. 分片表命名规范

- **表名格式**：`{prefix}_{shardIndex}`，如 `t_order_0`、`t_order_1`
- **数据库名格式**：`{prefix}{shardIndex}`，如 `db0`、`db1`
- **保持一致性**：所有分片表使用相同的命名规则

### 5. 数据迁移

当分片数量变化时，需要数据迁移：

```java
@Service
@RequiredArgsConstructor
public class ShardingMigrationService {
    
    private final ShardingService shardingService;
    
    /**
     * 数据迁移（示例）
     */
    public void migrateData(int oldShardCount, int newShardCount) {
        for (int oldIndex = 0; oldIndex < oldShardCount; oldIndex++) {
            String oldTable = "t_order_" + oldIndex;
            List<Order> orders = orderMapper.selectAll(oldTable);
            
            for (Order order : orders) {
                // 重新计算分片索引
                int newIndex = shardingService.calculateShardIndex(
                    order.getOrderId(), 
                    newShardCount
                );
                String newTable = "t_order_" + newIndex;
                
                // 迁移数据
                if (newIndex != oldIndex) {
                    orderMapper.insertOrder(newTable, order);
                    orderMapper.deleteOrder(oldTable, order.getOrderId());
                }
            }
        }
    }
}
```

## 常见问题

### 1. 如何选择分片策略？

**答**：根据分片键类型和业务需求选择：
- **数字类型**：推荐使用取模策略
- **字符串类型**：可以使用哈希策略
- **特殊需求**：实现自定义策略

### 2. 分片数量如何确定？

**答**：
- 考虑单表数据量（建议 500 万以内）
- 考虑查询性能（分片过多会增加查询复杂度）
- 考虑扩展性（建议使用 2 的幂次）

### 3. 如何避免跨分片查询？

**答**：
- 尽量使用分片键作为查询条件
- 避免使用非分片键的查询
- 如果必须跨分片查询，考虑使用中间表或汇总表

### 4. 分片数量变化时如何处理？

**答**：
- 需要数据迁移
- 建议在业务低峰期进行
- 可以使用双写策略，逐步迁移

### 5. 如何监控分片数据分布？

**答**：
- 定期统计各分片的数据量
- 监控各分片的查询性能
- 如果数据分布不均匀，考虑调整分片策略

### 6. 支持哪些分片键类型？

**答**：
- **Long**：推荐用于 ID 类型
- **String**：推荐用于业务编号
- **Number**：支持 Integer、Long、BigDecimal 等
- **其他类型**：使用 hashCode 计算

## 版本历史

- **v1.0.0** (2024-01-01)
  - 初始版本
  - 提供分片索引计算功能
  - 提供分片表名生成功能
  - 提供分片数据库名生成功能
  - 支持取模和哈希分片策略
  - 支持自定义分片策略

## 许可证

本项目采用 MIT 许可证。

## 联系方式

如有问题或建议，请联系开发团队。

