package com.example.user.infrastructure.po;

import com.baomidou.mybatisplus.annotation.TableName;
import com.example.common.domain.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

/**
 * 角色权限关联持久化对象（PO）
 * <p>
 * 用于 MyBatis Plus 的数据持久化，对应数据库表 sys_role_permission
 * </p>
 * 
 * @author system
 * @version 1.0.0
 * @since 1.0.0
 */
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@TableName("sys_role_permission")
public class RolePermissionPO extends BaseEntity {
    
    /**
     * 角色ID
     */
    private Long roleId;
    
    /**
     * 权限ID
     */
    private Long permissionId;
}

