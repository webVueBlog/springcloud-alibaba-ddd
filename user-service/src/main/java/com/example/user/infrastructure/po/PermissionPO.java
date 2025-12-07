package com.example.user.infrastructure.po;

import com.baomidou.mybatisplus.annotation.TableName;
import com.example.common.domain.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

/**
 * 权限持久化对象（PO）
 * <p>
 * 用于 MyBatis Plus 的数据持久化，对应数据库表 sys_permission
 * </p>
 * 
 * @author system
 * @version 1.0.0
 * @since 1.0.0
 */
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@TableName("sys_permission")
public class PermissionPO extends BaseEntity {
    
    /**
     * 权限编码
     */
    private String permissionCode;
    
    /**
     * 权限名称
     */
    private String permissionName;
    
    /**
     * 资源路径
     */
    private String resource;
    
    /**
     * 请求方法
     */
    private String method;
    
    /**
     * 权限描述
     */
    private String description;
    
    /**
     * 权限状态
     * <p>
     * 0-禁用，1-正常
     * </p>
     */
    private Integer status;
    
    /**
     * 权限类型
     * <p>
     * BUTTON - 按钮权限：控制前端按钮、菜单等功能的显示和操作
     * DATA - 数据权限：控制用户能看到和操作的数据范围
     * </p>
     */
    private String permissionType;
}

