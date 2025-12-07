# 国际化组件 (Component I18n)

## 概述

Component I18n 是一个国际化（Internationalization，简称 i18n）组件，提供多语言消息支持功能。基于 Spring 的 MessageSource 实现，支持从 properties 文件加载多语言消息，支持消息参数替换。

## 功能特性

### 核心功能

- **多语言支持**：支持中文、英文等多种语言
- **消息参数替换**：支持在消息中使用占位符，动态替换参数
- **自动语言检测**：支持从请求头、Session 中自动检测语言环境
- **消息文件管理**：基于 properties 文件管理多语言消息
- **默认消息**：如果消息不存在，使用 code 作为默认消息

### 技术特性

- 基于 Spring MessageSource 实现
- 支持 ResourceBundleMessageSource
- 支持 SessionLocaleResolver
- 完善的异常处理和日志记录
- UTF-8 编码支持中文

## 技术栈

- Spring Boot 2.7.18
- Spring Web
- Lombok 1.18.28

## 快速开始

### 1. 添加依赖

在需要使用国际化组件的服务中，添加以下依赖：

```xml
<dependency>
    <groupId>com.example</groupId>
    <artifactId>component-i18n</artifactId>
    <version>1.0.0</version>
</dependency>
```

### 2. 创建消息文件

在 `src/main/resources` 目录下创建消息文件：

**messages.properties**（默认消息）：
```properties
common.success=操作成功
common.error=操作失败
common.param.error=参数错误
auth.login.success=登录成功
auth.login.failed=登录失败
```

**messages_zh_CN.properties**（简体中文）：
```properties
common.success=操作成功
common.error=操作失败
common.param.error=参数错误
auth.login.success=登录成功
auth.login.failed=登录失败
```

**messages_en_US.properties**（美式英语）：
```properties
common.success=Operation successful
common.error=Operation failed
common.param.error=Parameter error
auth.login.success=Login successful
auth.login.failed=Login failed
```

### 3. 启用组件

确保 Spring Boot 能够扫描到国际化组件：

```java
@SpringBootApplication
@ComponentScan(basePackages = {"com.example.yourservice", "com.example.i18n"})
public class YourApplication {
    public static void main(String[] args) {
        SpringApplication.run(YourApplication.class, args);
    }
}
```

### 4. 使用国际化服务

```java
@Service
@RequiredArgsConstructor
public class UserService {
    
    private final I18nService i18nService;
    
    public Result<String> login(LoginDTO loginDTO) {
        // 获取国际化消息
        String successMessage = i18nService.getMessage("auth.login.success");
        return Result.success(successMessage);
    }
}
```

## 使用指南

### 基本使用

#### 获取当前语言环境的消息

```java
@Service
@RequiredArgsConstructor
public class OrderService {
    
    private final I18nService i18nService;
    
    public Result<String> createOrder(OrderDTO orderDTO) {
        // 获取当前语言环境的消息
        String message = i18nService.getMessage("common.success");
        // 中文环境：操作成功
        // 英文环境：Operation successful
        return Result.success(message);
    }
}
```

#### 获取指定语言的消息

```java
@Service
@RequiredArgsConstructor
public class OrderService {
    
    private final I18nService i18nService;
    
    public Result<String> createOrder(OrderDTO orderDTO) {
        // 获取指定语言的消息
        String message = i18nService.getMessage("common.success", "en-US");
        // 始终返回英文：Operation successful
        return Result.success(message);
    }
}
```

### 消息参数替换

#### 使用占位符

在消息文件中使用占位符 `{0}`、`{1}` 等：

**messages_zh_CN.properties**：
```properties
user.welcome=欢迎，{0}！
user.order.count=您有 {0} 个订单
user.balance=您的余额为 {0} 元
```

**messages_en_US.properties**：
```properties
user.welcome=Welcome, {0}!
user.order.count=You have {0} orders
user.balance=Your balance is {0}
```

#### 在代码中使用参数

