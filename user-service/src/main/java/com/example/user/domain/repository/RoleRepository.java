package com.example.user.domain.repository;

import com.example.user.domain.model.Role;

import java.util.List;

/**
 * 角色仓储接口
 * <p>
 * 定义角色的持久化操作，属于领域层，不依赖具体的技术实现
 * </p>
 * <p>
 * 仓储模式说明：
 * <ul>
 *   <li>仓储接口定义在领域层，保持领域层纯净</li>
 *   <li>仓储实现在基础设施层，使用 MyBatis Plus 进行数据访问</li>
 *   <li>通过仓储模式隔离领域模型和持久化技术</li>
 * </ul>
 * </p>
 * 
 * @author system
 * @version 1.0.0
 * @since 1.0.0
 */
public interface RoleRepository {
    
    /**
     * 根据ID查询角色
     * 
     * @param id 角色ID，不能为 null
     * @return 角色领域模型，如果不存在返回 null
     */
    Role findById(Long id);
    
    /**
     * 根据角色编码查询角色
     * 
     * @param roleCode 角色编码，不能为 null 或空字符串
     * @return 角色领域模型，如果不存在返回 null
     */
    Role findByRoleCode(String roleCode);
    
    /**
     * 查询所有角色
     * 
     * @return 角色列表，如果不存在返回空列表
     */
    List<Role> findAll();
    
    /**
     * 保存角色（新增或更新）
     * 
     * @param role 角色领域模型，不能为 null
     * @return 保存后的角色领域模型
     */
    Role save(Role role);
    
    /**
     * 删除角色（逻辑删除）
     * 
     * @param id 角色ID，不能为 null
     */
    void delete(Long id);
}

