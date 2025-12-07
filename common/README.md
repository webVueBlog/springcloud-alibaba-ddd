# 公共组件 (Common)

## 概述

Common 是一个公共基础组件，提供微服务架构中通用的功能模块，包括统一响应结果封装、异常处理、基础实体类、工具类等。所有微服务都可以依赖此组件来使用这些通用功能。

## 功能特性

### 1. 统一响应结果封装 (`Result`)

提供统一的 API 响应格式，包含：
- 状态码（code）
- 响应消息（message）
- 响应数据（data，泛型）
- 时间戳（timestamp）

### 2. 全局异常处理 (`GlobalExceptionHandler`)

统一处理应用中的各种异常，包括：
- 业务异常（BusinessException）
- 参数验证异常（MethodArgumentNotValidException、BindException、ConstraintViolationException）
- 非法参数异常（IllegalArgumentException）
- 运行时异常（RuntimeException）
- 其他所有异常（Exception）

### 3. 基础实体类 (`BaseEntity`)

提供所有领域实体类的基类，包含：
- 主键ID（使用雪花算法自动生成）
- 创建时间（createTime）
- 更新时间（updateTime）
- 逻辑删除标记（deleted）

### 4. 业务异常类 (`BusinessException`)

用于在业务逻辑中抛出业务异常，支持自定义错误码和错误消息。

### 5. 工具类

- **StringUtil**：字符串工具类（判空、手机号验证、邮箱验证、脱敏等）
- **DateUtil**：日期时间工具类（格式化、获取当前时间等）
- **PageUtil**：分页工具类（创建分页对象、分页请求参数、分页响应结果）

### 6. 常量类 (`Constants`)

定义系统常量，包括 Token 相关常量、用户信息相关常量等。

## 技术栈

- Spring Boot 2.7.18
- Spring Boot Starter Web
- Spring Boot Starter Validation
- MyBatis-Plus 3.5.3.1
- Lombok 1.18.28
- FastJSON2 2.0.60
- Hutool 5.8.20

## 快速开始

### 1. 添加依赖

在需要使用 Common 组件的服务中，添加以下依赖：

```xml
<dependency>
    <groupId>com.example</groupId>
    <artifactId>common</artifactId>
    <version>1.0.0</version>
</dependency>
```

### 2. 启用全局异常处理

确保 Spring Boot 能够扫描到 `GlobalExceptionHandler` 类：

```java
@SpringBootApplication
@ComponentScan(basePackages = {"com.example.yourservice", "com.example.common"})
public class YourApplication {
    public static void main(String[] args) {
        SpringApplication.run(YourApplication.class, args);
    }
}
```

## 使用指南

### 统一响应结果 (`Result`)

#### 成功响应

```java
// 无数据的成功响应
return Result.success();

// 有数据的成功响应
return Result.success(user);
return Result.success(userList);
```

#### 错误响应

```java
// 使用默认错误码（500）
return Result.error("操作失败");

// 指定错误码
return Result.error(400, "参数错误");
return Result.error(401, "未授权");
return Result.error(404, "资源不存在");
```

#### 响应示例

```json
{
  "code": 200,
  "message": "操作成功",
  "data": {
    "id": 1,
    "username": "admin"
  },
  "timestamp": 1699000000000
}
```

### 全局异常处理

#### 抛出业务异常

```java
// 使用默认错误码（500）
throw new BusinessException("用户名或密码错误");

// 指定错误码
throw new BusinessException(400, "参数不能为空");
throw new BusinessException(404, "用户不存在");
```

#### 参数验证异常

使用 Bean Validation 注解进行参数验证，异常会自动被 `GlobalExceptionHandler` 处理：

```java
@PostMapping("/user")
public Result<User> createUser(@Valid @RequestBody UserDTO userDTO) {
    // 如果验证失败，会自动返回 400 错误
    // ...
}
```

```java
public class UserDTO {
    @NotBlank(message = "用户名不能为空")
    private String username;
    
    @NotBlank(message = "密码不能为空")
    @Size(min = 6, max = 20, message = "密码长度必须在6-20位之间")
    private String password;
    
    @Pattern(regexp = "^1[3-9]\\d{9}$", message = "手机号格式不正确")
    private String phone;
}
```

