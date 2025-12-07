package com.example.user.interfaces.controller;

import com.example.common.exception.BusinessException;
import com.example.common.result.Result;
import com.example.user.application.service.UserManagementService;
import com.example.user.application.service.UserService;
import com.example.user.domain.model.Permission;
import com.example.user.domain.model.Role;
import com.example.user.domain.model.User;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;
import java.util.List;

/**
 * 用户控制器
 * <p>
 * 提供用户管理、用户角色和权限相关的 REST API 接口，属于接口层（interfaces）
 * </p>
 * <p>
 * 功能说明：
 * <ul>
 *   <li>用户管理：用户列表、创建、更新、删除</li>
 *   <li>查询用户角色：根据用户ID查询用户拥有的角色</li>
 *   <li>查询用户权限：根据用户ID查询用户拥有的权限</li>
 *   <li>权限验证：检查用户是否拥有指定权限</li>
 * </ul>
 * </p>
 * <p>
 * 请求路径：/api/user
 * </p>
 * <p>
 * 参数验证：
 * <ul>
 *   <li>使用 @Validated 验证请求参数</li>
 *   <li>使用 Bean Validation 注解进行参数校验</li>
 * </ul>
 * </p>
 * 
 * @author system
 * @version 1.0.0
 * @since 1.0.0
 */
@Slf4j
@RestController
@RequestMapping("/api/user")
@RequiredArgsConstructor
@Validated
public class UserController {

    /** 用户应用服务（角色和权限） */
    private final UserService userService;
    
    /** 用户管理应用服务 */
    private final UserManagementService userManagementService;

    /**
     * 获取用户列表
     * 
     * @param username 用户名（可选）
     * @param phone 手机号（可选）
     * @param status 状态（可选）
     * @param page 页码（可选，暂未实现分页）
     * @param size 每页大小（可选，暂未实现分页）
     * @param request HTTP请求（用于获取当前用户ID）
     * @return 用户列表
     */
    @GetMapping("/list")
    public Result<List<User>> getUserList(
            @RequestParam(required = false) String username,
            @RequestParam(required = false) String phone,
            @RequestParam(required = false) Integer status,
            @RequestParam(required = false, defaultValue = "1") Integer page,
            @RequestParam(required = false, defaultValue = "10") Integer size,
            HttpServletRequest request) {
        try {
            // 从请求头获取当前用户ID（由网关传递）
            Long currentUserId = null;
            String userIdHeader = request.getHeader("X-User-Id");
            if (userIdHeader != null && !userIdHeader.isEmpty()) {
                try {
                    currentUserId = Long.parseLong(userIdHeader);
                } catch (NumberFormatException e) {
                    log.warn("无法解析用户ID: {}", userIdHeader);
                }
            }
            
            // 如果请求头没有用户ID，尝试从Token中解析（从Authorization头）
            // 注意：如果网关已配置传递X-User-Id，这里不会执行
            if (currentUserId == null) {
                String authHeader = request.getHeader("Authorization");
                if (authHeader != null && authHeader.startsWith("Bearer ")) {
                    String token = authHeader.substring(7);
                    try {
                        // 简单解析JWT Token获取用户ID（不验证签名，仅用于获取用户ID）
                        // JWT格式：header.payload.signature
                        String[] parts = token.split("\\.");
                        if (parts.length == 3) {
                            // 解析payload（Base64解码）
                            String payload = new String(java.util.Base64.getUrlDecoder().decode(parts[1]));
                            // 解析JSON获取userId
                            com.alibaba.fastjson2.JSONObject jsonObject = com.alibaba.fastjson2.JSON.parseObject(payload);
                            Object userIdObj = jsonObject.get("userId");
                            if (userIdObj != null) {
                                currentUserId = Long.valueOf(userIdObj.toString());
                                log.debug("从Token解析用户ID成功: userId={}", currentUserId);
                            }
                        }
                    } catch (Exception e) {
                        log.debug("无法从Token解析用户ID: {}", e.getMessage());
                    }
                }
            }
            
            log.info("查询用户列表: username={}, phone={}, status={}, page={}, size={}, currentUserId={}", 
                    username, phone, status, page, size, currentUserId);
            
            List<User> users = userManagementService.getUserList(username, phone, status, page, size, currentUserId);
            
            log.info("查询用户列表成功: count={}, currentUserId={}", users.size(), currentUserId);
            return Result.success(users);
        } catch (BusinessException e) {
            log.warn("查询用户列表失败: message={}", e.getMessage());
            return Result.error(e.getMessage());
        } catch (Exception e) {
            log.error("查询用户列表异常", e);
            return Result.error("查询用户列表失败: " + e.getMessage());
        }
    }

