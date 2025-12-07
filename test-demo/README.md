# 测试 DEMO 模块

## 概述

test-demo 是一个测试模块，包含各种功能的测试示例，用于验证项目中各个服务和组件的功能是否正常。本模块提供了完整的测试示例和测试框架，帮助开发者快速编写和运行测试用例。

## 功能特性

### 1. 服务测试

- ✅ **认证服务测试** (`AuthServiceTest`)
  - 账号密码登录测试
  - 手机号密码登录测试
  - 手机号验证码登录测试
  - 微信登录测试
  - 验证码发送测试

- ✅ **秒杀服务测试** (`SeckillServiceTest`)
  - 秒杀下单测试
  - 库存初始化测试
  - 库存查询测试
  - 防重复下单测试
  - 分布式锁测试
  - 限流保护测试

### 2. 组件测试

- ✅ **组件测试** (`ComponentTest`)
  - 分布式锁组件测试
  - 限流组件测试
  - 缓存组件测试
  - 加密解密组件测试
  - 敏感词过滤组件测试

## 项目结构

```
test-demo/
├── src/
│   ├── main/
│   │   ├── java/
│   │   │   └── com/example/demo/
│   │   │       └── DemoTest.java          # 测试 DEMO 主类（示例）
│   │   └── resources/
│   │       └── application-test.yml       # 测试配置文件
│   └── test/
│       └── java/
│           └── com/example/demo/
│               ├── AuthServiceTest.java    # 认证服务测试
│               ├── SeckillServiceTest.java # 秒杀服务测试
│               └── ComponentTest.java     # 组件测试
├── pom.xml                                 # Maven 配置文件
└── README.md                               # 本文档
```

## 技术栈

- **Spring Boot Test**: 2.7.18
- **JUnit**: 4.x
- **Spring Test**: 集成测试支持
- **H2 Database**: 内存数据库（用于单元测试）

## 快速开始

### 1. 运行所有测试

```bash
mvn test
```

### 2. 运行特定测试类

```bash
# 运行认证服务测试
mvn test -Dtest=AuthServiceTest

# 运行秒杀服务测试
mvn test -Dtest=SeckillServiceTest

# 运行组件测试
mvn test -Dtest=ComponentTest
```

### 3. 运行特定测试方法

```bash
# 运行特定的测试方法
mvn test -Dtest=AuthServiceTest#testLoginByUsername
```

## 测试配置

### application-test.yml

测试配置文件位于 `src/main/resources/application-test.yml`，包含以下配置：

```yaml
spring:
  application:
    name: test-demo
  # 数据源配置（使用 H2 内存数据库）
  datasource:
    url: jdbc:h2:mem:testdb
    driver-class-name: org.h2.Driver
    username: sa
    password: 
  # Redis 配置（如果测试需要 Redis）
  # redis:
  #   enabled: true
  #   host: localhost
  #   port: 6379
  #   password: 
  #   database: 0

# 日志配置
logging:
  level:
    root: INFO
    com.example: DEBUG
```

## 测试说明

### 认证服务测试 (AuthServiceTest)

**测试内容：**
- `testLoginByUsername`: 测试账号密码登录
- `testLoginByPhone`: 测试手机号密码登录
- `testLoginByPhoneCode`: 测试手机号验证码登录
- `testLoginByWechat`: 测试微信登录
- `testSendPhoneVerifyCode`: 测试发送手机验证码
- `testSendEmailVerifyCode`: 测试发送邮箱验证码

**前置条件：**
- 数据库已初始化（执行 `sql/init.sql`）
- 测试账号已创建（admin/123456, test/123456）
- Redis 已启动（如果使用缓存）

**示例代码：**
```java
@Test
public void testLoginByUsername() {
    LoginDTO loginDTO = new LoginDTO();
    loginDTO.setUsername("admin");
    loginDTO.setPassword("123456");
    
    LoginResultDTO result = authService.loginByUsername(loginDTO);
    
    assertNotNull(result);
    assertNotNull(result.getToken());
    assertEquals("admin", result.getUsername());
}
```

### 秒杀服务测试 (SeckillServiceTest)

**测试内容：**
- `testSeckill`: 测试秒杀下单
- `testInitStock`: 测试库存初始化
- `testGetRemainingStock`: 测试库存查询
- `testPreventDuplicateOrder`: 测试防重复下单
- `testDistributedLock`: 测试分布式锁
- `testRateLimit`: 测试限流保护

**前置条件：**
- Redis 已启动（必需）
- 秒杀服务已启动或使用 Mock

**示例代码：**
```java
@Test
public void testSeckill() {
    Long activityId = 1L;
    Long userId = 123L;
    
    // 初始化库存
    seckillService.initStock(activityId, 100);
    
    // 执行秒杀
    SeckillResult result = seckillService.seckill(activityId, userId);
    
    assertTrue(result.getSuccess());
    assertNotNull(result.getOrderNo());
    
    // 验证库存
    Long remainingStock = seckillService.getRemainingStock(activityId);
    assertEquals(99L, remainingStock);
}
```