### 基础实体类 (`BaseEntity`)

所有领域实体类继承 `BaseEntity`：

```java
@Data
@EqualsAndHashCode(callSuper = true)
public class User extends BaseEntity {
    private String username;
    private String password;
    private String phone;
    // ... 其他字段
}
```

### 工具类使用

#### StringUtil（字符串工具类）

```java
// 判断字符串是否为空
if (StringUtil.isEmpty(str)) {
    // 处理空字符串
}

// 验证手机号
if (StringUtil.isPhone("13800138000")) {
    // 手机号格式正确
}

// 验证邮箱
if (StringUtil.isEmail("test@example.com")) {
    // 邮箱格式正确
}

// 脱敏手机号
String masked = StringUtil.maskPhone("13800138000");
// 输出：138****8000

// 脱敏邮箱
String masked = StringUtil.maskEmail("test@example.com");
// 输出：te***@example.com
```

#### DateUtil（日期时间工具类）

```java
// 格式化日期时间
String dateTimeStr = DateUtil.formatDateTime(LocalDateTime.now());
// 输出：2024-01-01 12:00:00

// 格式化日期
String dateStr = DateUtil.formatDate(LocalDateTime.now());
// 输出：2024-01-01

// 获取当前时间
LocalDateTime now = DateUtil.now();
```

#### PageUtil（分页工具类）

```java
// 创建 MyBatis-Plus 分页对象
Page<User> page = PageUtil.createPage(1, 10);
List<User> users = userMapper.selectPage(page, null).getRecords();

// 使用分页请求参数
PageUtil.PageRequest request = new PageUtil.PageRequest();
request.setCurrent(1);
request.setSize(20);

// 使用分页响应结果
PageUtil.PageResult<User> result = new PageUtil.PageResult<>();
result.setTotal(100L);
result.setCurrent(1);
result.setSize(10);
result.setRecords(userList);
return Result.success(result);
```

### 常量使用

```java
// 获取 Token 请求头
String tokenHeader = Constants.TOKEN_HEADER; // "Authorization"

// 获取 Token 前缀
String tokenPrefix = Constants.TOKEN_PREFIX; // "Bearer "

// 从 JWT Token 中获取用户ID
Long userId = (Long) claims.get(Constants.USER_ID_KEY);

// 从 JWT Token 中获取用户名
String username = (String) claims.get(Constants.USERNAME_KEY);
```

## 完整示例

### Controller 示例

```java
@RestController
@RequestMapping("/api/user")
@RequiredArgsConstructor
public class UserController {
    
    private final UserService userService;
    
    /**
     * 创建用户
     */
    @PostMapping
    public Result<User> createUser(@Valid @RequestBody UserDTO userDTO) {
        User user = userService.createUser(userDTO);
        return Result.success(user);
    }
    
    /**
     * 根据ID查询用户
     */
    @GetMapping("/{id}")
    public Result<User> getUserById(@PathVariable Long id) {
        User user = userService.getUserById(id);
        if (user == null) {
            return Result.error(404, "用户不存在");
        }
        return Result.success(user);
    }
    
    /**
     * 分页查询用户
     */
    @GetMapping
    public Result<PageUtil.PageResult<User>> getUsers(
            @RequestParam(defaultValue = "1") Integer current,
            @RequestParam(defaultValue = "10") Integer size) {
        Page<User> page = PageUtil.createPage(current, size);
        IPage<User> userPage = userService.getUsers(page);
        
        PageUtil.PageResult<User> result = new PageUtil.PageResult<>();
        result.setTotal(userPage.getTotal());
        result.setCurrent((int) userPage.getCurrent());
        result.setSize((int) userPage.getSize());
        result.setRecords(userPage.getRecords());
        
        return Result.success(result);
    }
}
```

### Service 示例

