package com.example.user.domain.repository;

import com.example.user.domain.model.Permission;

import java.util.List;

/**
 * 权限仓储接口
 * <p>
 * 定义权限的持久化操作，属于领域层，不依赖具体的技术实现
 * </p>
 * 
 * @author system
 * @version 1.0.0
 * @since 1.0.0
 */
public interface PermissionRepository {
    
    /**
     * 根据ID查询权限
     * 
     * @param id 权限ID，不能为 null
     * @return 权限领域模型，如果不存在返回 null
     */
    Permission findById(Long id);
    
    /**
     * 根据权限编码查询权限
     * 
     * @param permissionCode 权限编码，不能为 null 或空字符串
     * @return 权限领域模型，如果不存在返回 null
     */
    Permission findByPermissionCode(String permissionCode);
    
    /**
     * 查询所有权限
     * 
     * @return 权限列表，如果不存在返回空列表
     */
    List<Permission> findAll();
}

