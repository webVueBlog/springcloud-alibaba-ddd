# 用户服务（User Service）

## 概述

用户服务是基于 DDD（领域驱动设计）架构的微服务，负责处理用户角色、权限管理等业务功能。提供用户角色查询、权限查询、权限验证等功能，支持基于角色的访问控制（RBAC）。

## 技术栈

- **Spring Boot**: 2.7.18
- **Spring Cloud Alibaba**: 2021.0.5.0
- **MyBatis Plus**: 3.5.3.1
- **MySQL**: 8.0.33
- **Redis**: 缓存用户角色和权限（可选）
- **Nacos**: 服务注册与发现（可选）

## 项目结构

```
user-service/
├── domain/                      # 领域层
│   ├── model/                  # 领域模型
│   │   ├── Role.java           # 角色领域模型
│   │   ├── Permission.java     # 权限领域模型
│   │   ├── UserRole.java       # 用户角色关联模型
│   │   └── RolePermission.java # 角色权限关联模型
│   └── repository/              # 仓储接口
│       ├── RoleRepository.java
│       ├── PermissionRepository.java
│       ├── UserRoleRepository.java
│       └── RolePermissionRepository.java
├── application/                # 应用层
│   └── service/                # 应用服务
│       └── UserService.java   # 用户应用服务
├── infrastructure/             # 基础设施层
│   ├── repository/              # 仓储实现
│   │   ├── RoleRepositoryImpl.java
│   │   ├── PermissionRepositoryImpl.java
│   │   ├── UserRoleRepositoryImpl.java
│   │   └── RolePermissionRepositoryImpl.java
│   ├── mapper/                  # MyBatis Mapper
│   │   ├── RoleMapper.java
│   │   ├── PermissionMapper.java
│   │   ├── UserRoleMapper.java
│   │   └── RolePermissionMapper.java
│   └── po/                      # 持久化对象
│       ├── RolePO.java
│       ├── PermissionPO.java
│       ├── UserRolePO.java
│       └── RolePermissionPO.java
├── interfaces/                  # 接口层
│   └── controller/              # REST控制器
│       └── UserController.java
└── UserApplication.java        # 启动类
```

## DDD 架构说明

### 分层职责

1. **领域层（domain）**
   - 领域模型：角色、权限聚合根，包含业务属性
   - 仓储接口：定义数据访问接口，不依赖具体实现

2. **应用层（application）**
   - 应用服务：协调领域对象完成业务用例

3. **基础设施层（infrastructure）**
   - 仓储实现：实现领域层的仓储接口
   - Mapper：MyBatis Plus Mapper 接口
   - PO：持久化对象，对应数据库表

4. **接口层（interfaces）**
   - Controller：REST API 接口，接收 HTTP 请求

## 功能特性

### 1. 角色管理

- ✅ **查询用户角色**：根据用户ID查询用户拥有的所有角色
- ✅ **角色缓存**：使用 Redis 缓存用户角色，提高查询性能
- ✅ **角色状态管理**：支持角色的启用和禁用

### 2. 权限管理

- ✅ **查询用户权限**：根据用户ID查询用户拥有的所有权限（通过角色间接获得）
- ✅ **权限缓存**：使用 Redis 缓存用户权限，提高查询性能
- ✅ **权限验证**：检查用户是否拥有指定权限
- ✅ **权限继承**：用户通过角色间接拥有权限

### 3. 关联管理

- ✅ **用户角色关联**：管理用户和角色的多对多关系
- ✅ **角色权限关联**：管理角色和权限的多对多关系

## RBAC 模型说明

### 权限继承

```
用户 (User)
  ↓ (通过 UserRole)
角色 (Role)
  ↓ (通过 RolePermission)
权限 (Permission)
```

**说明：**
- 用户通过角色间接拥有权限
- 用户权限 = 用户所有角色的权限集合（去重）
- 一个用户可以有多个角色
- 一个角色可以拥有多个权限

### 数据表关系

```
sys_user (用户表)
  ↓
sys_user_role (用户角色关联表)
  ↓
sys_role (角色表)
  ↓
sys_role_permission (角色权限关联表)
  ↓
sys_permission (权限表)
```

