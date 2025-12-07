package com.example.common.constant;

/**
 * 系统常量类
 * <p>
 * 定义系统中使用的常量，包括：
 * <ul>
 *   <li>Token 相关常量</li>
 *   <li>用户信息相关常量</li>
 * </ul>
 * </p>
 * 
 * @author system
 * @version 1.0.0
 * @since 1.0.0
 */
public class Constants {
    
    /** HTTP 请求头中的 Token 字段名 */
    public static final String TOKEN_HEADER = "Authorization";
    
    /** Token 前缀，格式：Bearer {token} */
    public static final String TOKEN_PREFIX = "Bearer ";
    
    /** JWT Token 中用户ID的键名 */
    public static final String USER_ID_KEY = "userId";
    
    /** JWT Token 中用户名的键名 */
    public static final String USERNAME_KEY = "username";
    
    /**
     * 私有构造函数，防止实例化
     */
    private Constants() {
        throw new UnsupportedOperationException("Constants class cannot be instantiated");
    }
}

