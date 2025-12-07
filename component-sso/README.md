# 单点登录组件 (Component SSO)

## 概述

Component SSO 是一个单点登录（Single Sign-On）组件，提供基于 JWT（JSON Web Token）的统一认证功能。支持 Token 生成、验证、用户信息提取、登出等功能，适用于微服务架构中的统一认证和前后端分离架构中的身份验证。

## 功能特性

### 核心功能

- **Token 生成**：根据用户ID和自定义声明生成 JWT Token
- **Token 验证**：验证 Token 的有效性、完整性和过期时间
- **用户信息提取**：从 Token 中提取用户ID和自定义声明
- **登出功能**：使 Token 失效（需要缓存服务支持）

### 技术特性

- 基于 JWT（JSON Web Token）标准
- 使用 HS512 算法签名，安全性高
- 支持自定义声明（Claims）
- 支持 Token 过期时间配置
- 支持缓存服务集成，实现 Token 黑名单功能
- 提供拦截器，自动验证 Token
- 完善的异常处理和日志记录

## 技术栈

- Spring Boot 2.7.18
- JWT (jjwt) 0.11.5+
- Lombok 1.18.28
- Component Cache（可选，用于 Token 黑名单）

## 快速开始

### 1. 添加依赖

在需要使用 SSO 组件的服务中，添加以下依赖：

```xml
<dependency>
    <groupId>com.example</groupId>
    <artifactId>component-sso</artifactId>
    <version>1.0.0</version>
</dependency>
```

### 2. 配置 JWT

在 `application.yml` 中配置 JWT 相关参数：

```yaml
jwt:
  secret: your-secret-key-should-be-at-least-256-bits-long-and-secure  # JWT 密钥，建议至少 256 位
  expiration: 86400  # Token 过期时间（秒），默认 86400 秒（24小时）
```

### 3. 启用组件

确保 Spring Boot 能够扫描到 SSO 组件：

```java
@SpringBootApplication
@ComponentScan(basePackages = {"com.example.yourservice", "com.example.sso"})
public class YourApplication {
    public static void main(String[] args) {
        SpringApplication.run(YourApplication.class, args);
    }
}
```

### 4. 使用 SSO 服务

```java
@Service
@RequiredArgsConstructor
public class AuthService {
    
    private final SSOService ssoService;
    
    public LoginResult login(String username, String password) {
        // 验证用户名和密码
        User user = validateUser(username, password);
        
        // 生成 Token
        Map<String, Object> claims = new HashMap<>();
        claims.put("username", user.getUsername());
        claims.put("role", user.getRole());
        String token = ssoService.generateToken(user.getId(), claims);
        
        LoginResult result = new LoginResult();
        result.setToken(token);
        result.setUserId(user.getId());
        return result;
    }
}
```

## 使用指南

### 生成 Token

#### 基本用法

```java
@Service
@RequiredArgsConstructor
public class AuthService {
    
    private final SSOService ssoService;
    
    public String login(User user) {
        // 生成 Token（不带自定义声明）
        String token = ssoService.generateToken(user.getId(), null);
        return token;
    }
}
```

#### 带自定义声明

```java
@Service
@RequiredArgsConstructor
public class AuthService {
    
    private final SSOService ssoService;
    
    public String login(User user) {
        // 构建自定义声明
        Map<String, Object> claims = new HashMap<>();
        claims.put("username", user.getUsername());
        claims.put("email", user.getEmail());
        claims.put("role", user.getRole());
        claims.put("permissions", user.getPermissions());
        
        // 生成 Token
        String token = ssoService.generateToken(user.getId(), claims);
        return token;
    }
}
```

### 验证 Token

#### 基本用法

```java
@Service
@RequiredArgsConstructor
public class UserService {
    
    private final SSOService ssoService;
    
    public User getCurrentUser(String token) {
        // 验证 Token
        if (!ssoService.validateToken(token)) {
            throw new BusinessException("Token 无效或已过期");
        }
        
        // 获取用户ID
        Long userId = ssoService.getUserIdFromToken(token);
        
        // 查询用户信息
        return userRepository.findById(userId);
    }
}
```

#### 获取自定义声明

```java
@Service
@RequiredArgsConstructor
public class UserService {
    
    private final SSOService ssoService;
    
    public UserInfo getCurrentUserInfo(String token) {
        // 验证 Token
        if (!ssoService.validateToken(token)) {
            throw new BusinessException("Token 无效或已过期");
        }
        
        // 获取所有声明
        Map<String, Object> claims = ssoService.getClaimsFromToken(token);
        
        UserInfo userInfo = new UserInfo();
        userInfo.setUserId((Long) claims.get("userId"));
        userInfo.setUsername((String) claims.get("username"));
        userInfo.setEmail((String) claims.get("email"));
        userInfo.setRole((String) claims.get("role"));
        
        return userInfo;
    }
}
```

