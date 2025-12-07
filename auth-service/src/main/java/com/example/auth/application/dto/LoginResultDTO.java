package com.example.auth.application.dto;

import lombok.Data;

/**
 * 登录结果数据传输对象
 * <p>
 * 包含登录成功后返回的信息，客户端需要使用 Token 进行后续的认证请求
 * </p>
 * 
 * @author system
 * @version 1.0.0
 * @since 1.0.0
 */
@Data
public class LoginResultDTO {
    
    /**
     * JWT Token
     * <p>
     * 用于后续 API 请求的认证，需要在请求头中携带：
     * <pre>Authorization: Bearer {token}</pre>
     * </p>
     */
    private String token;
    
    /**
     * 用户ID
     * <p>系统分配的唯一用户标识</p>
     */
    private Long userId;
    
    /**
     * 用户名
     * <p>用户的登录用户名，可能为空（如手机号登录时）</p>
     */
    private String username;
}