```java
@Service
@RequiredArgsConstructor
public class UserService {
    
    private final I18nService i18nService;
    
    public String getWelcomeMessage(String username) {
        // 替换 {0} 为 username
        return i18nService.getMessage("user.welcome", username);
        // 中文：欢迎，张三！
        // 英文：Welcome, Zhang San!
    }
    
    public String getOrderCountMessage(Integer count) {
        // 替换 {0} 为 count
        return i18nService.getMessage("user.order.count", count);
        // 中文：您有 5 个订单
        // 英文：You have 5 orders
    }
    
    public String getBalanceMessage(BigDecimal balance) {
        // 替换 {0} 为 balance
        return i18nService.getMessage("user.balance", balance);
        // 中文：您的余额为 1000.00 元
        // 英文：Your balance is 1000.00
    }
}
```

#### 多个参数

```java
// 消息文件
user.info=用户 {0}，年龄 {1}，邮箱 {2}

// 代码
String message = i18nService.getMessage("user.info", "张三", 25, "zhangsan@example.com");
// 中文：用户 张三，年龄 25，邮箱 zhangsan@example.com
// 英文：User Zhang San, age 25, email zhangsan@example.com
```

### 从请求中获取语言环境

```java
@RestController
@RequestMapping("/api/user")
@RequiredArgsConstructor
public class UserController {
    
    private final I18nService i18nService;
    
    @GetMapping("/info")
    public Result<UserInfo> getUserInfo(HttpServletRequest request) {
        // 从请求中获取语言环境
        String locale = i18nService.getLocaleFromRequest(request);
        log.info("当前语言环境: {}", locale);
        
        // 根据语言环境返回不同的消息
        String message = i18nService.getMessage("common.success", locale);
        return Result.success(message);
    }
}
```

### 在 Controller 中使用

```java
@RestController
@RequestMapping("/api/order")
@RequiredArgsConstructor
public class OrderController {
    
    private final OrderService orderService;
    private final I18nService i18nService;
    
    @PostMapping
    public Result<Order> createOrder(@RequestBody OrderDTO orderDTO) {
        Order order = orderService.createOrder(orderDTO);
        
        // 返回国际化消息
        String message = i18nService.getMessage("order.create.success");
        return Result.success(order).setMessage(message);
    }
    
    @GetMapping("/{id}")
    public Result<Order> getOrder(@PathVariable Long id) {
        Order order = orderService.getOrderById(id);
        if (order == null) {
            // 返回国际化错误消息
            String message = i18nService.getMessage("order.not.found");
            return Result.error(404, message);
        }
        
        String message = i18nService.getMessage("order.get.success");
        return Result.success(order).setMessage(message);
    }
}
```

## 完整示例

### 示例1：统一响应消息国际化

```java
@RestController
@RequestMapping("/api/user")
@RequiredArgsConstructor
@Slf4j
public class UserController {
    
    private final UserService userService;
    private final I18nService i18nService;
    
    /**
     * 创建用户
     */
    @PostMapping
    public Result<User> createUser(@RequestBody UserDTO userDTO) {
        try {
            User user = userService.createUser(userDTO);
            String message = i18nService.getMessage("user.create.success");
            return Result.success(user).setMessage(message);
        } catch (BusinessException e) {
            String message = i18nService.getMessage("user.create.failed", e.getMessage());
            return Result.error(400, message);
        }
    }
    
    /**
     * 更新用户
     */
    @PutMapping("/{id}")
    public Result<User> updateUser(@PathVariable Long id, @RequestBody UserDTO userDTO) {
        try {
            User user = userService.updateUser(id, userDTO);
            String message = i18nService.getMessage("user.update.success");
            return Result.success(user).setMessage(message);
        } catch (BusinessException e) {
            String message = i18nService.getMessage("user.update.failed", e.getMessage());
            return Result.error(400, message);
        }
    }
    
    /**
     * 删除用户
     */
    @DeleteMapping("/{id}")
    public Result<Void> deleteUser(@PathVariable Long id) {
        try {
            userService.deleteUser(id);
            String message = i18nService.getMessage("user.delete.success");
            return Result.success().setMessage(message);
        } catch (BusinessException e) {
            String message = i18nService.getMessage("user.delete.failed", e.getMessage());
            return Result.error(400, message);
        }
    }
}
```

### 示例2：异常消息国际化

