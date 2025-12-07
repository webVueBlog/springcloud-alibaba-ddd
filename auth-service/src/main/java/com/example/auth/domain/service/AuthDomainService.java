package com.example.auth.domain.service;

import com.example.auth.domain.model.User;
import com.example.encrypt.EncryptService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

/**
 * 认证领域服务
 * <p>
 * 负责认证相关的领域逻辑，包括密码验证和加密
 * </p>
 * 
 * @author system
 * @version 1.0.0
 * @since 1.0.0
 */
@Service
@RequiredArgsConstructor
public class AuthDomainService {

    /** 加密服务 */
    private final EncryptService encryptService;

    /**
     * 验证密码
     * <p>
     * 将输入的密码与用户盐值组合后进行MD5加密，然后与数据库中存储的加密密码进行比较
     * </p>
     * 
     * @param user 用户领域模型，必须包含 password 和 salt
     * @param inputPassword 用户输入的原始密码
     * @return true 表示密码正确，false 表示密码错误
     */
    public boolean validatePassword(User user, String inputPassword) {
        String encryptedPassword = encryptService.encryptMD5(inputPassword + user.getSalt());
        return encryptedPassword.equals(user.getPassword());
    }

    /**
     * 加密密码
     * <p>
     * 将密码与盐值组合后进行MD5加密
     * </p>
     * 
     * @param password 原始密码
     * @param salt 盐值
     * @return MD5加密后的密码字符串
     */
    public String encryptPassword(String password, String salt) {
        return encryptService.encryptMD5(password + salt);
    }
}