### 使用拦截器

#### 配置拦截器

```java
@Configuration
@RequiredArgsConstructor
public class WebMvcConfig implements WebMvcConfigurer {
    
    private final SSOInterceptor ssoInterceptor;
    
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(ssoInterceptor)
                .addPathPatterns("/api/**")  // 拦截所有 /api/** 路径
                .excludePathPatterns(        // 排除不需要验证的路径
                    "/api/login",
                    "/api/register",
                    "/api/public/**"
                );
    }
}
```

#### 在 Controller 中获取用户信息

```java
@RestController
@RequestMapping("/api/user")
@RequiredArgsConstructor
public class UserController {
    
    /**
     * 获取当前用户信息
     */
    @GetMapping("/current")
    public Result<UserInfo> getCurrentUser(HttpServletRequest request) {
        // 从请求属性中获取用户ID（拦截器已设置）
        Long userId = (Long) request.getAttribute("userId");
        String token = (String) request.getAttribute("token");
        
        // 获取用户信息
        User user = userService.getUserById(userId);
        
        // 或者从 Token 中获取声明
        // Map<String, Object> claims = ssoService.getClaimsFromToken(token);
        
        UserInfo userInfo = new UserInfo();
        userInfo.setUserId(user.getId());
        userInfo.setUsername(user.getUsername());
        return Result.success(userInfo);
    }
}
```

### 登出功能

#### 基本用法

```java
@Service
@RequiredArgsConstructor
public class AuthService {
    
    private final SSOService ssoService;
    
    public void logout(String token) {
        // 登出（使 Token 失效）
        ssoService.logout(token);
    }
}
```

#### 在 Controller 中使用

```java
@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {
    
    private final SSOService ssoService;
    
    @PostMapping("/logout")
    public Result<Void> logout(HttpServletRequest request) {
        // 从请求头获取 Token
        String token = getTokenFromRequest(request);
        
        if (StringUtils.hasText(token)) {
            ssoService.logout(token);
        }
        
        return Result.success();
    }
    
    private String getTokenFromRequest(HttpServletRequest request) {
        String authHeader = request.getHeader("Authorization");
        if (StringUtils.hasText(authHeader) && authHeader.startsWith("Bearer ")) {
            return authHeader.substring(7);
        }
        return null;
    }
}
```

## 完整示例

### 示例1：登录和 Token 生成

```java
@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@Slf4j
public class AuthController {
    
    private final AuthService authService;
    
    /**
     * 用户登录
     */
    @PostMapping("/login")
    public Result<LoginResult> login(@RequestBody LoginDTO loginDTO) {
        LoginResult result = authService.login(loginDTO);
        return Result.success(result);
    }
}

@Service
@RequiredArgsConstructor
@Slf4j
public class AuthService {
    
    private final SSOService ssoService;
    private final UserRepository userRepository;
    
    /**
     * 用户登录
     */
    public LoginResult login(LoginDTO loginDTO) {
        // 验证用户名和密码
        User user = userRepository.findByUsername(loginDTO.getUsername());
        if (user == null || !validatePassword(user, loginDTO.getPassword())) {
            throw new BusinessException("用户名或密码错误");
        }
        
        // 构建 Token 声明
        Map<String, Object> claims = new HashMap<>();
        claims.put("username", user.getUsername());
        claims.put("email", user.getEmail());
        claims.put("role", user.getRole());
        
        // 生成 Token
        String token = ssoService.generateToken(user.getId(), claims);
        
        log.info("用户登录成功: userId={}, username={}", user.getId(), user.getUsername());
        
        LoginResult result = new LoginResult();
        result.setToken(token);
        result.setUserId(user.getId());
        result.setUsername(user.getUsername());
        return result;
    }
    
    private boolean validatePassword(User user, String password) {
        // 密码验证逻辑
        return user.getPassword().equals(encryptPassword(password));
    }
}
```

### 示例2：使用拦截器自动验证

