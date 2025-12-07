package com.example.user.domain.repository;

import com.example.user.domain.model.User;

import java.util.List;

/**
 * 用户管理仓储接口
 * <p>
 * 定义用户管理的持久化操作，属于领域层
 * </p>
 * 
 * @author system
 * @version 1.0.0
 * @since 1.0.0
 */
public interface UserManagementRepository {
    
    /**
     * 查询所有用户
     * 
     * @return 用户列表
     */
    List<User> findAll();
    
    /**
     * 根据ID查询用户
     * 
     * @param id 用户ID，不能为 null
     * @return 用户领域模型，如果不存在返回 null
     */
    User findById(Long id);
    
    /**
     * 保存用户（新增或更新）
     * 
     * @param user 用户领域模型，不能为 null
     * @return 保存后的用户领域模型
     */
    User save(User user);
    
    /**
     * 删除用户（逻辑删除）
     * 
     * @param id 用户ID，不能为 null
     */
    void delete(Long id);
}

