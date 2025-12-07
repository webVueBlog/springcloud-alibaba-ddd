# 缓存组件 (Component Cache)

## 概述

Component Cache 是一个多实现的缓存组件，提供统一的缓存操作接口，支持对象的存储、获取、删除等操作。支持两种实现方式：
- **Redis 缓存**：基于 Redis 的分布式缓存，适用于分布式环境
- **Caffeine 本地缓存**：基于 Caffeine 的高性能本地缓存，适用于单机环境或热点数据

## 功能特性

### 核心功能

- **设置缓存**：支持设置缓存，可指定过期时间
- **获取缓存**：支持获取缓存，自动进行类型转换
- **删除缓存**：支持单个和批量删除
- **判断存在**：判断 key 是否存在
- **设置过期**：为已存在的 key 设置过期时间（Redis 支持，Caffeine 不支持）
- **模式匹配**：支持通配符匹配查找 key
- **数值操作**：支持数值的递增和递减操作

### 技术特性

#### Redis 缓存
- 基于 Redis 和 Spring Data Redis
- 使用 FastJSON2 进行序列化，支持复杂对象
- 支持分布式环境，数据可在多个服务间共享
- 自动配置，条件加载（Redis 可用时才加载）

#### Caffeine 本地缓存
- 基于 Caffeine 的高性能本地缓存
- 支持 LRU 自动淘汰策略
- 支持基于时间和基于大小的过期策略
- 线程安全，支持并发访问
- 适用于单机环境或热点数据缓存

## 技术栈

- Spring Boot 2.7.18
- Spring Data Redis
- Redis (Lettuce 连接池)
- Caffeine（本地缓存）
- FastJSON2 2.0.60
- Redisson 3.23.3

## 快速开始

### 1. 添加依赖

在需要使用缓存组件的服务中，添加以下依赖：

```xml
<dependency>
    <groupId>com.example</groupId>
    <artifactId>component-cache</artifactId>
    <version>1.0.0</version>
</dependency>
```

### 2. 配置缓存

#### 方式1：使用 Redis 缓存（推荐用于分布式环境）

在 `application.yml` 中配置 Redis 连接信息：

```yaml
spring:
  redis:
    enabled: true  # 启用 Redis（默认启用）
    host: localhost
    port: 6379
    password: ""  # 如果有密码，填写密码
    database: 0
```

#### 方式2：使用 Caffeine 本地缓存（推荐用于单机环境或热点数据）

在 `application.yml` 中配置 Caffeine 本地缓存：

```yaml
cache:
  local:
    enabled: true  # 启用本地缓存（默认不启用）
    max-size: 10000  # 最大缓存条目数，默认 10000
    expire-after-write: 3600  # 过期时间（秒），默认 3600 秒（1小时）
```

**注意**：Redis 缓存和 Caffeine 本地缓存可以同时启用，但建议根据实际场景选择使用。

### 3. 启用组件

确保 Spring Boot 能够扫描到缓存组件：

```java
@SpringBootApplication
@ComponentScan(basePackages = {"com.example.yourservice", "com.example.cache"})
public class YourApplication {
    public static void main(String[] args) {
        SpringApplication.run(YourApplication.class, args);
    }
}
```

或者导入配置类：

```java
@SpringBootApplication
@Import(com.example.cache.config.CacheConfig.class)
public class YourApplication {
    // ...
}
```

### 4. 使用缓存服务

```java
@Service
@RequiredArgsConstructor
public class UserService {
    
    @Autowired(required = false)
    private CacheService cacheService;
    
    public User getUserById(Long id) {
        String key = "user:" + id;
        
        // 先从缓存获取
        User user = cacheService != null ? cacheService.get(key, User.class) : null;
        if (user != null) {
            return user;
        }
        
        // 缓存不存在，从数据库查询
        user = userRepository.findById(id);
        
        // 存入缓存（30分钟过期）
        if (cacheService != null && user != null) {
            cacheService.set(key, user, 30, TimeUnit.MINUTES);
        }
        
        return user;
    }
}
```

