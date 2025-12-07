package com.example.user.domain.repository;

import com.example.user.domain.model.Permission;
import com.example.user.domain.model.RolePermission;

import java.util.List;

/**
 * 角色权限关联仓储接口
 * <p>
 * 定义角色权限关联的持久化操作，属于领域层
 * </p>
 * 
 * @author system
 * @version 1.0.0
 * @since 1.0.0
 */
public interface RolePermissionRepository {
    
    /**
     * 根据角色ID查询角色权限关联列表
     * 
     * @param roleId 角色ID，不能为 null
     * @return 角色权限关联列表，如果不存在返回空列表
     */
    List<RolePermission> findByRoleId(Long roleId);
    
    /**
     * 根据角色ID查询权限列表
     * 
     * @param roleId 角色ID，不能为 null
     * @return 权限列表，如果不存在返回空列表
     */
    List<Permission> findPermissionsByRoleId(Long roleId);
    
    /**
     * 保存角色权限关联
     * 
     * @param rolePermission 角色权限关联，不能为 null
     * @return 保存后的角色权限关联
     */
    RolePermission save(RolePermission rolePermission);
    
    /**
     * 删除角色权限关联
     * 
     * @param roleId 角色ID，不能为 null
     * @param permissionId 权限ID，不能为 null
     */
    void deleteByRoleIdAndPermissionId(Long roleId, Long permissionId);
}