### 组件测试 (ComponentTest)

**测试内容：**
- `testDistributedLock`: 测试分布式锁组件
- `testRateLimit`: 测试限流组件
- `testCache`: 测试缓存组件
- `testEncrypt`: 测试加密解密组件
- `testSensitiveWord`: 测试敏感词过滤组件

**前置条件：**
- Redis 已启动（分布式锁、限流、缓存需要）
- 组件已正确配置

**示例代码：**
```java
@Test
public void testCache() {
    String key = "test:cache:key";
    String value = "test value";
    
    // 设置缓存
    cacheService.set(key, value, 60, TimeUnit.SECONDS);
    
    // 获取缓存
    String cachedValue = cacheService.get(key, String.class);
    assertEquals(value, cachedValue);
    
    // 删除缓存
    cacheService.delete(key);
}
```

## 测试最佳实践

### 1. 使用 @ActiveProfiles("test")

所有测试类都应该使用 `@ActiveProfiles("test")` 激活测试配置：

```java
@RunWith(SpringRunner.class)
@SpringBootTest
@ActiveProfiles("test")
public class AuthServiceTest {
    // ...
}
```

### 2. 使用断言验证结果

使用 JUnit 的断言方法验证测试结果：

```java
import static org.junit.Assert.*;

@Test
public void testLogin() {
    LoginResultDTO result = authService.loginByUsername(loginDTO);
    assertNotNull(result);
    assertNotNull(result.getToken());
    assertEquals("admin", result.getUsername());
}
```

### 3. 使用 Mock 对象

对于外部依赖，可以使用 Mock 对象：

```java
@Mock
private CacheService cacheService;

@Before
public void setUp() {
    MockitoAnnotations.initMocks(this);
}
```

### 4. 清理测试数据

使用 `@After` 或 `@AfterClass` 清理测试数据：

```java
@After
public void tearDown() {
    // 清理测试数据
    cacheService.delete("test:key");
}
```

### 5. 使用测试数据

创建测试数据工具类：

```java
public class TestDataUtil {
    public static LoginDTO createLoginDTO() {
        LoginDTO dto = new LoginDTO();
        dto.setUsername("admin");
        dto.setPassword("123456");
        return dto;
    }
}
```

## 注意事项

### 1. 服务依赖

- **数据库**：测试前需要确保数据库已初始化（执行 `sql/init.sql`）
- **Redis**：部分测试需要 Redis 支持（分布式锁、限流、缓存）
- **微服务**：集成测试需要启动对应的微服务

### 2. 测试数据

- 使用测试账号：`admin/123456`、`test/123456`
- 测试数据应该独立，不影响生产数据
- 测试后应该清理测试数据

### 3. 配置信息

- 测试配置使用 `application-test.yml`
- 确保配置信息正确（数据库连接、Redis 地址等）
- 可以使用环境变量覆盖配置

### 4. 并发测试

- 并发测试需要使用 `CountDownLatch` 和 `ExecutorService`
- 注意线程安全问题
- 验证分布式锁和限流是否生效

## 常见问题

### 1. 测试失败，提示连接数据库失败？

**答**：检查以下几点：
- 数据库是否已启动
- 数据库连接配置是否正确
- 是否使用了 H2 内存数据库（单元测试）

### 2. 测试失败，提示 Redis 连接失败？

**答**：检查以下几点：
- Redis 是否已启动
- Redis 连接配置是否正确
- 是否需要在测试配置中启用 Redis

### 3. 测试失败，提示服务未找到？

**答**：检查以下几点：
- 对应的微服务是否已启动
- 是否使用了 Mock 对象
- 服务发现配置是否正确

### 4. 如何编写集成测试？

**答**：
1. 使用 `@SpringBootTest` 注解
2. 启动完整的 Spring 上下文
3. 使用真实的数据库和服务
4. 确保测试数据独立

### 5. 如何编写单元测试？

**答**：
1. 使用 `@Mock` 和 `@InjectMocks` 注解
2. 使用 Mockito 框架
3. 不启动 Spring 上下文
4. 只测试单个方法或类

## 扩展测试

### 添加新的测试类

1. 在 `src/test/java/com/example/demo/` 目录下创建新的测试类
2. 使用 `@RunWith(SpringRunner.class)` 和 `@SpringBootTest` 注解
3. 使用 `@ActiveProfiles("test")` 激活测试配置
4. 编写测试方法，使用 `@Test` 注解

### 添加新的测试方法

1. 在现有的测试类中添加新的测试方法
2. 使用 `@Test` 注解
3. 编写测试逻辑和断言
4. 确保测试方法独立，不依赖其他测试方法

## 版本历史

- **v1.0.0** (2024-01-01)
  - 初始版本
  - 包含认证服务测试
  - 包含秒杀服务测试
  - 包含组件测试

## 许可证

本项目采用 MIT 许可证。

## 联系方式

如有问题或建议，请联系开发团队。