## 使用指南

### 基本操作

#### 设置缓存

```java
@Autowired(required = false)
private CacheService cacheService;

// 设置缓存（不过期）
cacheService.set("user:1", user);

// 设置缓存（带过期时间）
cacheService.set("token:123", token, 30, TimeUnit.MINUTES);
cacheService.set("session:abc", session, 1, TimeUnit.HOURS);
cacheService.set("temp:data", data, 60, TimeUnit.SECONDS);
```

#### 获取缓存

```java
// 获取缓存
User user = cacheService.get("user:1", User.class);
String token = cacheService.get("token:123", String.class);
List<String> list = cacheService.get("list:1", List.class);
```

#### 删除缓存

```java
// 删除单个缓存
cacheService.delete("user:1");

// 批量删除
List<String> keys = Arrays.asList("user:1", "user:2", "user:3");
cacheService.deleteBatch(keys);
```

#### 判断 key 是否存在

```java
if (cacheService.exists("user:1")) {
    // key 存在
    User user = cacheService.get("user:1", User.class);
}
```

#### 设置过期时间

```java
// 为已存在的 key 设置过期时间
cacheService.expire("user:1", 30, TimeUnit.MINUTES);
```

#### 模式匹配查找 key

```java
// 查找所有以 "user:" 开头的 key
Set<String> keys = cacheService.keys("user:*");

// 查找所有匹配的 key
Set<String> allKeys = cacheService.keys("*");
```

**注意**：在生产环境中，如果 key 数量很大，`keys()` 操作可能很慢，建议谨慎使用。

#### 数值操作

```java
// 递增
Long count = cacheService.increment("counter:1");
// 如果 key 不存在，先初始化为 0 再加 1，返回 1
// 如果 key 存在且值为 5，则加 1 后返回 6

// 递减
Long count = cacheService.decrement("counter:1");
// 如果 key 不存在，先初始化为 0 再减 1，返回 -1
// 如果 key 存在且值为 5，则减 1 后返回 4
```

### 完整示例

#### 示例 1：用户信息缓存

```java
@Service
@RequiredArgsConstructor
@Slf4j
public class UserService {
    
    private final UserRepository userRepository;
    
    @Autowired(required = false)
    private CacheService cacheService;
    
    /**
     * 根据ID获取用户（带缓存）
     */
    public User getUserById(Long id) {
        String cacheKey = "user:" + id;
        
        // 从缓存获取
        if (cacheService != null) {
            User cachedUser = cacheService.get(cacheKey, User.class);
            if (cachedUser != null) {
                log.debug("从缓存获取用户: id={}", id);
                return cachedUser;
            }
        }
        
        // 从数据库查询
        User user = userRepository.findById(id);
        if (user == null) {
            return null;
        }
        
        // 存入缓存（30分钟过期）
        if (cacheService != null) {
            cacheService.set(cacheKey, user, 30, TimeUnit.MINUTES);
            log.debug("用户信息已缓存: id={}", id);
        }
        
        return user;
    }
    
    /**
     * 更新用户信息（清除缓存）
     */
    public User updateUser(Long id, UserDTO userDTO) {
        User user = userRepository.findById(id);
        if (user == null) {
            throw new BusinessException(404, "用户不存在");
        }
        
        // 更新用户信息
        user.setUsername(userDTO.getUsername());
        user = userRepository.save(user);
        
        // 清除缓存
        if (cacheService != null) {
            cacheService.delete("user:" + id);
            log.debug("用户缓存已清除: id={}", id);
        }
        
        return user;
    }
}
```

#### 示例 2：验证码缓存

