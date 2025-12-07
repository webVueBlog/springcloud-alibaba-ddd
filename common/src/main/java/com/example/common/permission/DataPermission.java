package com.example.common.permission;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 数据权限注解
 * <p>
 * 用于标记需要数据权限控制的方法
 * </p>
 * 
 * @author system
 * @version 1.0.0
 * @since 1.0.0
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface DataPermission {
    
    /**
     * 权限编码
     * <p>
     * 如 "ARTICLE_DATA_ALL"、"ARTICLE_DATA_SELF" 等
     * </p>
     */
    String value() default "";
    
    /**
     * 数据权限类型
     * <p>
     * ALL - 全部数据权限
     * DEPT - 部门数据权限
     * SELF - 自己数据权限
     * </p>
     */
    DataPermissionType type() default DataPermissionType.SELF;
    
    /**
     * 资源字段名
     * <p>
     * 用于标识数据所有者的字段，如 "userId"、"authorId" 等
     * </p>
     */
    String resourceField() default "userId";
}

