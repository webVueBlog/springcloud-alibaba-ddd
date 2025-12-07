package com.example.sso;

import java.util.Map;

/**
 * 单点登录（SSO）服务接口
 * <p>
 * 提供基于 JWT（JSON Web Token）的单点登录功能，包括：
 * <ul>
 *   <li>Token 生成：根据用户ID和自定义声明生成 JWT Token</li>
 *   <li>Token 验证：验证 Token 的有效性和完整性</li>
 *   <li>用户信息提取：从 Token 中提取用户ID和自定义声明</li>
 *   <li>登出功能：使 Token 失效</li>
 * </ul>
 * </p>
 * <p>
 * 使用场景：
 * <ul>
 *   <li>微服务架构中的统一认证</li>
 *   <li>前后端分离架构中的身份验证</li>
 *   <li>跨域单点登录</li>
 *   <li>API 接口鉴权</li>
 * </ul>
 * </p>
 * <p>
 * 使用示例：
 * <pre>
 * // 注入 SSO 服务
 * @Autowired
 * private SSOService ssoService;
 * 
 * // 生成 Token
 * Map&lt;String, Object&gt; claims = new HashMap&lt;&gt;();
 * claims.put("username", "admin");
 * claims.put("role", "ADMIN");
 * String token = ssoService.generateToken(userId, claims);
 * 
 * // 验证 Token
 * boolean valid = ssoService.validateToken(token);
 * 
 * // 获取用户ID
 * Long userId = ssoService.getUserIdFromToken(token);
 * 
 * // 获取自定义声明
 * Map&lt;String, Object&gt; claims = ssoService.getClaimsFromToken(token);
 * 
 * // 登出
 * ssoService.logout(token);
 * </pre>
 * </p>
 * 
 * @author system
 * @version 1.0.0
 * @since 1.0.0
 */
public interface SSOService {
    
    /**
     * 生成 SSO Token
     * <p>
     * 根据用户ID和自定义声明生成 JWT Token
     * Token 包含用户ID、自定义声明、签发时间、过期时间等信息
     * </p>
     * <p>
     * Token 格式：
     * <ul>
     *   <li>Header：算法类型（HS512）</li>
     *   <li>Payload：用户ID、自定义声明、签发时间、过期时间</li>
     *   <li>Signature：使用密钥签名的签名</li>
     * </ul>
     * </p>
     * 
     * @param userId 用户ID，不能为 null
     * @param claims 自定义声明（Claims），可以为 null 或空 Map
     * @return JWT Token 字符串
     * @throws IllegalArgumentException 如果 userId 为 null
     * @throws RuntimeException 如果 Token 生成失败
     */
    String generateToken(Long userId, Map<String, Object> claims);

    /**
     * 验证 Token
     * <p>
     * 验证 Token 的有效性，包括：
     * <ul>
     *   <li>Token 格式是否正确</li>
     *   <li>签名是否有效</li>
     *   <li>是否已过期</li>
     *   <li>是否在缓存中存在（如果缓存服务可用）</li>
     * </ul>
     * </p>
     * 
     * @param token JWT Token 字符串，不能为 null 或空字符串
     * @return true 表示 Token 有效，false 表示 Token 无效
     */
    boolean validateToken(String token);

    /**
     * 从 Token 中获取用户ID
     * <p>
     * 解析 Token 并提取用户ID
     * </p>
     * 
     * @param token JWT Token 字符串，不能为 null 或空字符串
     * @return 用户ID
     * @throws RuntimeException 如果 Token 无效或解析失败
     */
    Long getUserIdFromToken(String token);

    /**
     * 从 Token 中获取 Claims
     * <p>
     * 解析 Token 并提取所有声明（Claims），包括用户ID和自定义声明
     * </p>
     * 
     * @param token JWT Token 字符串，不能为 null 或空字符串
     * @return Claims Map，包含所有声明信息
     * @throws RuntimeException 如果 Token 无效或解析失败
     */
    Map<String, Object> getClaimsFromToken(String token);

    /**
     * 登出
     * <p>
     * 使 Token 失效，从缓存中删除 Token（如果缓存服务可用）
     * 注意：JWT 本身是无状态的，登出功能依赖于缓存服务
     * </p>
     * 
     * @param token JWT Token 字符串，不能为 null 或空字符串
     */
    void logout(String token);
}