```java
@Service
@RequiredArgsConstructor
@Slf4j
public class VerifyCodeService {
    
    @Autowired(required = false)
    private CacheService cacheService;
    
    /**
     * 发送验证码
     */
    public void sendVerifyCode(String phone) {
        // 生成6位随机验证码
        String code = String.format("%06d", new Random().nextInt(1000000));
        
        // 存入缓存（5分钟过期）
        if (cacheService != null) {
            String cacheKey = "verify_code:phone:" + phone;
            cacheService.set(cacheKey, code, 5, TimeUnit.MINUTES);
            log.info("验证码已发送: phone={}, code={}", phone, code);
        } else {
            log.warn("缓存服务未配置，验证码无法缓存: phone={}, code={}", phone, code);
        }
        
        // 这里应该调用短信服务发送验证码
        // smsService.send(phone, code);
    }
    
    /**
     * 验证验证码
     */
    public boolean verifyCode(String phone, String code) {
        if (cacheService == null) {
            log.warn("缓存服务未配置，跳过验证码验证");
            return false;
        }
        
        String cacheKey = "verify_code:phone:" + phone;
        String cachedCode = cacheService.get(cacheKey, String.class);
        
        if (cachedCode == null) {
            log.warn("验证码不存在或已过期: phone={}", phone);
            return false;
        }
        
        if (!cachedCode.equals(code)) {
            log.warn("验证码错误: phone={}", phone);
            return false;
        }
        
        // 验证成功，删除验证码
        cacheService.delete(cacheKey);
        log.info("验证码验证成功: phone={}", phone);
        return true;
    }
}
```

#### 示例 3：计数器缓存

```java
@Service
@RequiredArgsConstructor
@Slf4j
public class CounterService {
    
    @Autowired(required = false)
    private CacheService cacheService;
    
    /**
     * 增加访问计数
     */
    public Long incrementVisitCount(Long userId) {
        if (cacheService == null) {
            return 0L;
        }
        
        String cacheKey = "visit_count:user:" + userId;
        Long count = cacheService.increment(cacheKey);
        
        // 设置过期时间（24小时）
        cacheService.expire(cacheKey, 24, TimeUnit.HOURS);
        
        return count;
    }
    
    /**
     * 获取访问计数
     */
    public Long getVisitCount(Long userId) {
        if (cacheService == null) {
            return 0L;
        }
        
        String cacheKey = "visit_count:user:" + userId;
        String countStr = cacheService.get(cacheKey, String.class);
        
        return countStr != null ? Long.parseLong(countStr) : 0L;
    }
}
```

#### 示例 4：批量操作

```java
@Service
@RequiredArgsConstructor
@Slf4j
public class CacheCleanupService {
    
    @Autowired(required = false)
    private CacheService cacheService;
    
    /**
     * 清除所有用户缓存
     */
    public void clearUserCache() {
        if (cacheService == null) {
            return;
        }
        
        // 查找所有用户相关的 key
        Set<String> keys = cacheService.keys("user:*");
        
        if (keys != null && !keys.isEmpty()) {
            // 批量删除
            cacheService.deleteBatch(new ArrayList<>(keys));
            log.info("已清除用户缓存: count={}", keys.size());
        }
    }
    
    /**
     * 清除过期验证码
     */
    public void clearExpiredVerifyCodes() {
        if (cacheService == null) {
            return;
        }
        
        // 查找所有验证码相关的 key
        Set<String> keys = cacheService.keys("verify_code:*");
        
        if (keys != null && !keys.isEmpty()) {
            List<String> expiredKeys = new ArrayList<>();
            for (String key : keys) {
                if (!cacheService.exists(key)) {
                    expiredKeys.add(key);
                }
            }
            
            if (!expiredKeys.isEmpty()) {
                cacheService.deleteBatch(expiredKeys);
                log.info("已清除过期验证码: count={}", expiredKeys.size());
            }
        }
    }
}
```

## 配置说明

### Redis 缓存配置

#### 必需配置

- **Redis 连接**：必须配置 Redis 连接信息

#### 可选配置

