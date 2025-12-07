package com.example.user.application.service;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.TypeReference;
import com.example.cache.CacheService;
import com.example.common.exception.BusinessException;
import com.example.user.domain.model.Permission;
import com.example.user.domain.model.Role;
import com.example.user.domain.repository.PermissionRepository;
import com.example.user.domain.repository.RolePermissionRepository;
import com.example.user.domain.repository.UserRoleRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * 用户应用服务
 * <p>
 * 负责协调领域对象完成用户角色和权限管理的业务用例，是应用层的核心
 * </p>
 * <p>
 * 职责说明：
 * <ul>
 *   <li>查询用户角色：根据用户ID查询用户拥有的角色</li>
 *   <li>查询用户权限：根据用户ID查询用户拥有的权限（通过角色间接获得）</li>
 *   <li>权限验证：检查用户是否拥有指定权限</li>
 *   <li>缓存优化：使用缓存提高查询性能</li>
 * </ul>
 * </p>
 * <p>
 * 权限继承说明：
 * <ul>
 *   <li>用户通过角色间接拥有权限</li>
 *   <li>用户权限 = 用户所有角色的权限集合（去重）</li>
 *   <li>权限检查时，需要遍历用户的所有角色，查找是否包含指定权限</li>
 * </ul>
 * </p>
 * 
 * @author system
 * @version 1.0.0
 * @since 1.0.0
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class UserService {

    /** 用户角色关联仓储 */
    private final UserRoleRepository userRoleRepository;
    
    /** 角色权限关联仓储 */
    private final RolePermissionRepository rolePermissionRepository;
    
    /** 权限仓储 */
    private final PermissionRepository permissionRepository;
    
    /** 缓存服务（可选，用于缓存用户角色和权限） */
    @Autowired(required = false)
    private CacheService cacheService;
    
    /** Redis 模板（可选，用于直接访问 Redis） */
    @Autowired(required = false)
    private RedisTemplate<String, Object> redisTemplate;

    /**
     * 获取用户角色
     * <p>
     * 根据用户ID查询用户拥有的所有角色
     * </p>
     * <p>
     * 缓存策略：
     * <ul>
     *   <li>如果缓存服务可用，优先从缓存获取</li>
     *   <li>缓存不存在时，从数据库查询并缓存结果</li>
     *   <li>缓存过期时间：30分钟</li>
     * </ul>
     * </p>
     * 
     * @param userId 用户ID，不能为 null
     * @return 角色列表，如果用户没有角色返回空列表
     */
    public List<Role> getUserRoles(Long userId) {
        Assert.notNull(userId, "用户ID不能为空");
        
        try {
            // 尝试从缓存获取
            if (cacheService != null && redisTemplate != null) {
                String cacheKey = "user:roles:" + userId;
                Object cachedValue = redisTemplate.opsForValue().get(cacheKey);
                if (cachedValue != null) {
                    try {
                        // 使用 FastJSON2 的 TypeReference 正确转换泛型类型
                        List<Role> cachedRoles = JSON.parseObject(
                            JSON.toJSONString(cachedValue),
                            new TypeReference<List<Role>>() {}
                        );
                        if (cachedRoles != null && !cachedRoles.isEmpty()) {
                            log.debug("从缓存获取用户角色: userId={}, count={}", userId, cachedRoles.size());
                            return cachedRoles;
                        }
                    } catch (Exception e) {
                        log.warn("从缓存解析用户角色失败，清除缓存: userId={}", userId, e);
                        // 清除损坏的缓存
                        cacheService.delete(cacheKey);
                    }
                }
            }
            
            // 从数据库查询
            List<Role> roles = userRoleRepository.findRolesByUserId(userId);
            
            // 缓存结果
            if (cacheService != null && roles != null && !roles.isEmpty()) {
                String cacheKey = "user:roles:" + userId;
                cacheService.set(cacheKey, roles, 30, TimeUnit.MINUTES);
                log.debug("缓存用户角色: userId={}, count={}", userId, roles.size());
            }
            
            log.info("获取用户角色成功: userId={}, count={}", userId, roles != null ? roles.size() : 0);
            return roles != null ? roles : java.util.Collections.emptyList();
        } catch (Exception e) {
            log.error("获取用户角色失败: userId={}", userId, e);
            throw new BusinessException("获取用户角色失败: " + e.getMessage());
        }
    }

    /**
     * 获取用户权限
     * <p>
     * 根据用户ID查询用户拥有的所有权限（通过角色间接获得）
     * </p>
     * <p>
     * 查询逻辑：
     * <ol>
     *   <li>查询用户的所有角色</li>
     *   <li>查询每个角色的所有权限</li>
     *   <li>合并所有权限并去重</li>
     * </ol>
     * </p>
     * <p>
     * 缓存策略：
     * <ul>
     *   <li>如果缓存服务可用，优先从缓存获取</li>
     *   <li>缓存不存在时，从数据库查询并缓存结果</li>
     *   <li>缓存过期时间：30分钟</li>
     * </ul>
     * </p>
     * 
     * @param userId 用户ID，不能为 null
     * @return 权限列表，如果用户没有权限返回空列表
     */
    public List<Permission> getUserPermissions(Long userId) {
        Assert.notNull(userId, "用户ID不能为空");
        
        try {
            // 尝试从缓存获取
            if (cacheService != null && redisTemplate != null) {
                String cacheKey = "user:permissions:" + userId;
                Object cachedValue = redisTemplate.opsForValue().get(cacheKey);
                if (cachedValue != null) {
                    try {
                        // 使用 FastJSON2 的 TypeReference 正确转换泛型类型
                        List<Permission> cachedPermissions = JSON.parseObject(
                            JSON.toJSONString(cachedValue),
                            new TypeReference<List<Permission>>() {}
                        );
                        if (cachedPermissions != null && !cachedPermissions.isEmpty()) {
                            log.debug("从缓存获取用户权限: userId={}, count={}", userId, cachedPermissions.size());
                            return cachedPermissions;
                        }
                    } catch (Exception e) {
                        log.warn("从缓存解析用户权限失败，清除缓存: userId={}", userId, e);
                        // 清除损坏的缓存
                        cacheService.delete(cacheKey);
                    }
                }
            }
            
            // 1. 查询用户的所有角色
            List<Role> roles = getUserRoles(userId);
            
            // 2. 查询每个角色的所有权限
            Set<Permission> permissionSet = roles.stream()
                    .flatMap(role -> {
                        List<Permission> permissions = rolePermissionRepository.findPermissionsByRoleId(role.getId());
                        return permissions != null ? permissions.stream() : java.util.stream.Stream.empty();
                    })
                    .collect(Collectors.toSet()); // 使用 Set 去重
            
            List<Permission> permissions = permissionSet.stream()
                    .collect(Collectors.toList());
            
            // 缓存结果
            if (cacheService != null && !permissions.isEmpty()) {
                String cacheKey = "user:permissions:" + userId;
                cacheService.set(cacheKey, permissions, 30, TimeUnit.MINUTES);
                log.debug("缓存用户权限: userId={}, count={}", userId, permissions.size());
            }
            
            log.info("获取用户权限成功: userId={}, count={}", userId, permissions.size());
            return permissions;
        } catch (Exception e) {
            log.error("获取用户权限失败: userId={}", userId, e);
            throw new BusinessException("获取用户权限失败: " + e.getMessage());
        }
    }

    /**
     * 检查用户是否有权限
     * <p>
     * 根据用户ID和权限编码检查用户是否拥有指定权限
     * </p>
     * <p>
     * 检查逻辑：
     * <ol>
     *   <li>获取用户的所有权限</li>
     *   <li>检查权限列表中是否包含指定权限编码</li>
     * </ol>
     * </p>
     * 
     * @param userId 用户ID，不能为 null
     * @param permissionCode 权限编码，不能为 null 或空字符串
     * @return true 表示用户拥有该权限，false 表示用户没有该权限
     */
    public boolean hasPermission(Long userId, String permissionCode) {
        Assert.notNull(userId, "用户ID不能为空");
        Assert.hasText(permissionCode, "权限编码不能为空");
        
        try {
            // 获取用户的所有权限
            List<Permission> permissions = getUserPermissions(userId);
            
            // 检查是否包含指定权限编码
            boolean hasPermission = permissions.stream()
                    .anyMatch(p -> permissionCode.equals(p.getPermissionCode()));
            
            log.debug("权限检查: userId={}, permissionCode={}, hasPermission={}", 
                    userId, permissionCode, hasPermission);
            return hasPermission;
        } catch (Exception e) {
            log.error("权限检查失败: userId={}, permissionCode={}", userId, permissionCode, e);
            // 权限检查失败时，为了安全起见，返回 false
            return false;
        }
    }
    
    /**
     * 分配用户角色
     * <p>
     * 为用户分配角色，会先删除用户现有的所有角色，然后添加新的角色
     * </p>
     * 
     * @param userId 用户ID，不能为 null
     * @param roleIds 角色ID列表，不能为 null
     */
    public void assignUserRoles(Long userId, List<Long> roleIds) {
        Assert.notNull(userId, "用户ID不能为空");
        Assert.notNull(roleIds, "角色ID列表不能为空");
        
        try {
            // 获取用户当前的角色ID列表
            List<com.example.user.domain.model.Role> existingRoles = 
                userRoleRepository.findRolesByUserId(userId);
            List<Long> existingRoleIds = existingRoles.stream()
                .map(com.example.user.domain.model.Role::getId)
                .collect(Collectors.toList());
            
            // 计算需要删除的角色（在当前角色中但不在新角色列表中）
            List<Long> toDelete = existingRoleIds.stream()
                .filter(roleId -> !roleIds.contains(roleId))
                .collect(Collectors.toList());
            
            // 计算需要添加的角色（在新角色列表中但不在当前角色中）
            List<Long> toAdd = roleIds.stream()
                .filter(roleId -> !existingRoleIds.contains(roleId))
                .collect(Collectors.toList());
            
            // 删除不需要的角色
            for (Long roleId : toDelete) {
                userRoleRepository.deleteByUserIdAndRoleId(userId, roleId);
            }
            
            // 添加新角色（save 方法会自动处理已删除记录的恢复）
            for (Long roleId : toAdd) {
                com.example.user.domain.model.UserRole userRole = 
                    new com.example.user.domain.model.UserRole();
                userRole.setUserId(userId);
                userRole.setRoleId(roleId);
                userRoleRepository.save(userRole);
            }
            
            // 清除缓存
            clearUserRoleCache(userId);
            
            log.info("分配用户角色成功: userId={}, roleIds={}, deleted={}, added={}", 
                    userId, roleIds, toDelete.size(), toAdd.size());
        } catch (Exception e) {
            log.error("分配用户角色失败: userId={}", userId, e);
            throw new BusinessException("分配用户角色失败: " + e.getMessage());
        }
    }
    
    /**
     * 清除用户角色缓存
     * <p>
     * 当用户角色发生变化时，清除缓存以便下次查询时重新加载
     * </p>
     * 
     * @param userId 用户ID，不能为 null
     */
    public void clearUserRoleCache(Long userId) {
        Assert.notNull(userId, "用户ID不能为空");
        
        if (cacheService != null) {
            try {
                String roleCacheKey = "user:roles:" + userId;
                String permissionCacheKey = "user:permissions:" + userId;
                cacheService.delete(roleCacheKey);
                cacheService.delete(permissionCacheKey);
                log.debug("清除用户角色和权限缓存: userId={}", userId);
            } catch (Exception e) {
                log.warn("清除用户角色缓存失败: userId={}", userId, e);
            }
        }
    }
}

