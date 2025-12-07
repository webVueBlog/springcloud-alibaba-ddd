package com.example.user.domain.model;

import com.example.common.domain.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;

/**
 * 菜单领域模型
 * <p>
 * 菜单的聚合根，包含菜单的核心业务属性
 * </p>
 * <p>
 * 菜单状态说明：
 * <ul>
 *   <li>0 - 禁用：菜单已禁用，不能使用</li>
 *   <li>1 - 正常：菜单正常，可以使用</li>
 * </ul>
 * </p>
 * <p>
 * 菜单树形结构说明：
 * <ul>
 *   <li>通过 parentId 字段实现树形结构</li>
 *   <li>parentId 为 null 或 0 表示顶级菜单</li>
 *   <li>通过 children 字段存储子菜单列表</li>
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
public class Menu extends BaseEntity {
    
    /**
     * 父菜单ID
     * <p>
     * 父菜单的ID，如果为 null 或 0 表示顶级菜单
     * </p>
     */
    private Long parentId;
    
    /**
     * 菜单编码
     * <p>
     * 菜单的唯一标识，如 "SYSTEM_MENU"、"SYSTEM_USER" 等
     * </p>
     */
    private String menuCode;
    
    /**
     * 菜单名称
     * <p>
     * 菜单的显示名称，如 "菜单管理"、"用户管理" 等
     * </p>
     */
    private String menuName;
    
    /**
     * 路由路径
     * <p>
     * 前端路由路径，如 "/system/menu"、"/system/user" 等
     * </p>
     */
    private String path;
    
    /**
     * 图标
     * <p>
     * 菜单图标名称，如 "Menu"、"User" 等
     * </p>
     */
    private String icon;
    
    /**
     * 排序
     * <p>
     * 菜单排序号，数字越小越靠前
     * </p>
     */
    private Integer sort;
    
    /**
     * 菜单描述
     * <p>
     * 菜单的详细描述信息，可选
     * </p>
     */
    private String description;
    
    /**
     * 菜单状态
     * <p>
     * 0-禁用，1-正常
     * </p>
     */
    private Integer status;
    
    /**
     * 子菜单列表
     * <p>
     * 用于构建树形结构，不持久化到数据库
     * </p>
     */
    private List<Menu> children;
}

