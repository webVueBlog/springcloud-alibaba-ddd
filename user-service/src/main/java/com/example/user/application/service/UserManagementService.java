package com.example.user.application.service;

import com.example.common.exception.BusinessException;
import com.example.user.domain.model.Permission;
import com.example.user.domain.model.User;
import com.example.user.domain.repository.UserManagementRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;

import java.security.MessageDigest;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * 用户管理应用服务
 * <p>
 * 负责协调领域对象完成用户管理的业务用例
 * </p>
 * 
 * @author system
 * @version 1.0.0
 * @since 1.0.0
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class UserManagementService {

    /** 用户管理仓储 */
    private final UserManagementRepository userManagementRepository;
    
    /** 用户服务（用于获取用户权限） */
    private final UserService userService;

    /**
     * 获取用户列表
     * 
     * @param username 用户名（可选）
     * @param phone 手机号（可选）
     * @param status 状态（可选）
     * @param page 页码（可选，暂未实现分页）
     * @param size 每页大小（可选，暂未实现分页）
     * @param currentUserId 当前用户ID（用于数据权限过滤）
     * @return 用户列表
     */
    public List<User> getUserList(String username, String phone, Integer status, Integer page, Integer size, Long currentUserId) {
        try {
            List<User> allUsers = userManagementRepository.findAll();
            
            // 应用查询条件过滤
            List<User> filteredUsers = allUsers.stream()
                    .filter(user -> {
                        if (StringUtils.hasText(username)) {
                            return user.getUsername() != null && 
                                   user.getUsername().toLowerCase().contains(username.toLowerCase());
                        }
                        return true;
                    })
                    .filter(user -> {
                        if (StringUtils.hasText(phone)) {
                            return user.getPhone() != null && 
                                   user.getPhone().contains(phone);
                        }
                        return true;
                    })
                    .filter(user -> {
                        if (status != null) {
                            return user.getStatus() != null && user.getStatus().equals(status);
                        }
                        return true;
                    })
                    .collect(Collectors.toList());
            
            // 应用数据权限过滤（只有在有用户ID且有数据权限配置时才过滤）
            if (currentUserId != null) {
                filteredUsers = applyDataPermissionFilter(filteredUsers, currentUserId);
            }
            // 如果没有用户ID，直接返回所有数据（可能是网关未配置或直接访问）
            
            return filteredUsers;
        } catch (Exception e) {
            log.error("获取用户列表失败", e);
            throw new BusinessException("获取用户列表失败: " + e.getMessage());
        }
    }
    
    /**
     * 应用数据权限过滤
     * 
     * @param users 用户列表
     * @param currentUserId 当前用户ID
     * @return 过滤后的用户列表
     */
    private List<User> applyDataPermissionFilter(List<User> users, Long currentUserId) {
        try {
            // 获取当前用户的数据权限
            List<Permission> allPermissions = userService.getUserPermissions(currentUserId);
            List<Permission> dataPermissions = allPermissions.stream()
                    .filter(p -> "DATA".equals(p.getPermissionType()))
                    .filter(p -> p.getPermissionCode() != null && p.getPermissionCode().startsWith("USER_DATA_"))
                    .collect(Collectors.toList());
            
            // 如果没有数据权限配置，返回所有数据（兼容旧系统或未配置数据权限的情况）
            if (dataPermissions.isEmpty()) {
                log.debug("用户没有数据权限配置，返回所有数据: userId={}", currentUserId);
                return users;
            }
            
            // 检查是否有全部数据权限
            boolean hasAllPermission = dataPermissions.stream()
                    .anyMatch(p -> "USER_DATA_ALL".equals(p.getPermissionCode()));
            
            if (hasAllPermission) {
                // 有全部权限，返回所有数据
                log.debug("用户拥有全部数据权限: userId={}", currentUserId);
                return users;
            }
            
            // 检查是否有部门数据权限
            boolean hasDeptPermission = dataPermissions.stream()
                    .anyMatch(p -> "USER_DATA_DEPT".equals(p.getPermissionCode()));
            
            if (hasDeptPermission) {
                // 有部门权限，这里简化处理，返回所有数据（实际应该根据部门过滤）
                // TODO: 实现部门数据权限过滤逻辑
                log.debug("用户拥有部门数据权限: userId={}", currentUserId);
                return users;
            }
            
            // 只有自己数据权限，只返回自己的数据
            boolean hasSelfPermission = dataPermissions.stream()
                    .anyMatch(p -> "USER_DATA_SELF".equals(p.getPermissionCode()));
            
            if (hasSelfPermission) {
                log.debug("用户只有自己数据权限: userId={}", currentUserId);
                return users.stream()
                        .filter(user -> currentUserId.equals(user.getId()))
                        .collect(Collectors.toList());
            }
            
            // 默认情况：如果没有匹配的数据权限，返回所有数据（兼容性处理）
            log.debug("用户数据权限未匹配任何规则，返回所有数据: userId={}", currentUserId);
            return users;
        } catch (Exception e) {
            log.error("应用数据权限过滤失败: userId={}", currentUserId, e);
            // 出错时返回所有数据，避免影响正常使用
            log.warn("数据权限过滤异常，返回所有数据以保证系统可用性: userId={}", currentUserId);
            return users;
        }
    }

    /**
     * 根据ID获取用户
     * 
     * @param id 用户ID
     * @return 用户
     */
    public User getUserById(Long id) {
        Assert.notNull(id, "用户ID不能为空");
        
        try {
            User user = userManagementRepository.findById(id);
            if (user == null) {
                throw new BusinessException("用户不存在");
            }
            return user;
        } catch (BusinessException e) {
            throw e;
        } catch (Exception e) {
            log.error("获取用户失败: userId={}", id, e);
            throw new BusinessException("获取用户失败: " + e.getMessage());
        }
    }

    /**
     * 创建用户
     * 
     * @param user 用户信息
     * @return 创建的用户
     */
    public User createUser(User user) {
        Assert.notNull(user, "用户不能为空");
        Assert.hasText(user.getUsername(), "用户名不能为空");
        
        try {
            // 检查用户名是否已存在
            List<User> existingUsers = userManagementRepository.findAll();
            boolean usernameExists = existingUsers.stream()
                    .anyMatch(u -> u.getUsername() != null && u.getUsername().equals(user.getUsername()));
            if (usernameExists) {
                throw new BusinessException("用户名已存在: " + user.getUsername());
            }
            
            // 检查手机号是否已存在
            if (StringUtils.hasText(user.getPhone())) {
                boolean phoneExists = existingUsers.stream()
                        .anyMatch(u -> u.getPhone() != null && u.getPhone().equals(user.getPhone()));
                if (phoneExists) {
                    throw new BusinessException("手机号已存在: " + user.getPhone());
                }
            }
            
            // 检查邮箱是否已存在
            if (StringUtils.hasText(user.getEmail())) {
                boolean emailExists = existingUsers.stream()
                        .anyMatch(u -> u.getEmail() != null && u.getEmail().equals(user.getEmail()));
                if (emailExists) {
                    throw new BusinessException("邮箱已存在: " + user.getEmail());
                }
            }
            
            // 设置默认值
            if (user.getStatus() == null) {
                user.setStatus(1);
            }
            
            // 如果提供了密码，进行加密
            if (StringUtils.hasText(user.getPassword())) {
                String salt = UUID.randomUUID().toString().replace("-", "").substring(0, 8);
                user.setSalt(salt);
                user.setPassword(md5(user.getPassword() + salt));
            }
            
            return userManagementRepository.save(user);
        } catch (BusinessException e) {
            throw e;
        } catch (Exception e) {
            log.error("创建用户失败: username={}", user.getUsername(), e);
            throw new BusinessException("创建用户失败: " + e.getMessage());
        }
    }

    /**
     * 更新用户
     * 
     * @param id 用户ID
     * @param user 用户信息
     * @return 更新后的用户
     */
    public User updateUser(Long id, User user) {
        Assert.notNull(id, "用户ID不能为空");
        Assert.notNull(user, "用户不能为空");
        
        try {
            User existingUser = userManagementRepository.findById(id);
            if (existingUser == null) {
                throw new BusinessException("用户不存在");
            }
            
            // 检查用户名是否重复
            if (StringUtils.hasText(user.getUsername()) && 
                !user.getUsername().equals(existingUser.getUsername())) {
                List<User> allUsers = userManagementRepository.findAll();
                boolean usernameExists = allUsers.stream()
                        .anyMatch(u -> !u.getId().equals(id) && 
                                     u.getUsername() != null && 
                                     u.getUsername().equals(user.getUsername()));
                if (usernameExists) {
                    throw new BusinessException("用户名已存在: " + user.getUsername());
                }
            }
            
            // 检查手机号是否重复
            if (StringUtils.hasText(user.getPhone()) && 
                !user.getPhone().equals(existingUser.getPhone())) {
                List<User> allUsers = userManagementRepository.findAll();
                boolean phoneExists = allUsers.stream()
                        .anyMatch(u -> !u.getId().equals(id) && 
                                     u.getPhone() != null && 
                                     u.getPhone().equals(user.getPhone()));
                if (phoneExists) {
                    throw new BusinessException("手机号已存在: " + user.getPhone());
                }
            }
            
            // 检查邮箱是否重复
            if (StringUtils.hasText(user.getEmail()) && 
                !user.getEmail().equals(existingUser.getEmail())) {
                List<User> allUsers = userManagementRepository.findAll();
                boolean emailExists = allUsers.stream()
                        .anyMatch(u -> !u.getId().equals(id) && 
                                     u.getEmail() != null && 
                                     u.getEmail().equals(user.getEmail()));
                if (emailExists) {
                    throw new BusinessException("邮箱已存在: " + user.getEmail());
                }
            }
            
            // 更新字段
            if (StringUtils.hasText(user.getUsername())) {
                existingUser.setUsername(user.getUsername());
            }
            if (StringUtils.hasText(user.getPhone())) {
                existingUser.setPhone(user.getPhone());
            }
            if (StringUtils.hasText(user.getEmail())) {
                existingUser.setEmail(user.getEmail());
            }
            if (user.getStatus() != null) {
                existingUser.setStatus(user.getStatus());
            }
            
            // 如果提供了新密码，进行加密
            if (StringUtils.hasText(user.getPassword())) {
                String salt = existingUser.getSalt();
                if (salt == null) {
                    salt = UUID.randomUUID().toString().replace("-", "").substring(0, 8);
                    existingUser.setSalt(salt);
                }
                existingUser.setPassword(md5(user.getPassword() + salt));
            }
            
            return userManagementRepository.save(existingUser);
        } catch (BusinessException e) {
            throw e;
        } catch (Exception e) {
            log.error("更新用户失败: userId={}", id, e);
            throw new BusinessException("更新用户失败: " + e.getMessage());
        }
    }

    /**
     * 删除用户
     * 
     * @param id 用户ID
     */
    public void deleteUser(Long id) {
        Assert.notNull(id, "用户ID不能为空");
        
        try {
            // 直接执行删除操作，不依赖findById（避免逻辑删除拦截器影响）
            userManagementRepository.delete(id);
            log.info("删除用户操作完成: userId={}", id);
        } catch (BusinessException e) {
            throw e;
        } catch (Exception e) {
            log.error("删除用户失败: userId={}", id, e);
            throw new BusinessException("删除用户失败: " + e.getMessage());
        }
    }

    /**
     * 修改用户密码
     * <p>
     * 管理员可以直接修改用户密码，不需要验证旧密码
     * </p>
     * 
     * @param userId 用户ID，不能为 null
     * @param newPassword 新密码，不能为空
     */
    public void changePassword(Long userId, String newPassword) {
        Assert.notNull(userId, "用户ID不能为空");
        Assert.hasText(newPassword, "新密码不能为空");
        
        try {
            // 获取用户信息
            User user = userManagementRepository.findById(userId);
            if (user == null) {
                throw new BusinessException("用户不存在");
            }
            
            // 获取或生成盐值
            String salt = user.getSalt();
            if (salt == null || salt.isEmpty()) {
                salt = UUID.randomUUID().toString().replace("-", "").substring(0, 8);
                user.setSalt(salt);
            }
            
            // 加密新密码
            String encryptedNewPassword = md5(newPassword + salt);
            
            // 验证新密码不能与当前密码相同
            if (encryptedNewPassword.equals(user.getPassword())) {
                throw new BusinessException("新密码不能与当前密码相同");
            }
            
            // 更新密码
            user.setPassword(encryptedNewPassword);
            userManagementRepository.save(user);
            
            log.info("修改密码成功: userId={}", userId);
        } catch (BusinessException e) {
            throw e;
        } catch (Exception e) {
            log.error("修改密码失败: userId={}", userId, e);
            throw new BusinessException("修改密码失败: " + e.getMessage());
        }
    }

    /**
     * MD5加密
     */
    private String md5(String input) {
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            byte[] messageDigest = md.digest(input.getBytes());
            StringBuilder hexString = new StringBuilder();
            for (byte b : messageDigest) {
                String hex = Integer.toHexString(0xff & b);
                if (hex.length() == 1) {
                    hexString.append('0');
                }
                hexString.append(hex);
            }
            return hexString.toString();
        } catch (Exception e) {
            throw new RuntimeException("MD5加密失败", e);
        }
    }
}