## API 接口

### 1. 获取用户角色

**GET** `/api/user/roles?userId={userId}`

**请求参数：**
- `userId`：用户ID（Long 类型，必须大于 0）

**响应示例：**
```json
{
  "code": 200,
  "message": "操作成功",
  "data": [
    {
      "id": 1,
      "roleCode": "ADMIN",
      "roleName": "管理员",
      "description": "系统管理员，拥有所有权限",
      "status": 1,
      "createTime": "2024-01-01T10:00:00",
      "updateTime": "2024-01-01T10:00:00"
    }
  ]
}
```

**使用示例：**
```bash
curl -X GET "http://localhost:8082/api/user/roles?userId=1"
```

### 2. 获取用户权限

**GET** `/api/user/permissions?userId={userId}`

**请求参数：**
- `userId`：用户ID（Long 类型，必须大于 0）

**响应示例：**
```json
{
  "code": 200,
  "message": "操作成功",
  "data": [
    {
      "id": 1,
      "permissionCode": "USER_VIEW",
      "permissionName": "查看用户",
      "resource": "/api/user/**",
      "method": "GET",
      "description": "查看用户信息",
      "status": 1,
      "createTime": "2024-01-01T10:00:00",
      "updateTime": "2024-01-01T10:00:00"
    }
  ]
}
```

**使用示例：**
```bash
curl -X GET "http://localhost:8082/api/user/permissions?userId=1"
```

### 3. 检查用户是否有权限

**GET** `/api/user/has-permission?userId={userId}&permissionCode={permissionCode}`

**请求参数：**
- `userId`：用户ID（Long 类型，必须大于 0）
- `permissionCode`：权限编码（String 类型，不能为空）

**响应示例：**
```json
{
  "code": 200,
  "message": "操作成功",
  "data": true
}
```

**使用示例：**
```bash
curl -X GET "http://localhost:8082/api/user/has-permission?userId=1&permissionCode=USER_VIEW"
```

## 数据库设计

### 角色表（sys_role）

| 字段名 | 类型 | 说明 | 约束 |
|--------|------|------|------|
| id | BIGINT | 主键ID | PRIMARY KEY, AUTO_INCREMENT |
| role_code | VARCHAR(50) | 角色编码（唯一） | UNIQUE, NOT NULL |
| role_name | VARCHAR(100) | 角色名称 | NOT NULL |
| description | VARCHAR(255) | 描述 | NULL |
| status | INT | 状态：0-禁用，1-正常 | DEFAULT 1 |
| create_time | DATETIME | 创建时间 | DEFAULT CURRENT_TIMESTAMP |
| update_time | DATETIME | 更新时间 | DEFAULT CURRENT_TIMESTAMP ON UPDATE |
| deleted | INT | 删除标识：0-未删除，1-已删除 | DEFAULT 0 |

### 权限表（sys_permission）

| 字段名 | 类型 | 说明 | 约束 |
|--------|------|------|------|
| id | BIGINT | 主键ID | PRIMARY KEY, AUTO_INCREMENT |
| permission_code | VARCHAR(50) | 权限编码（唯一） | UNIQUE, NOT NULL |
| permission_name | VARCHAR(100) | 权限名称 | NOT NULL |
| resource | VARCHAR(255) | 资源路径 | NULL |
| method | VARCHAR(10) | 请求方法 | NULL |
| description | VARCHAR(255) | 描述 | NULL |
| status | INT | 状态：0-禁用，1-正常 | DEFAULT 1 |
| create_time | DATETIME | 创建时间 | DEFAULT CURRENT_TIMESTAMP |
| update_time | DATETIME | 更新时间 | DEFAULT CURRENT_TIMESTAMP ON UPDATE |
| deleted | INT | 删除标识：0-未删除，1-已删除 | DEFAULT 0 |

### 用户角色关联表（sys_user_role）

