package com.example.common.permission;

/**
 * 访问级别枚举
 * <p>
 * 用于控制内容的访问权限
 * </p>
 * 
 * @author system
 * @version 1.0.0
 * @since 1.0.0
 */
public enum AccessLevel {
    
    /**
     * 公开访问
     * <p>
     * 所有人都可以访问
     * </p>
     */
    PUBLIC("公开"),
    
    /**
     * 私有访问
     * <p>
     * 仅作者本人可以访问
     * </p>
     */
    PRIVATE("私有"),
    
    /**
     * 付费可见
     * <p>
     * 需要付费后才能访问
     * </p>
     */
    PAID("付费可见"),
    
    /**
     * 指定用户可见
     * <p>
     * 只有指定的用户或组织可以访问
     * </p>
     */
    SPECIFIED("指定用户可见");
    
    private final String description;
    
    AccessLevel(String description) {
        this.description = description;
    }
    
    public String getDescription() {
        return description;
    }
    
    /**
     * 根据字符串值获取枚举
     */
    public static AccessLevel fromString(String value) {
        if (value == null) {
            return PUBLIC;
        }
        for (AccessLevel level : values()) {
            if (level.name().equalsIgnoreCase(value)) {
                return level;
            }
        }
        return PUBLIC;
    }
}

