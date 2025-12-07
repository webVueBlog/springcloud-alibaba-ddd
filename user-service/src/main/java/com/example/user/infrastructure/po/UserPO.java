package com.example.user.infrastructure.po;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 用户持久化对象
 * <p>
 * 对应数据库表 sys_user，用于 MyBatis-Plus 的数据持久化操作
 * </p>
 * 
 * @author system
 * @version 1.0.0
 * @since 1.0.0
 */
@Data
@TableName("sys_user")
public class UserPO {
    
    /** 用户ID，使用雪花算法自动生成 */
    @TableId(type = IdType.ASSIGN_ID)
    private Long id;
    
    /** 用户名 */
    private String username;
    
    /** 加密后的密码 */
    private String password;
    
    /** 手机号 */
    private String phone;
    
    /** 邮箱地址 */
    private String email;
    
    /** 微信OpenID */
    private String wechatOpenId;
    
    /** 微信UnionID */
    private String wechatUnionId;
    
    /** 用户状态：1-正常，0-禁用 */
    private Integer status;
    
    /** 密码加密盐值 */
    private String salt;
    
    /** 创建时间 */
    private LocalDateTime createTime;
    
    /** 更新时间 */
    private LocalDateTime updateTime;
    
    /** 逻辑删除标记：0-未删除，1-已删除 */
    private Integer deleted;
}