| 字段名 | 类型 | 说明 | 约束 |
|--------|------|------|------|
| id | BIGINT | 主键ID | PRIMARY KEY, AUTO_INCREMENT |
| user_id | BIGINT | 用户ID | NOT NULL, INDEX |
| role_id | BIGINT | 角色ID | NOT NULL, INDEX |
| create_time | DATETIME | 创建时间 | DEFAULT CURRENT_TIMESTAMP |
| update_time | DATETIME | 更新时间 | DEFAULT CURRENT_TIMESTAMP ON UPDATE |
| deleted | INT | 删除标识：0-未删除，1-已删除 | DEFAULT 0 |

### 角色权限关联表（sys_role_permission）

| 字段名 | 类型 | 说明 | 约束 |
|--------|------|------|------|
| id | BIGINT | 主键ID | PRIMARY KEY, AUTO_INCREMENT |
| role_id | BIGINT | 角色ID | NOT NULL, INDEX |
| permission_id | BIGINT | 权限ID | NOT NULL, INDEX |
| create_time | DATETIME | 创建时间 | DEFAULT CURRENT_TIMESTAMP |
| update_time | DATETIME | 更新时间 | DEFAULT CURRENT_TIMESTAMP ON UPDATE |
| deleted | INT | 删除标识：0-未删除，1-已删除 | DEFAULT 0 |

## 配置说明

### application.yml

```yaml
server:
  port: 8082

spring:
  application:
    name: user-service
  cloud:
    nacos:
      discovery:
        server-addr: localhost:8848
        enabled: false  # 如果 Nacos 未启动，设置为 false
        fail-fast: false
  datasource:
    driver-class-name: com.mysql.cj.jdbc.Driver
    url: jdbc:mysql://localhost:3306/ddd_auth?useUnicode=true&characterEncoding=utf8&serverTimezone=Asia/Shanghai
    username: root
    password: password
    hikari:
      connection-timeout: 30000
      maximum-pool-size: 10
      minimum-idle: 5
  # Redis 配置（可选，用于缓存用户角色和权限）
  redis:
    enabled: true
    host: localhost
    port: 6379
    password: 
    database: 0

# MyBatis Plus 配置
mybatis-plus:
  configuration:
    map-underscore-to-camel-case: true
    log-impl: org.apache.ibatis.logging.stdout.StdOutImpl
  global-config:
    db-config:
      id-type: assign_id
      logic-delete-field: deleted
      logic-delete-value: 1
      logic-not-delete-value: 0
```

## 启动说明

### 1. 依赖服务

- **MySQL**：数据库服务（必需）
  - 确保数据库已创建
  - 执行建表 SQL 创建相关表（参考 `sql/init.sql`）
- **Redis**：缓存服务（可选）
  - 如果未启动，缓存功能会被禁用，但不影响基本功能
- **Nacos**：服务注册与发现（可选）
  - 如果未启动，设置 `spring.cloud.nacos.discovery.enabled: false`

### 2. 数据库准备

执行建表 SQL 创建相关表（参考 `sql/init.sql` 中的用户服务部分）。

### 3. 启动服务

```bash
# 使用 Maven
mvn spring-boot:run

# 或打包后运行
mvn clean package
java -jar target/user-service-1.0.0.jar
```

### 4. 验证服务

- 访问 `http://localhost:8082/actuator/health` 检查服务状态
- 访问 `http://localhost:8082/api/user/roles?userId=1` 测试查询接口

## 依赖组件

- **component-cache**: 缓存组件（Redis，可选）
- **common**: 公共模块（实体类、异常处理、统一响应）

## 使用示例

### 示例1：查询用户角色

```java
@RestController
@RequestMapping("/api/user")
@RequiredArgsConstructor
public class UserController {
    
    private final UserService userService;
    
    @GetMapping("/roles")
    public Result<List<Role>> getUserRoles(@RequestParam Long userId) {
        List<Role> roles = userService.getUserRoles(userId);
        return Result.success(roles);
    }
}
```

### 示例2：查询用户权限

```java
@Service
@RequiredArgsConstructor
public class UserService {
    
    private final UserRoleRepository userRoleRepository;
    private final RolePermissionRepository rolePermissionRepository;
    
    public List<Permission> getUserPermissions(Long userId) {
        // 1. 查询用户的所有角色
        List<Role> roles = getUserRoles(userId);
        
        // 2. 查询每个角色的所有权限
        Set<Permission> permissionSet = roles.stream()
                .flatMap(role -> {
                    List<Permission> permissions = 
                        rolePermissionRepository.findPermissionsByRoleId(role.getId());
                    return permissions != null ? permissions.stream() : Stream.empty();
                })
                .collect(Collectors.toSet());
        
        return permissionSet.stream().collect(Collectors.toList());
    }
}
```

