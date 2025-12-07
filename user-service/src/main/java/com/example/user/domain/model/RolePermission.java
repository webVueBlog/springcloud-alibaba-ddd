package com.example.user.domain.model;

import com.example.common.domain.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 角色权限关联领域模型
 * <p>
 * 用于表示角色和权限的多对多关系
 * </p>
 * <p>
 * 关系说明：
 * <ul>
 *   <li>一个角色可以拥有多个权限</li>
 *   <li>一个权限可以分配给多个角色</li>
 *   <li>通过 RolePermission 表维护角色和权限的关联关系</li>
 * </ul>
 * </p>
 * <p>
 * 权限继承：
 * <ul>
 *   <li>用户通过角色间接拥有权限</li>
 *   <li>用户权限 = 用户所有角色的权限集合</li>
 * </ul>
 * </p>
 * <p>
 * 继承说明：
 * <ul>
 *   <li>继承 BaseEntity，包含 id、createTime、updateTime、deleted 等基础字段</li>
 *   <li>使用 MyBatis Plus 的逻辑删除功能</li>
 * </ul>
 * </p>
 * 
 * @author system
 * @version 1.0.0
 * @since 1.0.0
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class RolePermission extends BaseEntity {
    
    /**
     * 角色ID
     * <p>
     * 关联的角色ID，对应 sys_role 表的 id
     * </p>
     */
    private Long roleId;
    
    /**
     * 权限ID
     * <p>
     * 关联的权限ID，对应 sys_permission 表的 id
     * </p>
     */
    private Long permissionId;
}

