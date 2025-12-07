package com.example.user.infrastructure.po;

import com.baomidou.mybatisplus.annotation.TableName;
import com.example.common.domain.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

/**
 * 菜单持久化对象（PO）
 * <p>
 * 用于 MyBatis Plus 的数据持久化，对应数据库表 sys_menu
 * </p>
 * 
 * @author system
 * @version 1.0.0
 * @since 1.0.0
 */
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@TableName("sys_menu")
public class MenuPO extends BaseEntity {
    
    /**
     * 父菜单ID
     */
    private Long parentId;
    
    /**
     * 菜单编码
     */
    private String menuCode;
    
    /**
     * 菜单名称
     */
    private String menuName;
    
    /**
     * 路由路径
     */
    private String path;
    
    /**
     * 图标
     */
    private String icon;
    
    /**
     * 排序
     */
    private Integer sort;
    
    /**
     * 菜单描述
     */
    private String description;
    
    /**
     * 菜单状态
     * <p>
     * 0-禁用，1-正常
     * </p>
     */
    private Integer status;
}