    /**
     * 获取用户详情
     * 
     * @param id 用户ID
     * @return 用户详情
     */
    @GetMapping("/{id}")
    public Result<User> getUserById(
            @PathVariable @NotNull(message = "用户ID不能为空") @Positive(message = "用户ID必须大于0") Long id) {
        try {
            log.info("查询用户详情: userId={}", id);
            
            User user = userManagementService.getUserById(id);
            
            log.info("查询用户详情成功: userId={}", id);
            return Result.success(user);
        } catch (BusinessException e) {
            log.warn("查询用户详情失败: userId={}, message={}", id, e.getMessage());
            return Result.error(e.getMessage());
        } catch (Exception e) {
            log.error("查询用户详情异常: userId={}", id, e);
            return Result.error("查询用户详情失败: " + e.getMessage());
        }
    }

    /**
     * 创建用户
     * 
     * @param user 用户信息
     * @return 创建的用户
     */
    @PostMapping
    public Result<User> createUser(@RequestBody User user) {
        try {
            log.info("创建用户: username={}", user.getUsername());
            
            User createdUser = userManagementService.createUser(user);
            
            log.info("创建用户成功: userId={}", createdUser.getId());
            return Result.success(createdUser);
        } catch (BusinessException e) {
            log.warn("创建用户失败: message={}", e.getMessage());
            return Result.error(e.getMessage());
        } catch (Exception e) {
            log.error("创建用户异常", e);
            return Result.error("创建用户失败: " + e.getMessage());
        }
    }

    /**
     * 更新用户
     * 
     * @param id 用户ID
     * @param user 用户信息
     * @return 更新后的用户
     */
    @PutMapping("/{id}")
    public Result<User> updateUser(
            @PathVariable @NotNull(message = "用户ID不能为空") @Positive(message = "用户ID必须大于0") Long id,
            @RequestBody User user) {
        try {
            log.info("更新用户: userId={}", id);
            
            User updatedUser = userManagementService.updateUser(id, user);
            
            log.info("更新用户成功: userId={}", id);
            return Result.success(updatedUser);
        } catch (BusinessException e) {
            log.warn("更新用户失败: userId={}, message={}", id, e.getMessage());
            return Result.error(e.getMessage());
        } catch (Exception e) {
            log.error("更新用户异常: userId={}", id, e);
            return Result.error("更新用户失败: " + e.getMessage());
        }
    }

    /**
     * 删除用户
     * 
     * @param id 用户ID
     * @return 操作结果
     */
    @DeleteMapping("/{id}")
    public Result<Void> deleteUser(
            @PathVariable @NotNull(message = "用户ID不能为空") @Positive(message = "用户ID必须大于0") Long id) {
        try {
            log.info("删除用户: userId={}", id);
            
            userManagementService.deleteUser(id);
            
            log.info("删除用户成功: userId={}", id);
            return Result.success(null);
        } catch (BusinessException e) {
            log.warn("删除用户失败: userId={}, message={}", id, e.getMessage());
            return Result.error(e.getMessage());
        } catch (Exception e) {
            log.error("删除用户异常: userId={}", id, e);
            return Result.error("删除用户失败: " + e.getMessage());
        }
    }

    /**
     * 获取用户角色
     * <p>
     * 根据用户ID查询用户拥有的所有角色
     * </p>
     * 
     * @param userId 用户ID，必须大于 0
     * @return 角色列表
     */
    @GetMapping("/roles")
    public Result<List<Role>> getUserRoles(
            @RequestParam @NotNull(message = "用户ID不能为空") @Positive(message = "用户ID必须大于0") Long userId) {
        try {
            log.info("查询用户角色: userId={}", userId);
            
            List<Role> roles = userService.getUserRoles(userId);
            
            log.info("查询用户角色成功: userId={}, count={}", userId, roles.size());
            return Result.success(roles);
        } catch (BusinessException e) {
            log.warn("查询用户角色失败: userId={}, message={}", userId, e.getMessage());
            return Result.error(e.getMessage());
        } catch (Exception e) {
            log.error("查询用户角色异常: userId={}", userId, e);
            return Result.error("查询用户角色失败: " + e.getMessage());
        }
    }