| 配置项 | 说明 | 默认值 | 是否必需 |
|--------|------|--------|----------|
| `spring.redis.enabled` | 是否启用 Redis | true | 是 |
| `spring.redis.host` | Redis 主机地址 | localhost | 是 |
| `spring.redis.port` | Redis 端口 | 6379 | 是 |
| `spring.redis.password` | Redis 密码 | "" | 否 |
| `spring.redis.database` | Redis 数据库索引 | 0 | 否 |

### Caffeine 本地缓存配置

#### 必需配置

- **启用本地缓存**：设置 `cache.local.enabled=true`

#### 可选配置

| 配置项 | 说明 | 默认值 | 是否必需 |
|--------|------|--------|----------|
| `cache.local.enabled` | 是否启用本地缓存 | false | 是 |
| `cache.local.max-size` | 最大缓存条目数 | 10000 | 否 |
| `cache.local.expire-after-write` | 过期时间（秒） | 3600 | 否 |

### 配置示例

#### Redis 缓存配置示例

```yaml
spring:
  redis:
    enabled: true
    host: localhost
    port: 6379
    password: your_password  # 如果有密码
    database: 0
    timeout: 3000ms
    lettuce:
      pool:
        max-active: 8
        max-idle: 8
        min-idle: 0
```

#### Caffeine 本地缓存配置示例

```yaml
cache:
  local:
    enabled: true
    max-size: 50000  # 最大缓存 50000 条
    expire-after-write: 7200  # 2 小时过期
```

#### 同时启用两种缓存

```yaml
spring:
  redis:
    enabled: true
    host: localhost
    port: 6379

cache:
  local:
    enabled: true
    max-size: 10000
    expire-after-write: 3600
```

**注意**：如果同时启用两种缓存，Spring 会优先注入 Redis 缓存服务（如果可用）。如果需要使用本地缓存，可以指定注入 `CaffeineCacheService`。

## 最佳实践

### 1. 选择合适的缓存实现

根据实际场景选择合适的缓存实现：

```java
// 分布式环境：使用 Redis 缓存
@Autowired(required = false)
private RedisCacheService redisCacheService;

// 单机环境或热点数据：使用 Caffeine 本地缓存
@Autowired(required = false)
private CaffeineCacheService caffeineCacheService;

// 或者使用统一的接口（Spring 会自动选择可用的实现）
@Autowired(required = false)
private CacheService cacheService;
```

**选择建议**：
- **分布式环境**：使用 Redis 缓存，数据可在多个服务间共享
- **单机环境**：使用 Caffeine 本地缓存，性能更好
- **热点数据**：使用 Caffeine 本地缓存，减少网络开销
- **需要跨服务共享的数据**：使用 Redis 缓存

### 2. 使用命名空间

建议使用命名空间格式的 key，便于管理和查找：

```java
// ✅ 正确
cacheService.set("user:1", user);
cacheService.set("token:123", token);
cacheService.set("verify_code:phone:13800138000", code);

// ❌ 错误
cacheService.set("1", user);
cacheService.set("token123", token);
```

### 3. 设置合理的过期时间

根据业务需求设置合理的过期时间：

```java
// 用户信息：30分钟
cacheService.set("user:1", user, 30, TimeUnit.MINUTES);

// Token：24小时
cacheService.set("token:123", token, 24, TimeUnit.HOURS);

// 验证码：5分钟
cacheService.set("verify_code:phone:13800138000", code, 5, TimeUnit.MINUTES);

// 临时数据：1小时
cacheService.set("temp:data", data, 1, TimeUnit.HOURS);
```

### 4. 处理缓存服务不可用的情况

使用 `@Autowired(required = false)` 注入，并检查 null：

```java
// ✅ 正确
@Autowired(required = false)
private CacheService cacheService;

public User getUserById(Long id) {
    if (cacheService != null) {
        User user = cacheService.get("user:" + id, User.class);
        if (user != null) {
            return user;
        }
    }
    // 从数据库查询
    // ...
}

// ❌ 错误
@Autowired
private CacheService cacheService;  // 如果 Redis 未配置，会报错
```