```java
@Configuration
@RequiredArgsConstructor
public class WebMvcConfig implements WebMvcConfigurer {
    
    private final SSOInterceptor ssoInterceptor;
    
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(ssoInterceptor)
                .addPathPatterns("/api/**")
                .excludePathPatterns(
                    "/api/auth/login",
                    "/api/auth/register",
                    "/api/public/**"
                );
    }
}

@RestController
@RequestMapping("/api/user")
@RequiredArgsConstructor
public class UserController {
    
    private final UserService userService;
    
    /**
     * 获取当前用户信息（自动验证 Token）
     */
    @GetMapping("/current")
    public Result<UserInfo> getCurrentUser(HttpServletRequest request) {
        // 拦截器已验证 Token 并设置 userId
        Long userId = (Long) request.getAttribute("userId");
        
        User user = userService.getUserById(userId);
        
        UserInfo userInfo = new UserInfo();
        userInfo.setUserId(user.getId());
        userInfo.setUsername(user.getUsername());
        userInfo.setEmail(user.getEmail());
        
        return Result.success(userInfo);
    }
    
    /**
     * 更新用户信息（自动验证 Token）
     */
    @PutMapping("/profile")
    public Result<Void> updateProfile(
            HttpServletRequest request,
            @RequestBody UpdateProfileDTO dto) {
        Long userId = (Long) request.getAttribute("userId");
        userService.updateProfile(userId, dto);
        return Result.success();
    }
}
```

### 示例3：从 Token 中获取角色和权限

```java
@Service
@RequiredArgsConstructor
public class PermissionService {
    
    private final SSOService ssoService;
    
    /**
     * 检查用户是否有指定角色
     */
    public boolean hasRole(String token, String role) {
        if (!ssoService.validateToken(token)) {
            return false;
        }
        
        Map<String, Object> claims = ssoService.getClaimsFromToken(token);
        String userRole = (String) claims.get("role");
        return role.equals(userRole);
    }
    
    /**
     * 检查用户是否有指定权限
     */
    public boolean hasPermission(String token, String permission) {
        if (!ssoService.validateToken(token)) {
            return false;
        }
        
        Map<String, Object> claims = ssoService.getClaimsFromToken(token);
        @SuppressWarnings("unchecked")
        List<String> permissions = (List<String>) claims.get("permissions");
        return permissions != null && permissions.contains(permission);
    }
}

@RestController
@RequestMapping("/api/admin")
@RequiredArgsConstructor
public class AdminController {
    
    private final PermissionService permissionService;
    
    /**
     * 管理员操作（需要 ADMIN 角色）
     */
    @PostMapping("/operation")
    public Result<Void> adminOperation(
            HttpServletRequest request,
            @RequestBody AdminOperationDTO dto) {
        String token = getTokenFromRequest(request);
        
        if (!permissionService.hasRole(token, "ADMIN")) {
            throw new BusinessException("无权限执行此操作");
        }
        
        // 执行管理员操作
        // ...
        
        return Result.success();
    }
}
```

### 示例4：Token 刷新

```java
@Service
@RequiredArgsConstructor
@Slf4j
public class AuthService {
    
    private final SSOService ssoService;
    
    /**
     * 刷新 Token
     */
    public String refreshToken(String oldToken) {
        // 验证旧 Token
        if (!ssoService.validateToken(oldToken)) {
            throw new BusinessException("Token 无效或已过期");
        }
        
        // 获取用户ID和声明
        Long userId = ssoService.getUserIdFromToken(oldToken);
        Map<String, Object> claims = ssoService.getClaimsFromToken(oldToken);
        
        // 移除 JWT 标准声明（这些会自动生成）
        claims.remove("iat");  // 签发时间
        claims.remove("exp");  // 过期时间
        claims.remove("userId");  // 用户ID（会重新设置）
        
        // 生成新 Token
        String newToken = ssoService.generateToken(userId, claims);
        
        // 使旧 Token 失效
        ssoService.logout(oldToken);
        
        log.info("Token 刷新成功: userId={}", userId);
        return newToken;
    }
}
```

## 配置说明

### JWT 配置

在 `application.yml` 中配置：

```yaml
jwt:
  # JWT 密钥，建议至少 256 位，生产环境应该使用环境变量或配置中心
  secret: your-secret-key-should-be-at-least-256-bits-long-and-secure-for-production-use
  
  # Token 过期时间（秒），默认 86400 秒（24小时）
  expiration: 86400
```

### 缓存服务配置（可选）

如果需要实现 Token 黑名单功能，需要配置缓存服务：

```yaml
# Redis 配置（如果使用 Redis 作为缓存）
spring:
  redis:
    host: localhost
    port: 6379
    password: 
    database: 0
```

或者在 `pom.xml` 中添加 Caffeine 依赖（本地缓存）：

```xml
<dependency>
    <groupId>com.example</groupId>
    <artifactId>component-cache</artifactId>
    <version>1.0.0</version>
</dependency>
```

