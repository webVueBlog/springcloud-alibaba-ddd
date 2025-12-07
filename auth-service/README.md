# 认证服务 (Auth Service)

## 概述

认证服务是基于 DDD（领域驱动设计）架构的微服务，提供用户认证、授权、登录等核心功能。支持多种登录方式，包括用户名密码、手机号密码、手机号验证码、微信登录等。

## 功能特性

### 支持的登录方式

1. **用户名密码登录**
   - 使用用户名和密码进行登录认证
   - 密码使用 MD5(原始密码 + 盐值) 加密存储

2. **手机号密码登录**
   - 使用手机号和密码进行登录认证

3. **手机号验证码登录**
   - 发送6位数字验证码到手机
   - 验证码有效期5分钟
   - 如果用户不存在会自动注册（默认密码：123456）

4. **微信登录（PC端）**
   - 通过微信授权码进行登录
   - 需要用户已绑定微信账号

5. **小程序微信登录**
   - 通过微信小程序授权码进行登录
   - 如果用户不存在会自动注册

### 其他功能

- **验证码发送**：支持手机号和邮箱验证码发送
- **Token 管理**：基于 JWT 的 Token 生成和管理
- **密码加密**：使用 MD5 + 盐值的方式加密密码
- **健康检查**：提供健康检查接口用于服务监控

## 技术架构

### 分层架构（DDD）

```
auth-service/
├── interfaces/          # 接口层（Controller）
│   └── controller/      # REST API 控制器
├── application/         # 应用层
│   ├── dto/            # 数据传输对象
│   └── service/        # 应用服务
├── domain/             # 领域层
│   ├── model/          # 领域模型
│   ├── repository/     # 仓储接口
│   └── service/        # 领域服务
└── infrastructure/     # 基础设施层
    ├── mapper/         # MyBatis Mapper
    ├── po/             # 持久化对象
    └── repository/     # 仓储实现
```

### 依赖组件

- **component-sso**：单点登录和 Token 管理
- **component-encrypt**：密码加密服务
- **component-cache**：验证码缓存（可选，需要 Redis）

### 技术栈

- Spring Boot 2.7.18
- Spring Cloud 2021.0.8
- MyBatis-Plus 3.5.3.1
- MySQL 8.0
- JWT (jjwt 0.11.5)
- Lombok 1.18.28

## 快速开始

### 前置要求

1. **Java 8+**
2. **Maven 3.6+**
3. **MySQL 8.0+**（必需）
4. **Redis**（可选，用于验证码缓存和 SSO Token 存储）
5. **Nacos**（可选，用于服务注册与发现）

### 数据库配置

1. 执行 SQL 脚本创建数据库表（参考 `sql/init.sql`）

2. 配置数据库连接（`application.yml`）：
```yaml
spring:
  datasource:
    driver-class-name: com.mysql.cj.jdbc.Driver
    url: jdbc:mysql://localhost:3306/your_database?useUnicode=true&characterEncoding=utf8&serverTimezone=Asia/Shanghai
    username: your_username
    password: your_password
```

### 配置文件

编辑 `src/main/resources/application.yml`：

```yaml
server:
  port: 8081

spring:
  application:
    name: auth-service
  datasource:
    # 数据库配置
    driver-class-name: com.mysql.cj.jdbc.Driver
    url: jdbc:mysql://localhost:3306/ddd_auth?useUnicode=true&characterEncoding=utf8&serverTimezone=Asia/Shanghai
    username: root
    password: your_password
  redis:
    # Redis 配置（可选）
    enabled: true
    host: localhost
    port: 6379
    password: ""
    database: 0

jwt:
  # JWT 密钥（至少64字节）
  secret: your-very-long-secret-key-for-jwt-token-generation-this-is-a-very-long-secret-key-to-ensure-security
  expiration: 86400  # Token 有效期（秒），默认24小时
```

### 启动服务

```bash
# 在项目根目录执行
mvn clean install

# 启动认证服务
cd auth-service
mvn spring-boot:run
```

