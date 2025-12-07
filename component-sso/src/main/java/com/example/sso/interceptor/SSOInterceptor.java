package com.example.sso.interceptor;

import com.example.sso.SSOService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * SSO 拦截器
 * <p>
 * 用于拦截 HTTP 请求，验证 Token 并提取用户信息
 * </p>
 * <p>
 * 使用方式：
 * <pre>
 * @Configuration
 * public class WebMvcConfig implements WebMvcConfigurer {
 *     @Autowired
 *     private SSOInterceptor ssoInterceptor;
 *     
 *     @Override
 *     public void addInterceptors(InterceptorRegistry registry) {
 *         registry.addInterceptor(ssoInterceptor)
 *                 .addPathPatterns("/api/**")
 *                 .excludePathPatterns("/api/login", "/api/register");
 *     }
 * }
 * </pre>
 * </p>
 * <p>
 * Token 获取方式：
 * <ul>
 *   <li>从请求头获取：Authorization: Bearer {token}</li>
 *   <li>从请求参数获取：?token={token}</li>
 * </ul>
 * </p>
 * 
 * @author system
 * @version 1.0.0
 * @since 1.0.0
 */
@Slf4j
@Component
public class SSOInterceptor implements HandlerInterceptor {

    /** SSO 服务 */
    @Autowired
    private SSOService ssoService;

    /** Token 请求头名称 */
    private static final String TOKEN_HEADER = "Authorization";
    
    /** Token 前缀 */
    private static final String TOKEN_PREFIX = "Bearer ";
    
    /** Token 参数名称 */
    private static final String TOKEN_PARAM = "token";

    /**
     * 请求处理前拦截
     * 
     * @param request HTTP 请求
     * @param response HTTP 响应
     * @param handler 处理器
     * @return true 表示继续处理，false 表示中断处理
     */
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        try {
            // 获取 Token
            String token = getTokenFromRequest(request);
            
            if (!StringUtils.hasText(token)) {
                log.warn("请求中未找到 Token: uri={}", request.getRequestURI());
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                response.setContentType("application/json;charset=UTF-8");
                response.getWriter().write("{\"code\":401,\"message\":\"未授权，请先登录\"}");
                return false;
            }

            // 验证 Token
            if (!ssoService.validateToken(token)) {
                log.warn("Token 验证失败: uri={}", request.getRequestURI());
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                response.setContentType("application/json;charset=UTF-8");
                response.getWriter().write("{\"code\":401,\"message\":\"Token 无效或已过期\"}");
                return false;
            }

            // 提取用户ID并设置到请求属性中
            Long userId = ssoService.getUserIdFromToken(token);
            request.setAttribute("userId", userId);
            request.setAttribute("token", token);
            
            log.debug("Token 验证成功: userId={}, uri={}", userId, request.getRequestURI());
            return true;
        } catch (Exception e) {
            log.error("SSO 拦截器处理失败: uri={}", request.getRequestURI(), e);
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            response.setContentType("application/json;charset=UTF-8");
            try {
                response.getWriter().write("{\"code\":500,\"message\":\"服务器内部错误\"}");
            } catch (Exception ex) {
                log.error("写入响应失败", ex);
            }
            return false;
        }
    }

    /**
     * 从请求中获取 Token
     * <p>
     * 优先从请求头获取（Authorization: Bearer {token}），
     * 如果请求头中没有，则从请求参数获取（?token={token}）
     * </p>
     * 
     * @param request HTTP 请求
     * @return Token 字符串，如果未找到返回 null
     */
    private String getTokenFromRequest(HttpServletRequest request) {
        // 从请求头获取
        String authHeader = request.getHeader(TOKEN_HEADER);
        if (StringUtils.hasText(authHeader) && authHeader.startsWith(TOKEN_PREFIX)) {
            return authHeader.substring(TOKEN_PREFIX.length());
        }
        
        // 从请求参数获取
        String tokenParam = request.getParameter(TOKEN_PARAM);
        if (StringUtils.hasText(tokenParam)) {
            return tokenParam;
        }
        
        return null;
    }
}

