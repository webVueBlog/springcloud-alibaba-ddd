package com.example.encrypt.impl;

import cn.hutool.core.codec.Base64;
import cn.hutool.crypto.SecureUtil;
import cn.hutool.crypto.digest.DigestUtil;
import cn.hutool.crypto.symmetric.AES;
import com.example.encrypt.EncryptService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * 加密解密服务实现
 * <p>
 * 基于 Hutool 工具库实现的加密解密服务
 * 提供 AES 对称加密、MD5/SHA256 哈希加密、Base64 编码等功能
 * </p>
 * <p>
 * 实现说明：
 * <ul>
 *   <li>AES 加密：使用 Hutool 的 SecureUtil.aes() 实现</li>
 *   <li>MD5/SHA256：使用 Hutool 的 DigestUtil 实现</li>
 *   <li>Base64：使用 Hutool 的 Base64 实现</li>
 * </ul>
 * </p>
 * 
 * @author system
 * @version 1.0.0
 * @since 1.0.0
 */
@Slf4j
@Service
public class EncryptServiceImpl implements EncryptService {

    /**
     * AES 对称加密
     * 
     * @param data 待加密的原始数据
     * @param key 加密密钥
     * @return Base64 编码的加密结果
     */
    @Override
    public String encryptAES(String data, String key) {
        try {
            if (data == null) {
                throw new IllegalArgumentException("待加密数据不能为空");
            }
            if (key == null || key.isEmpty()) {
                throw new IllegalArgumentException("加密密钥不能为空");
            }
            
            // 验证密钥长度（AES 密钥长度必须是 16、24 或 32 字节）
            byte[] keyBytes = key.getBytes();
            if (keyBytes.length != 16 && keyBytes.length != 24 && keyBytes.length != 32) {
                throw new IllegalArgumentException("AES 密钥长度必须是 16、24 或 32 字节，当前长度: " + keyBytes.length);
            }
            
            AES aes = SecureUtil.aes(keyBytes);
            String encrypted = aes.encryptBase64(data);
            log.debug("AES 加密成功: dataLength={}, keyLength={}", data.length(), keyBytes.length);
            return encrypted;
        } catch (Exception e) {
            log.error("AES 加密失败: dataLength={}, keyLength={}", 
                    data != null ? data.length() : 0, 
                    key != null ? key.getBytes().length : 0, e);
            throw new RuntimeException("AES 加密失败: " + e.getMessage(), e);
        }
    }

    /**
     * AES 对称解密
     * 
     * @param encryptedData Base64 编码的加密数据
     * @param key 解密密钥
     * @return 解密后的原始数据
     */
    @Override
    public String decryptAES(String encryptedData, String key) {
        try {
            if (encryptedData == null || encryptedData.isEmpty()) {
                throw new IllegalArgumentException("待解密数据不能为空");
            }
            if (key == null || key.isEmpty()) {
                throw new IllegalArgumentException("解密密钥不能为空");
            }
            
            // 验证密钥长度
            byte[] keyBytes = key.getBytes();
            if (keyBytes.length != 16 && keyBytes.length != 24 && keyBytes.length != 32) {
                throw new IllegalArgumentException("AES 密钥长度必须是 16、24 或 32 字节，当前长度: " + keyBytes.length);
            }
            
            AES aes = SecureUtil.aes(keyBytes);
            String decrypted = aes.decryptStr(encryptedData);
            log.debug("AES 解密成功: encryptedDataLength={}, keyLength={}", encryptedData.length(), keyBytes.length);
            return decrypted;
        } catch (Exception e) {
            log.error("AES 解密失败: encryptedDataLength={}, keyLength={}", 
                    encryptedData != null ? encryptedData.length() : 0, 
                    key != null ? key.getBytes().length : 0, e);
            throw new RuntimeException("AES 解密失败: " + e.getMessage(), e);
        }
    }

    /**
     * MD5 哈希加密
     * 
     * @param data 待加密的原始数据
     * @return 32 位十六进制 MD5 哈希值
     */
    @Override
    public String encryptMD5(String data) {
        try {
            if (data == null) {
                throw new IllegalArgumentException("待加密数据不能为空");
            }
            String md5 = DigestUtil.md5Hex(data);
            log.debug("MD5 加密成功: dataLength={}", data.length());
            return md5;
        } catch (Exception e) {
            log.error("MD5 加密失败: dataLength={}", data != null ? data.length() : 0, e);
            throw new RuntimeException("MD5 加密失败: " + e.getMessage(), e);
        }
    }

    /**
     * SHA256 哈希加密
     * 
     * @param data 待加密的原始数据
     * @return 64 位十六进制 SHA256 哈希值
     */
    @Override
    public String encryptSHA256(String data) {
        try {
            if (data == null) {
                throw new IllegalArgumentException("待加密数据不能为空");
            }
            String sha256 = DigestUtil.sha256Hex(data);
            log.debug("SHA256 加密成功: dataLength={}", data.length());
            return sha256;
        } catch (Exception e) {
            log.error("SHA256 加密失败: dataLength={}", data != null ? data.length() : 0, e);
            throw new RuntimeException("SHA256 加密失败: " + e.getMessage(), e);
        }
    }

    /**
     * Base64 编码
     * 
     * @param data 待编码的原始数据
     * @return Base64 编码后的字符串
     */
    @Override
    public String encodeBase64(String data) {
        try {
            if (data == null) {
                throw new IllegalArgumentException("待编码数据不能为空");
            }
            String encoded = Base64.encode(data);
            log.debug("Base64 编码成功: dataLength={}", data.length());
            return encoded;
        } catch (Exception e) {
            log.error("Base64 编码失败: dataLength={}", data != null ? data.length() : 0, e);
            throw new RuntimeException("Base64 编码失败: " + e.getMessage(), e);
        }
    }

    /**
     * Base64 解码
     * 
     * @param encodedData Base64 编码的字符串
     * @return 解码后的原始数据
     */
    @Override
    public String decodeBase64(String encodedData) {
        try {
            if (encodedData == null || encodedData.isEmpty()) {
                throw new IllegalArgumentException("待解码数据不能为空");
            }
            String decoded = Base64.decodeStr(encodedData);
            log.debug("Base64 解码成功: encodedDataLength={}", encodedData.length());
            return decoded;
        } catch (Exception e) {
            log.error("Base64 解码失败: encodedDataLength={}", encodedData != null ? encodedData.length() : 0, e);
            throw new RuntimeException("Base64 解码失败: " + e.getMessage(), e);
        }
    }
}

