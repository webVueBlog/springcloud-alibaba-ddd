package com.example.common.permission;

/**
 * 数据权限类型枚举
 * 
 * @author system
 * @version 1.0.0
 * @since 1.0.0
 */
public enum DataPermissionType {
    
    /**
     * 全部数据权限
     * <p>
     * 可以访问所有数据
     * </p>
     */
    ALL("全部数据"),
    
    /**
     * 部门数据权限
     * <p>
     * 只能访问本部门的数据（需要扩展部门表）
     * </p>
     */
    DEPT("部门数据"),
    
    /**
     * 自己数据权限
     * <p>
     * 只能访问自己的数据
     * </p>
     */
    SELF("自己数据");
    
    private final String description;
    
    DataPermissionType(String description) {
        this.description = description;
    }
    
    public String getDescription() {
        return description;
    }
}