```java
@RestControllerAdvice
@Slf4j
@RequiredArgsConstructor
public class GlobalExceptionHandler {
    
    private final I18nService i18nService;
    
    @ExceptionHandler(BusinessException.class)
    public Result<?> handleBusinessException(BusinessException e, HttpServletRequest request) {
        // 从请求中获取语言环境
        String locale = i18nService.getLocaleFromRequest(request);
        
        // 获取国际化错误消息
        String message = i18nService.getMessage(e.getMessage(), locale);
        
        log.warn("业务异常: {}", message);
        return Result.error(e.getCode(), message);
    }
    
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public Result<?> handleValidationException(MethodArgumentNotValidException e, HttpServletRequest request) {
        String locale = i18nService.getLocaleFromRequest(request);
        
        String message = i18nService.getMessage("common.param.error", locale);
        
        log.warn("参数验证异常: {}", message);
        return Result.error(400, message);
    }
}
```

### 示例3：消息文件示例

**messages_zh_CN.properties**：
```properties
# 通用消息
common.success=操作成功
common.error=操作失败
common.param.error=参数错误
common.not.found=资源不存在
common.unauthorized=未授权
common.forbidden=无权限

# 用户相关消息
user.create.success=用户创建成功
user.create.failed=用户创建失败：{0}
user.update.success=用户更新成功
user.update.failed=用户更新失败：{0}
user.delete.success=用户删除成功
user.delete.failed=用户删除失败：{0}
user.not.found=用户不存在
user.welcome=欢迎，{0}！
user.login.success=登录成功
user.login.failed=登录失败：{0}

# 订单相关消息
order.create.success=订单创建成功
order.create.failed=订单创建失败：{0}
order.update.success=订单更新成功
order.update.failed=订单更新失败：{0}
order.not.found=订单不存在
order.status.invalid=订单状态无效：{0}
```

**messages_en_US.properties**：
```properties
# Common messages
common.success=Operation successful
common.error=Operation failed
common.param.error=Parameter error
common.not.found=Resource not found
common.unauthorized=Unauthorized
common.forbidden=Forbidden

# User messages
user.create.success=User created successfully
user.create.failed=User creation failed: {0}
user.update.success=User updated successfully
user.update.failed=User update failed: {0}
user.delete.success=User deleted successfully
user.delete.failed=User deletion failed: {0}
user.not.found=User not found
user.welcome=Welcome, {0}!
user.login.success=Login successful
user.login.failed=Login failed: {0}

# Order messages
order.create.success=Order created successfully
order.create.failed=Order creation failed: {0}
order.update.success=Order updated successfully
order.update.failed=Order update failed: {0}
order.not.found=Order not found
order.status.invalid=Invalid order status: {0}
```

## 配置说明

### 消息文件配置

消息文件需要放在 `src/main/resources` 目录下，命名规则：

- `messages.properties` - 默认消息（当找不到对应语言的消息时使用）
- `messages_zh_CN.properties` - 简体中文消息
- `messages_en_US.properties` - 美式英语消息
- `messages_en.properties` - 英语消息（通用）
- `messages_zh.properties` - 中文消息（通用）

### 语言环境解析

组件使用 `SessionLocaleResolver` 解析语言环境，支持以下方式：

1. **请求头 Accept-Language**：
   ```
   Accept-Language: zh-CN,zh;q=0.9,en;q=0.8
   ```

2. **Session 中的语言设置**：
   ```java
   // 设置语言
   LocaleResolver localeResolver = ...;
   localeResolver.setLocale(request, response, Locale.US);
   ```

3. **默认语言**：简体中文（zh-CN）

### 自定义配置

如果需要自定义配置，可以创建自己的配置类：

```java
@Configuration
public class CustomI18nConfig {
    
    @Bean
    public LocaleResolver localeResolver() {
        // 使用 CookieLocaleResolver 替代 SessionLocaleResolver
        CookieLocaleResolver resolver = new CookieLocaleResolver();
        resolver.setDefaultLocale(Locale.SIMPLIFIED_CHINESE);
        resolver.setCookieName("locale");
        resolver.setCookieMaxAge(3600);
        return resolver;
    }
    
    @Bean
    public ResourceBundleMessageSource messageSource() {
        ResourceBundleMessageSource messageSource = new ResourceBundleMessageSource();
        messageSource.setBasename("messages");
        messageSource.setDefaultEncoding("UTF-8");
        messageSource.setUseCodeAsDefaultMessage(true);
        // 设置缓存时间（秒）
        messageSource.setCacheSeconds(3600);
        return messageSource;
    }
}
```