```java
@Service
@RequiredArgsConstructor
public class UserService {
    
    private final UserRepository userRepository;
    
    public User createUser(UserDTO userDTO) {
        // 验证手机号
        if (!StringUtil.isPhone(userDTO.getPhone())) {
            throw new BusinessException(400, "手机号格式不正确");
        }
        
        // 验证邮箱
        if (StringUtil.isNotEmpty(userDTO.getEmail()) 
                && !StringUtil.isEmail(userDTO.getEmail())) {
            throw new BusinessException(400, "邮箱格式不正确");
        }
        
        User user = new User();
        user.setUsername(userDTO.getUsername());
        user.setPhone(userDTO.getPhone());
        user.setEmail(userDTO.getEmail());
        user.setCreateTime(DateUtil.now());
        
        return userRepository.save(user);
    }
    
    public User getUserById(Long id) {
        if (id == null || id <= 0) {
            throw new BusinessException(400, "用户ID不能为空");
        }
        return userRepository.findById(id);
    }
}
```

## 错误码说明

| 错误码 | 说明 | 使用场景 |
|--------|------|----------|
| 200 | 成功 | 操作成功 |
| 400 | 参数错误 | 参数验证失败、参数格式错误等 |
| 401 | 未授权 | Token 无效或过期 |
| 403 | 无权限 | 用户无权限访问资源 |
| 404 | 资源不存在 | 请求的资源不存在 |
| 500 | 服务器错误 | 系统异常、业务异常等 |

## 最佳实践

### 1. 统一使用 Result 封装响应

所有 Controller 方法的返回值都应该使用 `Result` 封装：

```java
// ✅ 正确
@GetMapping("/{id}")
public Result<User> getUserById(@PathVariable Long id) {
    return Result.success(user);
}

// ❌ 错误
@GetMapping("/{id}")
public User getUserById(@PathVariable Long id) {
    return user;
}
```

### 2. 使用业务异常处理业务错误

对于业务逻辑错误，使用 `BusinessException` 抛出：

```java
// ✅ 正确
if (user == null) {
    throw new BusinessException(404, "用户不存在");
}

// ❌ 错误
if (user == null) {
    return Result.error(404, "用户不存在");
}
```

### 3. 使用参数验证注解

使用 Bean Validation 注解进行参数验证，而不是在代码中手动验证：

```java
// ✅ 正确
@PostMapping
public Result<User> createUser(@Valid @RequestBody UserDTO userDTO) {
    // ...
}

// ❌ 错误
@PostMapping
public Result<User> createUser(@RequestBody UserDTO userDTO) {
    if (userDTO.getUsername() == null || userDTO.getUsername().isEmpty()) {
        return Result.error(400, "用户名不能为空");
    }
    // ...
}
```

### 4. 继承 BaseEntity

所有领域实体类都应该继承 `BaseEntity`：

```java
// ✅ 正确
@Data
@EqualsAndHashCode(callSuper = true)
public class User extends BaseEntity {
    // ...
}

// ❌ 错误
@Data
public class User {
    private Long id;
    private LocalDateTime createTime;
    // ...
}
```

### 5. 使用工具类

使用工具类而不是重复实现相同功能：

```java
// ✅ 正确
if (StringUtil.isEmpty(str)) {
    // ...
}

// ❌ 错误
if (str == null || str.trim().isEmpty()) {
    // ...
}
```

## 常见问题

### 1. 全局异常处理器不生效？

**答**：确保 Spring Boot 能够扫描到 `GlobalExceptionHandler` 类，在启动类中添加：

```java
@ComponentScan(basePackages = {"com.example.yourservice", "com.example.common"})
```

### 2. 如何自定义错误码？

**答**：使用 `BusinessException` 的构造函数指定错误码：

```java
throw new BusinessException(400, "自定义错误消息");
```

### 3. 如何扩展 Result 类？

**答**：可以继承 `Result` 类或创建新的响应类，但建议保持统一的响应格式。

### 4. 工具类方法不够用怎么办？

**答**：可以扩展工具类，添加新的静态方法，或者使用 Hutool 等第三方工具库。

## 版本历史

- **v1.0.0** (2024-01-01)
  - 初始版本
  - 提供统一响应结果封装
  - 提供全局异常处理
  - 提供基础实体类
  - 提供常用工具类

## 许可证

本项目采用 MIT 许可证。

## 联系方式

如有问题或建议，请联系开发团队。

