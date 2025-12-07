package com.example.auth.application.dto;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;

/**
 * 登录请求数据传输对象
 * <p>
 * 用于接收用户登录请求的参数，支持多种登录方式：
 * <ul>
 *   <li>用户名密码登录：需要 username 和 password</li>
 *   <li>手机号密码登录：需要 phone 和 password</li>
 *   <li>手机号验证码登录：需要 phone 和 code（通过单独接口获取）</li>
 * </ul>
 * </p>
 * 
 * @author system
 * @version 1.0.0
 * @since 1.0.0
 */
@Data
public class LoginDTO {
    
    /**
     * 用户名
     * <p>用于用户名密码登录方式</p>
     */
    @NotBlank(message = "用户名不能为空", groups = {UsernameLogin.class})
    private String username;
    
    /**
     * 密码
     * <p>用于用户名密码登录或手机号密码登录方式</p>
     */
    @NotBlank(message = "密码不能为空", groups = {UsernameLogin.class, PhoneLogin.class})
    private String password;
    
    /**
     * 手机号
     * <p>用于手机号密码登录或手机号验证码登录方式</p>
     */
    @NotBlank(message = "手机号不能为空", groups = {PhoneLogin.class, PhoneCodeLogin.class})
    @Pattern(regexp = "^1[3-9]\\d{9}$", message = "手机号格式不正确", groups = {PhoneLogin.class, PhoneCodeLogin.class})
    private String phone;
    
    /**
     * 邮箱
     * <p>预留字段，用于邮箱登录方式</p>
     */
    private String email;
    
    /**
     * 验证码
     * <p>用于手机号验证码登录方式</p>
     */
    @NotBlank(message = "验证码不能为空", groups = {PhoneCodeLogin.class})
    @Pattern(regexp = "^\\d{6}$", message = "验证码必须是6位数字", groups = {PhoneCodeLogin.class})
    private String code;
    
    /**
     * 用户名登录验证组
     */
    public interface UsernameLogin {}
    
    /**
     * 手机号密码登录验证组
     */
    public interface PhoneLogin {}
    
    /**
     * 手机号验证码登录验证组
     */
    public interface PhoneCodeLogin {}
}

