package com.example.user.domain.repository;

import com.example.user.domain.model.Role;
import com.example.user.domain.model.UserRole;

import java.util.List;

/**
 * 用户角色关联仓储接口
 * <p>
 * 定义用户角色关联的持久化操作，属于领域层
 * </p>
 * 
 * @author system
 * @version 1.0.0
 * @since 1.0.0
 */
public interface UserRoleRepository {
    
    /**
     * 根据用户ID查询用户角色关联列表
     * 
     * @param userId 用户ID，不能为 null
     * @return 用户角色关联列表，如果不存在返回空列表
     */
    List<UserRole> findByUserId(Long userId);
    
    /**
     * 根据用户ID查询角色列表
     * 
     * @param userId 用户ID，不能为 null
     * @return 角色列表，如果不存在返回空列表
     */
    List<Role> findRolesByUserId(Long userId);
    
    /**
     * 保存用户角色关联
     * 
     * @param userRole 用户角色关联，不能为 null
     * @return 保存后的用户角色关联
     */
    UserRole save(UserRole userRole);
    
    /**
     * 删除用户角色关联
     * 
     * @param userId 用户ID，不能为 null
     * @param roleId 角色ID，不能为 null
     */
    void deleteByUserIdAndRoleId(Long userId, Long roleId);
}