## 最佳实践

### 1. 消息代码命名规范

使用点号分隔的层次结构：

```properties
# ✅ 推荐：使用层次结构
user.create.success=用户创建成功
user.update.success=用户更新成功
order.create.success=订单创建成功

# ❌ 不推荐：扁平结构
userCreateSuccess=用户创建成功
userUpdateSuccess=用户更新成功
```

### 2. 消息文件组织

按模块组织消息文件：

```properties
# 用户模块
user.create.success=用户创建成功
user.update.success=用户更新成功
user.delete.success=用户删除成功

# 订单模块
order.create.success=订单创建成功
order.update.success=订单更新成功
order.delete.success=订单删除成功

# 通用消息
common.success=操作成功
common.error=操作失败
```

### 3. 参数使用

合理使用参数，避免硬编码：

```properties
# ✅ 推荐：使用参数
user.welcome=欢迎，{0}！
user.order.count=您有 {0} 个订单

# ❌ 不推荐：硬编码
user.welcome.zhangsan=欢迎，张三！
user.welcome.lisi=欢迎，李四！
```

### 4. 错误消息处理

在异常处理器中使用国际化：

```java
@RestControllerAdvice
@RequiredArgsConstructor
public class GlobalExceptionHandler {
    
    private final I18nService i18nService;
    
    @ExceptionHandler(BusinessException.class)
    public Result<?> handleBusinessException(BusinessException e, HttpServletRequest request) {
        String locale = i18nService.getLocaleFromRequest(request);
        String message = i18nService.getMessage(e.getMessage(), locale);
        return Result.error(e.getCode(), message);
    }
}
```

### 5. 保持消息文件同步

确保所有语言的消息文件包含相同的 key：

```properties
# messages_zh_CN.properties
user.create.success=用户创建成功

# messages_en_US.properties（必须包含相同的 key）
user.create.success=User created successfully
```

### 6. 使用默认消息

如果消息不存在，使用 code 作为默认消息：

```java
// 如果 "unknown.message" 不存在，返回 "unknown.message"
String message = i18nService.getMessage("unknown.message");
```

## 常见问题

### 1. 消息不显示中文？

**答**：检查以下几点：
- 消息文件编码是否为 UTF-8
- `messageSource.setDefaultEncoding("UTF-8")` 是否配置
- IDE 是否将 properties 文件识别为 UTF-8 编码

### 2. 消息文件找不到？

**答**：检查以下几点：
- 消息文件是否在 `src/main/resources` 目录下
- 文件命名是否正确（messages_zh_CN.properties）
- basename 配置是否正确（"messages"）

### 3. 参数替换不生效？

**答**：检查以下几点：
- 占位符格式是否正确（{0}、{1} 等）
- 参数数量是否匹配
- 参数顺序是否正确

### 4. 如何添加新语言？

**答**：
1. 创建新的消息文件，如 `messages_fr_FR.properties`（法语）
2. 在文件中添加对应的消息
3. 确保所有 key 都存在

### 5. 如何切换语言？

**答**：可以通过以下方式切换语言：

```java
// 方式1：设置 Session
@GetMapping("/locale/{locale}")
public Result<String> setLocale(@PathVariable String locale, HttpServletRequest request, HttpServletResponse response) {
    LocaleResolver localeResolver = ...;
    localeResolver.setLocale(request, response, Locale.forLanguageTag(locale));
    return Result.success("语言切换成功");
}

// 方式2：使用请求头
// 在请求头中添加：Accept-Language: en-US
```

### 6. 消息文件可以放在其他位置吗？

**答**：可以，需要修改 basename 配置：

```java
@Bean
public ResourceBundleMessageSource messageSource() {
    ResourceBundleMessageSource messageSource = new ResourceBundleMessageSource();
    // 使用 classpath:i18n/messages
    messageSource.setBasename("i18n/messages");
    messageSource.setDefaultEncoding("UTF-8");
    return messageSource;
}
```

## 版本历史

- **v1.0.0** (2024-01-01)
  - 初始版本
  - 提供国际化消息获取功能
  - 支持多语言切换
  - 支持消息参数替换

## 许可证

本项目采用 MIT 许可证。

## 联系方式

如有问题或建议，请联系开发团队。

