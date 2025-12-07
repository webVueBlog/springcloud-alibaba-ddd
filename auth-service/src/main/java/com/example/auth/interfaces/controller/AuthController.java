package com.example.auth.interfaces.controller;

import com.example.auth.application.dto.LoginDTO;
import com.example.auth.application.dto.LoginResultDTO;
import com.example.auth.application.dto.RegisterDTO;
import com.example.auth.application.dto.VerifyCodeDTO;
import com.example.auth.application.service.AuthService;
import com.example.common.result.Result;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;

/**
 * 认证控制器
 * <p>
 * 提供用户认证相关的 REST API 接口，包括：
 * <ul>
 *   <li>多种登录方式（用户名、手机号、验证码、微信等）</li>
 *   <li>验证码发送功能</li>
 * </ul>
 * </p>
 * 
 * @author system
 * @version 1.0.0
 * @since 1.0.0
 */
@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@Validated
public class AuthController {

    private final AuthService authService;

    /**
     * 用户注册
     * <p>
     * 创建新用户账号，注册成功后自动登录并返回 JWT Token
     * </p>
     * 
     * @param registerDTO 注册请求参数，包含用户名、密码等信息
     * @return 登录结果，包含 Token、用户ID和用户名
     */
    @PostMapping("/register")
    public Result<LoginResultDTO> register(@Valid @RequestBody RegisterDTO registerDTO) {
        LoginResultDTO result = authService.register(registerDTO);
        return Result.success(result);
    }

    /**
     * 账号密码登录
     * <p>
     * 使用用户名和密码进行登录认证，验证成功后返回 JWT Token
     * </p>
     * 
     * @param loginDTO 登录请求参数，包含用户名和密码
     * @return 登录结果，包含 Token、用户ID和用户名
     */
    @PostMapping("/login/username")
    public Result<LoginResultDTO> loginByUsername(@Valid @RequestBody LoginDTO loginDTO) {
        LoginResultDTO result = authService.loginByUsername(loginDTO);
        return Result.success(result);
    }

    /**
     * 手机号密码登录
     * <p>
     * 使用手机号和密码进行登录认证
     * </p>
     * 
     * @param loginDTO 登录请求参数，包含手机号和密码
     * @return 登录结果，包含 Token、用户ID和用户名
     */
    @PostMapping("/login/phone")
    public Result<LoginResultDTO> loginByPhone(@Valid @RequestBody LoginDTO loginDTO) {
        LoginResultDTO result = authService.loginByPhone(loginDTO);
        return Result.success(result);
    }

    /**
     * 手机号验证码登录
     * <p>
     * 使用手机号和验证码进行登录，如果用户不存在会自动注册
     * </p>
     * 
     * @param phone 手机号，格式：11位数字
     * @param code 验证码，6位数字
     * @return 登录结果，包含 Token、用户ID和用户名
     */
    @PostMapping("/login/phone-code")
    public Result<LoginResultDTO> loginByPhoneCode(
            @RequestParam @NotBlank(message = "手机号不能为空") 
            @Pattern(regexp = "^1[3-9]\\d{9}$", message = "手机号格式不正确") String phone, 
            @RequestParam @NotBlank(message = "验证码不能为空") 
            @Pattern(regexp = "^\\d{6}$", message = "验证码必须是6位数字") String code) {
        LoginResultDTO result = authService.loginByPhoneCode(phone, code);
        return Result.success(result);
    }

    /**
     * 发送手机验证码
     * <p>
     * 向指定手机号发送6位数字验证码，验证码有效期5分钟
     * </p>
     * 
     * @param dto 验证码请求参数，包含手机号
     * @return 操作结果
     */
    @PostMapping("/verify-code/phone")
    public Result<Void> sendPhoneVerifyCode(@Valid @RequestBody VerifyCodeDTO dto) {
        authService.sendPhoneVerifyCode(dto.getPhone());
        return Result.success();
    }

    /**
     * 发送邮箱验证码
     * <p>
     * 向指定邮箱发送6位数字验证码，验证码有效期5分钟
     * </p>
     * 
     * @param dto 验证码请求参数，包含邮箱地址
     * @return 操作结果
     */
    @PostMapping("/verify-code/email")
    public Result<Void> sendEmailVerifyCode(@Valid @RequestBody VerifyCodeDTO dto) {
        authService.sendEmailVerifyCode(dto.getEmail());
        return Result.success();
    }

    /**
     * 微信登录（PC端）
     * <p>
     * 通过微信授权码进行登录，需要用户已绑定微信账号
     * </p>
     * 
     * @param code 微信授权码
     * @return 登录结果，包含 Token、用户ID和用户名
     */
    @PostMapping("/login/wechat")
    public Result<LoginResultDTO> loginByWechat(
            @RequestParam @NotBlank(message = "微信授权码不能为空") String code) {
        LoginResultDTO result = authService.loginByWechat(code);
        return Result.success(result);
    }

    /**
     * 小程序微信登录
     * <p>
     * 通过微信小程序授权码进行登录，如果用户不存在会自动注册
     * </p>
     * 
     * @param code 微信小程序授权码
     * @return 登录结果，包含 Token、用户ID和用户名
     */
    @PostMapping("/login/miniprogram")
    public Result<LoginResultDTO> loginByMiniProgram(
            @RequestParam @NotBlank(message = "微信小程序授权码不能为空") String code) {
        LoginResultDTO result = authService.loginByMiniProgram(code);
        return Result.success(result);
    }

    /**
     * 邮箱注册
     * <p>
     * 使用邮箱和验证码进行注册，注册成功后自动登录并返回 JWT Token
     * </p>
     * 
     * @param registerDTO 邮箱注册请求参数
     * @return 登录结果，包含 Token、用户ID和用户名
     */
    @PostMapping("/register/email")
    public Result<LoginResultDTO> registerByEmail(@Valid @RequestBody com.example.auth.application.dto.EmailRegisterDTO registerDTO) {
        LoginResultDTO result = authService.registerByEmail(registerDTO);
        return Result.success(result);
    }

    /**
     * 忘记密码
     * <p>
     * 发送密码重置邮件，包含重置令牌链接
     * </p>
     * 
     * @param forgetPasswordDTO 忘记密码请求参数
     * @return 操作结果
     */
    @PostMapping("/forget-password")
    public Result<Void> forgetPassword(@Valid @RequestBody com.example.auth.application.dto.ForgetPasswordDTO forgetPasswordDTO) {
        authService.forgetPassword(forgetPasswordDTO);
        return Result.success();
    }

    /**
     * 重置密码
     * <p>
     * 使用重置令牌重置用户密码
     * </p>
     * 
     * @param resetPasswordDTO 重置密码请求参数
     * @return 操作结果
     */
    @PostMapping("/reset-password")
    public Result<Void> resetPassword(@Valid @RequestBody com.example.auth.application.dto.ResetPasswordDTO resetPasswordDTO) {
        authService.resetPassword(resetPasswordDTO);
        return Result.success();
    }
}