或者直接运行主类：
```bash
java -jar auth-service/target/auth-service-1.0.0.jar
```

## API 文档

### 基础信息

- **服务地址**：http://localhost:8081
- **API 前缀**：/api/auth
- **Content-Type**：application/json

### 接口列表

#### 1. 账号密码登录

**接口**：`POST /api/auth/login/username`

**请求体**：
```json
{
  "username": "admin",
  "password": "123456"
}
```

**响应**：
```json
{
  "code": 200,
  "message": "操作成功",
  "data": {
    "token": "eyJhbGciOiJIUzUxMiJ9...",
    "userId": 1,
    "username": "admin"
  },
  "timestamp": 1699000000000
}
```

#### 2. 手机号密码登录

**接口**：`POST /api/auth/login/phone`

**请求体**：
```json
{
  "phone": "13800138000",
  "password": "123456"
}
```

**响应**：同账号密码登录

#### 3. 手机号验证码登录

**接口**：`POST /api/auth/login/phone-code`

**请求参数**：
- `phone`：手机号（11位数字）
- `code`：验证码（6位数字）

**示例**：
```bash
POST /api/auth/login/phone-code?phone=13800138000&code=123456
```

**响应**：同账号密码登录

#### 4. 发送手机验证码

**接口**：`POST /api/auth/verify-code/phone`

**请求体**：
```json
{
  "phone": "13800138000"
}
```

**响应**：
```json
{
  "code": 200,
  "message": "操作成功",
  "data": null,
  "timestamp": 1699000000000
}
```

**说明**：
- 验证码为6位随机数字
- 验证码有效期5分钟
- 验证码存储在 Redis 中（如果 Redis 未配置，仅记录日志）

#### 5. 发送邮箱验证码

**接口**：`POST /api/auth/verify-code/email`

**请求体**：
```json
{
  "email": "test@example.com"
}
```

**响应**：同手机验证码

#### 6. 微信登录（PC端）

**接口**：`POST /api/auth/login/wechat`

**请求参数**：
- `code`：微信授权码

**示例**：
```bash
POST /api/auth/login/wechat?code=wx_code_123
```

**响应**：同账号密码登录

**说明**：需要用户已绑定微信账号，否则返回错误

#### 7. 小程序微信登录

**接口**：`POST /api/auth/login/miniprogram`

**请求参数**：
- `code`：微信小程序授权码

**示例**：
```bash
POST /api/auth/login/miniprogram?code=wx_mini_code_123
```

**响应**：同账号密码登录

**说明**：如果用户不存在会自动注册

#### 8. 健康检查

**接口**：`GET /api/auth/health`

**响应**：
```json
{
  "code": 200,
  "message": "操作成功",
  "data": "auth-service is running",
  "timestamp": 1699000000000
}
```

## 使用示例

### 使用 curl 测试

```bash
# 1. 用户名密码登录
curl -X POST http://localhost:8081/api/auth/login/username \
  -H "Content-Type: application/json" \
  -d '{"username":"admin","password":"123456"}'

# 2. 发送手机验证码
curl -X POST http://localhost:8081/api/auth/verify-code/phone \
  -H "Content-Type: application/json" \
  -d '{"phone":"13800138000"}'

# 3. 手机号验证码登录
curl -X POST "http://localhost:8081/api/auth/login/phone-code?phone=13800138000&code=123456"

# 4. 使用 Token 访问其他服务（通过网关）
TOKEN="your-token-here"
curl -X GET "http://localhost:8080/api/user/roles?userId=1" \
  -H "Authorization: Bearer $TOKEN"
```

### 使用 Postman 测试

1. 导入环境变量：
   - `base_url`: http://localhost:8081
   - `token`: (登录后获取)

2. 创建 Collection，包含所有 API

3. 设置 Authorization：
   - Type: Bearer Token
   - Token: {{token}}

## 配置说明

### 必需配置

- **数据库连接**：必须配置 MySQL 数据库连接
- **JWT 密钥**：必须配置 JWT 密钥（至少64字节）

### 可选配置