## 最佳实践

### 1. JWT 密钥安全

- **长度要求**：建议至少 256 位（32 字节）
- **随机性**：使用随机生成的密钥
- **存储方式**：生产环境使用环境变量或配置中心，不要硬编码

```yaml
# 推荐：使用环境变量
jwt:
  secret: ${JWT_SECRET:default-secret-key}
```

### 2. Token 过期时间

- **访问令牌（Access Token）**：建议 15 分钟到 2 小时
- **刷新令牌（Refresh Token）**：建议 7 天到 30 天
- **根据业务需求调整**：安全要求高的场景使用较短的过期时间

```yaml
jwt:
  expiration: 3600  # 1 小时
```

### 3. 自定义声明

- **不要存储敏感信息**：Token 可以被解码，不要存储密码等敏感信息
- **控制声明大小**：Token 会随每个请求发送，避免声明过大
- **使用有意义的键名**：使用清晰的键名，便于理解

```java
Map<String, Object> claims = new HashMap<>();
claims.put("username", user.getUsername());  // ✅ 推荐
claims.put("role", user.getRole());          // ✅ 推荐
claims.put("password", user.getPassword());  // ❌ 不推荐：敏感信息
```

### 4. Token 传输方式

- **推荐**：使用请求头 `Authorization: Bearer {token}`
- **备选**：使用请求参数 `?token={token}`（不推荐，可能泄露到日志）
- **避免**：使用 Cookie（除非需要支持跨域）

```java
// ✅ 推荐：使用请求头
String token = request.getHeader("Authorization").substring(7);

// ❌ 不推荐：使用请求参数（可能泄露到日志）
String token = request.getParameter("token");
```

### 5. 错误处理

- **统一错误响应**：使用统一的错误响应格式
- **记录日志**：记录 Token 验证失败的日志，便于排查问题
- **安全提示**：不要泄露详细的错误信息给客户端

```java
try {
    if (!ssoService.validateToken(token)) {
        throw new BusinessException("Token 无效或已过期");
    }
} catch (Exception e) {
    log.error("Token 验证失败", e);
    throw new BusinessException("身份验证失败");
}
```

### 6. 使用拦截器

- **统一验证**：使用拦截器统一验证 Token，避免重复代码
- **排除路径**：正确配置排除路径，避免拦截登录等公开接口
- **提取用户信息**：在拦截器中提取用户信息，设置到请求属性中

```java
@Override
public void addInterceptors(InterceptorRegistry registry) {
    registry.addInterceptor(ssoInterceptor)
            .addPathPatterns("/api/**")
            .excludePathPatterns(
                "/api/auth/login",
                "/api/auth/register",
                "/api/public/**"
            );
}
```

## 常见问题

### 1. Token 验证失败？

**答**：检查以下几点：
- JWT 密钥是否配置正确
- Token 是否已过期
- Token 格式是否正确
- 如果使用缓存服务，检查 Token 是否在缓存中

### 2. 如何实现 Token 刷新？

**答**：参考示例4，从旧 Token 中提取用户信息，生成新 Token，并使旧 Token 失效。

### 3. 如何实现单点登出？

**答**：
- 如果使用缓存服务，调用 `logout()` 方法删除缓存中的 Token
- 如果不使用缓存服务，JWT 是无状态的，无法主动使 Token 失效，只能等待 Token 过期

### 4. Token 可以存储哪些信息？

**答**：
- **可以存储**：用户ID、用户名、角色、权限等非敏感信息
- **不要存储**：密码、银行卡号等敏感信息
- **控制大小**：避免声明过大，影响性能

### 5. 如何支持多服务器？

**答**：
- 使用共享的缓存服务（如 Redis）存储 Token
- 所有服务器使用相同的 JWT 密钥
- 在缓存中存储 Token，实现 Token 黑名单功能

### 6. Token 过期时间如何选择？

**答**：
- **短过期时间**（15 分钟-2 小时）：安全性高，但需要频繁刷新
- **长过期时间**（24 小时-7 天）：用户体验好，但安全性较低
- **推荐方案**：使用访问令牌（短过期时间）+ 刷新令牌（长过期时间）

## 版本历史

- **v1.0.0** (2024-01-01)
  - 初始版本
  - 提供 Token 生成功能
  - 提供 Token 验证功能
  - 提供用户信息提取功能
  - 提供登出功能
  - 提供拦截器支持

## 许可证

本项目采用 MIT 许可证。

## 联系方式

如有问题或建议，请联系开发团队。

