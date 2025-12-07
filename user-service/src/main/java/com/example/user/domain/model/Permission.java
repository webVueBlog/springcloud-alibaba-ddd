package com.example.user.domain.model;

import com.example.common.domain.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 权限领域模型
 * <p>
 * 权限的聚合根，包含权限的核心业务属性
 * </p>
 * <p>
 * 权限状态说明：
 * <ul>
 *   <li>0 - 禁用：权限已禁用，不能使用</li>
 *   <li>1 - 正常：权限正常，可以使用</li>
 * </ul>
 * </p>
 * <p>
 * 权限资源说明：
 * <ul>
 *   <li>resource：资源路径，如 "/api/user/**"</li>
 *   <li>method：请求方法，如 "GET"、"POST"、"PUT"、"DELETE" 等</li>
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
public class Permission extends BaseEntity {
    
    /**
     * 权限编码
     * <p>
     * 权限的唯一标识，如 "USER_VIEW"、"ORDER_CREATE" 等
     * </p>
     */
    private String permissionCode;
    
    /**
     * 权限名称
     * <p>
     * 权限的显示名称，如 "查看用户"、"创建订单" 等
     * </p>
     */
    private String permissionName;
    
    /**
     * 资源路径
     * <p>
     * 权限对应的资源路径，支持通配符，如 "/api/user/**"
     * </p>
     */
    private String resource;
    
    /**
     * 请求方法
     * <p>
     * 权限对应的 HTTP 请求方法，如 "GET"、"POST"、"PUT"、"DELETE" 等
     * </p>
     */
    private String method;
    
    /**
     * 权限描述
     * <p>
     * 权限的详细描述信息，可选
     * </p>
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

