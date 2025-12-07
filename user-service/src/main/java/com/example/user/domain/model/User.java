package com.example.user.domain.model;

import com.example.common.domain.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 用户领域模型
 * <p>
 * 表示系统中的用户实体，包含用户的基本信息
 * </p>
 * 
 * @author system
 * @version 1.0.0
 * @since 1.0.0
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class User extends BaseEntity {
    
    /** 用户名，用于登录 */
    private String username;
    
    /** 加密后的密码 */
    private String password;
    
    /** 手机号，11位数字 */
    private String phone;
    
    /** 邮箱地址 */
    private String email;
    
    /** 微信OpenID，用于微信登录 */
    private String wechatOpenId;
    
    /** 微信UnionID，用于跨平台用户识别 */
    private String wechatUnionId;
    
    /** 用户状态：1-正常，0-禁用 */
    private Integer status;
    
    /** 密码加密盐值，用于增强密码安全性 */
    private String salt;
}

