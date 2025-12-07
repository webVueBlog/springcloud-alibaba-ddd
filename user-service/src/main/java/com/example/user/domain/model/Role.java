package com.example.user.domain.model;

import com.example.common.domain.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 角色领域模型
 * <p>
 * 角色的聚合根，包含角色的核心业务属性
 * </p>
 * <p>
 * 角色状态说明：
 * <ul>
 *   <li>0 - 禁用：角色已禁用，不能使用</li>
 *   <li>1 - 正常：角色正常，可以使用</li>
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
public class Role extends BaseEntity {
    
    /**
     * 角色编码
     * <p>
     * 角色的唯一标识，如 "ADMIN"、"USER" 等
     * </p>
     */
    private String roleCode;
    
    /**
     * 角色名称
     * <p>
     * 角色的显示名称，如 "管理员"、"普通用户" 等
     * </p>
     */
    private String roleName;
    
    /**
     * 角色描述
     * <p>
     * 角色的详细描述信息，可选
     * </p>
     */
    private String description;
    
    /**
     * 角色状态
     * <p>
     * 0-禁用，1-正常
     * </p>
     */
    private Integer status;
}

