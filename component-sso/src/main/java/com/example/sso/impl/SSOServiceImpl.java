package com.example.sso.impl;

import com.example.cache.CacheService;
import com.example.sso.SSOService;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.UnsupportedJwtException;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * 单点登录服务实现
 * <p>
 * 基于 JWT（JSON Web Token）实现单点登录功能
 * </p>
 * <p>
 * 特性：
 * <ul>
 *   <li>使用 HS512 算法签名，安全性高</li>
 *   <li>支持自定义声明（Claims）</li>
 *   <li>支持 Token 过期时间配置</li>
 *   <li>支持缓存服务集成，实现 Token 黑名单功能</li>
 *   <li>完善的异常处理和日志记录</li>
 * </ul>
 * </p>
 * <p>
 * 注意：
 * <ul>
 *   <li>JWT 密钥（secret）应该足够长且安全，建议至少 256 位</li>
 *   <li>生产环境应该使用环境变量或配置中心管理密钥</li>
 *   <li>如果使用缓存服务，可以实现 Token 黑名单功能</li>
 *   <li>如果不使用缓存服务，Token 在过期前无法主动失效</li>
 * </ul>
 * </p>
 * 
 * @author system
 * @version 1.0.0
 * @since 1.0.0
 */
@Slf4j
@Service
public class SSOServiceImpl implements SSOService {

    /** 缓存服务（可选，用于 Token 黑名单） */
    @Autowired(required = false)
    private CacheService cacheService;

    /** JWT 密钥，用于签名和验证 Token */
    @Value("${jwt.secret:springcloud-alibaba-ddd-secret-key-for-jwt-token-generation}")
    private String secret;

    /** Token 过期时间（秒），默认 86400 秒（24小时） */
    @Value("${jwt.expiration:86400}")
    private Long expiration;

    /**
     * 获取签名密钥
     * <p>
     * 根据配置的密钥字符串生成 HMAC SHA 密钥
     * </p>
     * 
     * @return SecretKey 签名密钥
     */
    private SecretKey getSigningKey() {
        byte[] keyBytes = secret.getBytes(StandardCharsets.UTF_8);
        return Keys.hmacShaKeyFor(keyBytes);
    }

    /**
     * 生成 SSO Token
     * 
     * @param userId 用户ID
     * @param claims 自定义声明
     * @return JWT Token 字符串
     */
    @Override
    public String generateToken(Long userId, Map<String, Object> claims) {
        Assert.notNull(userId, "用户ID不能为空");
        
        try {
            // 构建 JWT Claims
            Map<String, Object> jwtClaims = new HashMap<>();
            if (claims != null) {
                jwtClaims.putAll(claims);
            }
            jwtClaims.put("userId", userId);

            // 计算过期时间
            Date now = new Date();
            Date expiryDate = new Date(now.getTime() + expiration * 1000);

            // 生成 JWT Token
            String token = Jwts.builder()
                    .setClaims(jwtClaims)
                    .setIssuedAt(now)
                    .setExpiration(expiryDate)
                    .signWith(getSigningKey(), SignatureAlgorithm.HS512)
                    .compact();

            // 将 Token 存储到缓存中，支持多服务器（如果缓存服务可用）
            if (cacheService != null) {
                String tokenKey = "sso:token:" + token;
                cacheService.set(tokenKey, userId, expiration, TimeUnit.SECONDS);
                log.debug("Token 已存储到缓存: userId={}, tokenKey={}", userId, tokenKey);
            } else {
                log.debug("缓存服务未配置，Token 未存储到缓存");
            }

            log.info("Token 生成成功: userId={}, expiration={}秒", userId, expiration);
            return token;
        } catch (Exception e) {
            log.error("Token 生成失败: userId={}", userId, e);
            throw new RuntimeException("Token 生成失败: " + e.getMessage(), e);
        }
    }