- **Redis**：如果未配置 Redis，验证码功能会降级（仅记录日志），SSO Token 功能会被禁用
- **Nacos**：如果未配置 Nacos，服务注册功能会被禁用

### 配置项说明

| 配置项 | 说明 | 默认值 | 是否必需 |
|--------|------|--------|----------|
| `server.port` | 服务端口 | 8081 | 否 |
| `spring.datasource.url` | 数据库连接URL | - | 是 |
| `spring.datasource.username` | 数据库用户名 | - | 是 |
| `spring.datasource.password` | 数据库密码 | - | 是 |
| `spring.redis.enabled` | 是否启用 Redis | false | 否 |
| `spring.redis.host` | Redis 主机地址 | localhost | 否 |
| `spring.redis.port` | Redis 端口 | 6379 | 否 |
| `jwt.secret` | JWT 密钥 | - | 是 |
| `jwt.expiration` | Token 有效期（秒） | 86400 | 否 |

## 安全说明

### 密码加密

- 使用 MD5(原始密码 + 盐值) 的方式加密密码
- 每个用户都有唯一的盐值
- 盐值使用时间戳的 MD5 值生成

### Token 安全

- Token 使用 JWT (HS512 算法) 生成
- Token 包含用户ID、用户名、手机号等信息
- Token 有效期可配置（默认24小时）
- Token 存储在 Redis 中（如果 Redis 可用）

### 验证码安全

- 验证码为6位随机数字
- 验证码有效期5分钟
- 验证码存储在 Redis 中，使用后自动删除
- 建议在生产环境中集成短信/邮件服务

## 错误码说明

| 错误码 | 说明 |
|--------|------|
| 200 | 成功 |
| 400 | 参数错误 |
| 401 | 未授权 |
| 403 | 无权限 |
| 404 | 资源不存在 |
| 500 | 服务器错误 |

## 开发说明

### 代码结构

遵循 DDD（领域驱动设计）分层架构：

- **interfaces 层**：REST API 接口，负责接收和响应 HTTP 请求
- **application 层**：应用服务，协调领域对象完成业务用例
- **domain 层**：领域模型和领域服务，包含核心业务逻辑
- **infrastructure 层**：基础设施实现，包括数据库访问、外部服务调用等

### 扩展开发

#### 添加新的登录方式

1. 在 `AuthService` 中添加新的登录方法
2. 在 `AuthController` 中添加对应的 API 接口
3. 在 `UserRepository` 中添加查询方法（如需要）

#### 集成短信服务

修改 `AuthService.sendPhoneVerifyCode()` 方法，调用实际的短信服务 API。

#### 集成邮件服务

修改 `AuthService.sendEmailVerifyCode()` 方法，调用实际的邮件服务 API。

#### 集成微信登录

修改 `AuthService.loginByWechat()` 和 `loginByMiniProgram()` 方法，调用微信 API 获取 openId。

## 常见问题

### 1. Redis 未配置时验证码功能不可用？

**答**：如果 Redis 未配置，验证码会记录在日志中，不会真正发送。建议配置 Redis 或集成实际的短信/邮件服务。

### 2. Token 生成失败？

**答**：检查 JWT 密钥配置是否正确，密钥长度至少需要64字节。同时检查用户数据是否完整（用户ID不能为空）。

### 3. 登录时提示"用户名或密码错误"？

**答**：
- 检查用户名是否正确
- 检查密码是否正确（注意密码是加密存储的）
- 检查数据库中用户状态是否为正常（status = 1）

### 4. 手机号验证码登录时用户自动注册的默认密码是什么？

**答**：默认密码是 `123456`，建议用户登录后立即修改密码。

## 版本历史

- **v1.0.0** (2024-01-01)
  - 初始版本
  - 支持用户名密码登录
  - 支持手机号密码登录
  - 支持手机号验证码登录
  - 支持微信登录（PC端和小程序）
  - 支持验证码发送功能

## 许可证

本项目采用 MIT 许可证。

## 联系方式

如有问题或建议，请联系开发团队。