### 5. 更新数据时清除缓存

更新数据时，应该清除相关的缓存：

```java
public User updateUser(Long id, UserDTO userDTO) {
    User user = userRepository.save(user);
    
    // 清除缓存
    if (cacheService != null) {
        cacheService.delete("user:" + id);
    }
    
    return user;
}
```

### 6. 避免使用 keys() 操作

在生产环境中，避免使用 `keys()` 操作，因为可能很慢：

```java
// ❌ 不推荐（生产环境）
Set<String> keys = cacheService.keys("user:*");

// ✅ 推荐：使用具体的 key
cacheService.delete("user:1");
```

### 7. 使用批量操作

需要删除多个 key 时，使用批量操作：

```java
// ✅ 正确
List<String> keys = Arrays.asList("user:1", "user:2", "user:3");
cacheService.deleteBatch(keys);

// ❌ 错误
cacheService.delete("user:1");
cacheService.delete("user:2");
cacheService.delete("user:3");
```

## 常见问题

### 1. 缓存服务注入为 null？

**答**：检查以下几点：
- **Redis 缓存**：
  - Redis 是否已配置并启动
  - `spring.redis.enabled` 是否为 `true`（默认启用）
- **Caffeine 本地缓存**：
  - `cache.local.enabled` 是否为 `true`（默认不启用）
- **通用**：
  - 是否使用 `@Autowired(required = false)` 注入
  - Spring Boot 是否能够扫描到 `com.example.cache` 包

### 2. 序列化失败？

**答**：确保对象是可序列化的，复杂对象会使用 FastJSON2 进行序列化。如果对象包含不可序列化的字段，可能需要特殊处理。

### 3. 缓存不生效？

**答**：检查以下几点：
- Redis 连接是否正常
- key 是否正确
- 过期时间是否已到
- 是否在更新数据时清除了缓存

### 4. 如何禁用缓存？

**答**：
- **禁用 Redis 缓存**：设置 `spring.redis.enabled=false`
- **禁用 Caffeine 本地缓存**：设置 `cache.local.enabled=false`（默认已禁用）
- **禁用所有缓存**：同时设置上述两个配置为 false

### 5. 如何切换 Redis 数据库？

**答**：配置 `spring.redis.database` 参数，默认为 0。

### 6. Redis 缓存和 Caffeine 本地缓存的区别？

**答**：

| 特性 | Redis 缓存 | Caffeine 本地缓存 |
|------|-----------|------------------|
| 适用场景 | 分布式环境 | 单机环境或热点数据 |
| 数据共享 | 支持跨服务共享 | 仅当前 JVM 实例 |
| 性能 | 网络延迟 | 内存访问，性能更高 |
| 持久化 | 支持 | 不支持 |
| 过期时间 | 支持为单个 key 设置 | 仅支持全局过期时间 |
| 内存占用 | 独立进程 | 当前 JVM 内存 |

### 7. 如何同时使用两种缓存？

**答**：可以同时启用两种缓存，但需要明确指定使用哪个实现：

```java
// 使用 Redis 缓存
@Autowired(required = false)
private RedisCacheService redisCacheService;

// 使用 Caffeine 本地缓存
@Autowired(required = false)
private CaffeineCacheService caffeineCacheService;

// 或者根据条件选择
@Autowired(required = false)
private CacheService cacheService;  // Spring 会优先注入 Redis 缓存（如果可用）
```

## 版本历史

- **v1.1.0** (2024-01-01)
  - 新增 Caffeine 本地缓存支持
  - 支持两种缓存实现：Redis 和 Caffeine
  - 优化缓存配置和文档

- **v1.0.0** (2024-01-01)
  - 初始版本
  - 提供基本的缓存操作接口
  - 基于 Redis 实现
  - 使用 FastJSON2 序列化

## 许可证

本项目采用 MIT 许可证。

## 联系方式

如有问题或建议，请联系开发团队。