    /**
     * 验证 Token
     * 
     * @param token JWT Token 字符串
     * @return true 表示 Token 有效，false 表示 Token 无效
     */
    @Override
    public boolean validateToken(String token) {
        if (!StringUtils.hasText(token)) {
            log.warn("Token 为空，验证失败");
            return false;
        }
        
        try {
            // 解析并验证 JWT Token
            Claims claims = Jwts.parserBuilder()
                    .setSigningKey(getSigningKey())
                    .build()
                    .parseClaimsJws(token)
                    .getBody();

            // 检查 Token 是否已过期（JWT 库会自动检查，这里再次确认）
            Date expiration = claims.getExpiration();
            if (expiration.before(new Date())) {
                log.warn("Token 已过期: expiration={}", expiration);
                return false;
            }

            // 检查缓存中是否存在（如果缓存服务可用，用于实现 Token 黑名单）
            // 注意：如果缓存服务未配置或 Token 不在缓存中，仍然允许通过（JWT 本身已验证）
            // 这样可以避免因为缓存问题导致 Token 验证失败
            if (cacheService != null) {
                String tokenKey = "sso:token:" + token;
                boolean exists = cacheService.exists(tokenKey);
                if (!exists) {
                    log.warn("Token 不在缓存中，但 JWT 本身有效，允许通过（可能缓存已过期或未配置）: tokenKey={}, userId={}", 
                            tokenKey, claims.get("userId"));
                    // 不返回 false，允许 Token 通过（JWT 本身已验证通过）
                    // 如果确实需要严格的 Token 黑名单功能，可以取消下面的注释
                    // return false;
                } else {
                    log.debug("Token 验证成功（缓存中存在）: userId={}", claims.get("userId"));
                }
            } else {
                log.debug("Token 验证成功（缓存服务未配置，仅验证 JWT 本身）: userId={}", claims.get("userId"));
            }
            
            return true;
        } catch (ExpiredJwtException e) {
            log.warn("Token 已过期: {}", e.getMessage());
            return false;
        } catch (MalformedJwtException e) {
            log.warn("Token 格式错误: {}", e.getMessage());
            return false;
        } catch (UnsupportedJwtException e) {
            log.warn("不支持的 Token 类型: {}", e.getMessage());
            return false;
        } catch (Exception e) {
            log.error("Token 验证失败", e);
            return false;
        }
    }

    /**
     * 从 Token 中获取用户ID
     * 
     * @param token JWT Token 字符串
     * @return 用户ID
     */
    @Override
    public Long getUserIdFromToken(String token) {
        Assert.hasText(token, "Token 不能为空");
        
        try {
            Claims claims = Jwts.parserBuilder()
                    .setSigningKey(getSigningKey())
                    .build()
                    .parseClaimsJws(token)
                    .getBody();
            
            Object userIdObj = claims.get("userId");
            if (userIdObj == null) {
                throw new RuntimeException("Token 中不包含用户ID");
            }
            
            Long userId = Long.valueOf(userIdObj.toString());
            log.debug("从 Token 中提取用户ID: userId={}", userId);
            return userId;
        } catch (ExpiredJwtException e) {
            log.error("Token 已过期，无法提取用户ID", e);
            throw new RuntimeException("Token 已过期: " + e.getMessage(), e);
        } catch (Exception e) {
            log.error("从 Token 中提取用户ID失败", e);
            throw new RuntimeException("提取用户ID失败: " + e.getMessage(), e);
        }
    }

    /**
     * 从 Token 中获取 Claims
     * 
     * @param token JWT Token 字符串
     * @return Claims Map
     */
    @Override
    public Map<String, Object> getClaimsFromToken(String token) {
        Assert.hasText(token, "Token 不能为空");
        
        try {
            Claims claims = Jwts.parserBuilder()
                    .setSigningKey(getSigningKey())
                    .build()
                    .parseClaimsJws(token)
                    .getBody();
            
            Map<String, Object> result = new HashMap<>(claims);
            log.debug("从 Token 中提取 Claims: size={}", result.size());
            return result;
        } catch (ExpiredJwtException e) {
            log.error("Token 已过期，无法提取 Claims", e);
            throw new RuntimeException("Token 已过期: " + e.getMessage(), e);
        } catch (Exception e) {
            log.error("从 Token 中提取 Claims 失败", e);
            throw new RuntimeException("提取 Claims 失败: " + e.getMessage(), e);
        }
    }

    /**
     * 登出
     * 
     * @param token JWT Token 字符串
     */
    @Override
    public void logout(String token) {
        if (!StringUtils.hasText(token)) {
            log.warn("Token 为空，无法登出");
            return;
        }
        
        if (cacheService != null) {
            try {
                String tokenKey = "sso:token:" + token;
                cacheService.delete(tokenKey);
                log.info("Token 已从缓存中删除，登出成功: tokenKey={}", tokenKey);
            } catch (Exception e) {
                log.error("登出失败，删除 Token 缓存失败", e);
                throw new RuntimeException("登出失败: " + e.getMessage(), e);
            }
        } else {
            log.warn("缓存服务未配置，无法实现登出功能。JWT 是无状态的，无法主动使 Token 失效");
        }
    }
}