### 示例3：权限验证

```java
@GetMapping("/has-permission")
public Result<Boolean> hasPermission(
        @RequestParam Long userId, 
        @RequestParam String permissionCode) {
    boolean hasPermission = userService.hasPermission(userId, permissionCode);
    return Result.success(hasPermission);
}
```

## 缓存策略

### 缓存 Key 设计

| Key | 说明 | 过期时间 |
|-----|------|----------|
| `user:roles:{userId}` | 用户角色列表 | 30分钟 |
| `user:permissions:{userId}` | 用户权限列表 | 30分钟 |

### 缓存更新

当用户角色或权限发生变化时，需要清除缓存：

```java
// 清除用户角色和权限缓存
userService.clearUserRoleCache(userId);
```

## 最佳实践

### 1. 权限验证

- **在网关层验证**：在 API Gateway 中统一验证用户权限
- **在服务层验证**：在业务服务中验证用户权限
- **使用注解验证**：使用 AOP 注解自动验证权限

### 2. 缓存使用

- **合理设置过期时间**：根据业务需求设置缓存过期时间
- **及时清除缓存**：当权限发生变化时，及时清除相关缓存
- **缓存穿透保护**：对于不存在的用户，也缓存空结果

### 3. 性能优化

- **批量查询**：批量查询用户角色和权限，减少数据库查询次数
- **使用缓存**：使用 Redis 缓存用户角色和权限，提高查询性能
- **索引优化**：在关联表上创建合适的索引

### 4. 安全考虑

- **权限最小化**：只授予用户必要的权限
- **定期审计**：定期检查用户权限，确保权限正确
- **权限变更日志**：记录权限变更日志，便于审计

## 常见问题

### 1. 查询用户角色返回空列表？

**答**：检查以下几点：
- 用户是否存在
- 用户是否已分配角色
- 角色是否已启用（status = 1）
- 关联关系是否已删除（deleted = 0）

### 2. 查询用户权限返回空列表？

**答**：检查以下几点：
- 用户是否有角色
- 角色是否有权限
- 权限是否已启用（status = 1）
- 关联关系是否已删除（deleted = 0）

### 3. 权限验证总是返回 false？

**答**：检查以下几点：
- 用户是否有角色
- 角色是否有该权限
- 权限编码是否正确
- 权限是否已启用

### 4. 缓存不生效？

**答**：检查以下几点：
- Redis 是否已启动
- Redis 配置是否正确
- 缓存 Key 是否正确
- 缓存是否已过期

### 5. 如何清除用户权限缓存？

**答**：调用 `clearUserRoleCache` 方法：

```java
userService.clearUserRoleCache(userId);
```

## 开发规范

1. **DDD 分层架构**：严格按照领域层、应用层、基础设施层、接口层分层
2. **领域模型**：领域模型继承 `BaseEntity`，包含基础字段
3. **仓储模式**：使用仓储接口和实现分离，保持领域层纯净
4. **缓存策略**：合理使用缓存，提高查询性能
5. **异常处理**：使用 `BusinessException` 处理业务异常
6. **代码注释**：为所有类和方法添加 JavaDoc 注释

## 后续优化

- [ ] 添加角色管理功能（创建、更新、删除角色）
- [ ] 添加权限管理功能（创建、更新、删除权限）
- [ ] 添加用户角色分配功能
- [ ] 添加角色权限分配功能
- [ ] 添加权限变更日志
- [ ] 添加权限审计功能
- [ ] 集成 Spring Security 进行权限控制

## 版本历史

- **v1.0.0** (2024-01-01)
  - 初始版本
  - 提供用户角色查询功能
  - 提供用户权限查询功能
  - 提供权限验证功能
  - 集成 Redis 缓存

## 许可证

本项目采用 MIT 许可证。

## 联系方式

如有问题或建议，请联系开发团队。

