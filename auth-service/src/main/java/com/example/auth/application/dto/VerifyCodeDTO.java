package com.example.auth.application.dto;

import lombok.Data;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;

/**
 * 验证码请求数据传输对象
 * <p>
 * 用于发送手机或邮箱验证码的请求参数
 * </p>
 * 
 * @author system
 * @version 1.0.0
 * @since 1.0.0
 */
@Data
public class VerifyCodeDTO {
    
    /**
     * 手机号
     * <p>用于发送手机验证码，格式：11位数字，以1开头</p>
     */
    @NotBlank(message = "手机号不能为空", groups = {PhoneVerify.class})
    @Pattern(regexp = "^1[3-9]\\d{9}$", message = "手机号格式不正确", groups = {PhoneVerify.class})
    private String phone;
    
    /**
     * 邮箱地址
     * <p>用于发送邮箱验证码</p>
     */
    @NotBlank(message = "邮箱不能为空", groups = {EmailVerify.class})
    @Email(message = "邮箱格式不正确", groups = {EmailVerify.class})
    private String email;
    
    /**
     * 手机验证码验证组
     */
    public interface PhoneVerify {}
    
    /**
     * 邮箱验证码验证组
     */
    public interface EmailVerify {}
}

