package com.example.auth.domain.repository;

import com.example.auth.domain.model.User;

/**
 * 用户仓储接口（领域层）
 * <p>
 * 定义用户数据的访问接口，遵循 DDD 仓储模式
 * 实现类位于基础设施层，负责与数据库交互
 * </p>
 * 
 * @author system
 * @version 1.0.0
 * @since 1.0.0
 */
public interface UserRepository {
    
    /**
     * 根据用户名查找用户
     * 
     * @param username 用户名
     * @return 用户领域模型，如果不存在返回 null
     */
    User findByUsername(String username);
    
    /**
     * 根据手机号查找用户
     * 
     * @param phone 手机号
     * @return 用户领域模型，如果不存在返回 null
     */
    User findByPhone(String phone);
    
    /**
     * 根据邮箱查找用户
     * 
     * @param email 邮箱地址
     * @return 用户领域模型，如果不存在返回 null
     */
    User findByEmail(String email);
    
    /**
     * 根据微信OpenID查找用户
     * 
     * @param openId 微信OpenID
     * @return 用户领域模型，如果不存在返回 null
     */
    User findByWechatOpenId(String openId);
    
    /**
     * 保存用户（新增或更新）
     * 
     * @param user 用户领域模型
     * @return 保存后的用户领域模型，包含生成的ID（如果是新增）
     */
    User save(User user);
    
    /**
     * 根据ID查找用户
     * 
     * @param id 用户ID
     * @return 用户领域模型，如果不存在返回 null
     */
    User findById(Long id);
}

