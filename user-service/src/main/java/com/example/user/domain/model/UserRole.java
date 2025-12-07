package com.example.user.domain.model;

import com.example.common.domain.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 用户角色关联领域模型
 * <p>
 * 用于表示用户和角色的多对多关系
 * </p>
 * <p>
 * 关系说明：
 * <ul>
 *   <li>一个用户可以有多个角色</li>
 *   <li>一个角色可以分配给多个用户</li>
 *   <li>通过 UserRole 表维护用户和角色的关联关系</li>
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
public class UserRole extends BaseEntity {
    
    /**
     * 用户ID
     * <p>
     * 关联的用户ID，对应 sys_user 表的 id
     * </p>
     */
    private Long userId;
    
    /**
     * 角色ID
     * <p>
     * 关联的角色ID，对应 sys_role 表的 id
     * </p>
     */
    private Long roleId;
}