    /**
     * 获取用户权限
     * <p>
     * 根据用户ID查询用户拥有的所有权限（通过角色间接获得）
     * </p>
     * 
     * @param userId 用户ID，必须大于 0
     * @return 权限列表
     */
    @GetMapping("/permissions")
    public Result<List<Permission>> getUserPermissions(
            @RequestParam @NotNull(message = "用户ID不能为空") @Positive(message = "用户ID必须大于0") Long userId) {
        try {
            log.info("查询用户权限: userId={}", userId);
            
            List<Permission> permissions = userService.getUserPermissions(userId);
            
            log.info("查询用户权限成功: userId={}, count={}", userId, permissions.size());
            return Result.success(permissions);
        } catch (BusinessException e) {
            log.warn("查询用户权限失败: userId={}, message={}", userId, e.getMessage());
            return Result.error(e.getMessage());
        } catch (Exception e) {
            log.error("查询用户权限异常: userId={}", userId, e);
            return Result.error("查询用户权限失败: " + e.getMessage());
        }
    }

    /**
     * 检查用户是否有权限
     * <p>
     * 根据用户ID和权限编码检查用户是否拥有指定权限
     * </p>
     * 
     * @param userId 用户ID，必须大于 0
     * @param permissionCode 权限编码，不能为空
     * @return true 表示用户拥有该权限，false 表示用户没有该权限
     */
    @GetMapping("/has-permission")
    public Result<Boolean> hasPermission(
            @RequestParam @NotNull(message = "用户ID不能为空") @Positive(message = "用户ID必须大于0") Long userId,
            @RequestParam @NotBlank(message = "权限编码不能为空") String permissionCode) {
        try {
            log.info("权限检查: userId={}, permissionCode={}", userId, permissionCode);
            
            boolean hasPermission = userService.hasPermission(userId, permissionCode);
            
            log.info("权限检查完成: userId={}, permissionCode={}, hasPermission={}", 
                    userId, permissionCode, hasPermission);
            return Result.success(hasPermission);
        } catch (Exception e) {
            log.error("权限检查异常: userId={}, permissionCode={}", userId, permissionCode, e);
            return Result.error("权限检查失败: " + e.getMessage());
        }
    }

    /**
     * 分配用户角色
     * <p>
     * 为用户分配角色，会先删除用户现有的所有角色，然后添加新的角色
     * </p>
     * 
     * @param userId 用户ID
     * @param request 角色ID列表
     * @return 操作结果
     */
    @PostMapping("/{userId}/roles")
    public Result<Void> assignUserRoles(
            @PathVariable @NotNull(message = "用户ID不能为空") @Positive(message = "用户ID必须大于0") Long userId,
            @RequestBody AssignRoleRequest request) {
        try {
            log.info("分配用户角色: userId={}, roleIds={}", userId, request.getRoleIds());
            
            userService.assignUserRoles(userId, request.getRoleIds());
            
            log.info("分配用户角色成功: userId={}", userId);
            return Result.success(null);
        } catch (BusinessException e) {
            log.warn("分配用户角色失败: userId={}, message={}", userId, e.getMessage());
            return Result.error(e.getMessage());
        } catch (Exception e) {
            log.error("分配用户角色异常: userId={}", userId, e);
            return Result.error("分配用户角色失败: " + e.getMessage());
        }
    }

    /**
     * 修改用户密码
     * <p>
     * 管理员可以直接修改用户密码，不需要验证旧密码
     * </p>
     * 
     * @param userId 用户ID，必须大于 0
     * @param request 修改密码请求（包含新密码）
     * @return 操作结果
     */
    @PostMapping("/{userId}/change-password")
    public Result<Void> changePassword(
            @PathVariable @NotNull(message = "用户ID不能为空") @Positive(message = "用户ID必须大于0") Long userId,
            @RequestBody ChangePasswordRequest request) {
        try {
            log.info("修改用户密码: userId={}", userId);
            
            userManagementService.changePassword(userId, request.getNewPassword());
            
            log.info("修改用户密码成功: userId={}", userId);
            return Result.success(null);
        } catch (BusinessException e) {
            log.warn("修改用户密码失败: userId={}, message={}", userId, e.getMessage());
            return Result.error(e.getMessage());
        } catch (Exception e) {
            log.error("修改用户密码异常: userId={}", userId, e);
            return Result.error("修改用户密码失败: " + e.getMessage());
        }
    }

    /**
     * 分配角色请求对象
     */
    public static class AssignRoleRequest {
        private List<Long> roleIds;

        public List<Long> getRoleIds() {
            return roleIds;
        }

        public void setRoleIds(List<Long> roleIds) {
            this.roleIds = roleIds;
        }
    }

    /**
     * 修改密码请求对象
     */
    public static class ChangePasswordRequest {
        private String newPassword;

        public String getNewPassword() {
            return newPassword;
        }

        public void setNewPassword(String newPassword) {
            this.newPassword = newPassword;
        }
    }
}

