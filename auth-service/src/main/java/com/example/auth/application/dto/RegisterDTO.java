package com.example.auth.application.dto;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

/**
 * 用户注册请求数据传输对象
 * <p>
 * 用于接收用户注册请求的参数，包含用户名、密码、手机号、邮箱等信息
 * </p>
 * 
 * @author system
 * @version 1.0.0
 * @since 1.0.0
 */
@Data
public class RegisterDTO {
    
    /**
     * 用户名
     * <p>用于登录，必须唯一</p>
     */
    @NotBlank(message = "用户名不能为空")
    @Size(min = 3, max = 50, message = "用户名长度必须在3-50个字符之间")
    @Pattern(regexp = "^[a-zA-Z0-9_]+$", message = "用户名只能包含字母、数字和下划线")
    private String username;
    
    /**
     * 密码
     * <p>用于登录认证</p>
     */
    @NotBlank(message = "密码不能为空")
    @Size(min = 6, max = 20, message = "密码长度必须在6-20个字符之间")
    private String password;
    
    /**
     * 确认密码
     * <p>必须与密码一致</p>
     */
    @NotBlank(message = "确认密码不能为空")
    private String confirmPassword;
    
    /**
     * 手机号
     * <p>11位数字，可选字段</p>
     */
    @Pattern(regexp = "^$|^1[3-9]\\d{9}$", message = "手机号格式不正确")
    private String phone;
    
    /**
     * 邮箱地址
     * <p>可选字段</p>
     */
    @Pattern(regexp = "^$|^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$", message = "邮箱格式不正确")
    private String email;
}

