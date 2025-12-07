package com.example.ratelimit.aspect;

import com.example.common.exception.BusinessException;
import com.example.ratelimit.RateLimitService;
import com.example.ratelimit.annotation.RateLimit;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.expression.EvaluationContext;
import org.springframework.expression.Expression;
import org.springframework.expression.ExpressionParser;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.expression.spel.support.StandardEvaluationContext;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;

/**
 * 限流切面
 * <p>
 * 基于 AOP 实现方法级别的限流控制
 * 拦截带有 @RateLimit 注解的方法，进行限流检查
 * </p>
 * <p>
 * 功能说明：
 * <ul>
 *   <li>自动解析限流 key（支持 SpEL 表达式）</li>
 *   <li>在 Web 环境中自动使用 URI + IP 作为 key</li>
 *   <li>在非 Web 环境中使用方法签名作为 key</li>
 *   <li>限流失败时抛出 BusinessException（状态码 429）</li>
 * </ul>
 * </p>
 * 
 * @author system
 * @version 1.0.0
 * @since 1.0.0
 */
@Slf4j
@Aspect
@Component
@RequiredArgsConstructor
public class RateLimitAspect {

    /** 限流服务 */
    private final RateLimitService rateLimitService;
    
    /** SpEL 表达式解析器 */
    private final ExpressionParser parser = new SpelExpressionParser();

    /**
     * 环绕通知：限流检查
     * <p>
     * 在方法执行前进行限流检查，如果超过限制则抛出异常
     * </p>
     * 
     * @param joinPoint 连接点
     * @param rateLimit 限流注解
     * @return 方法执行结果
     * @throws Throwable 如果限流失败或方法执行异常
     */
    @Around("@annotation(rateLimit)")
    public Object around(ProceedingJoinPoint joinPoint, RateLimit rateLimit) throws Throwable {
        // 解析限流 key
        String key = resolveKey(joinPoint, rateLimit);
        
        // 进行限流检查
        boolean allowed = rateLimitService.slidingWindowLimit(
            key, 
            rateLimit.limit(), 
            rateLimit.window()
        );
        
        if (!allowed) {
            log.warn("请求被限流: key={}, limit={}, window={}", 
                    key, rateLimit.limit(), rateLimit.window());
            throw new BusinessException(429, "请求过于频繁，请稍后再试");
        }
        
        log.debug("限流检查通过: key={}, limit={}, window={}", 
                key, rateLimit.limit(), rateLimit.window());
        
        // 执行原方法
        return joinPoint.proceed();
    }

    /**
     * 解析限流 key
     * <p>
     * 优先级：
     * <ol>
     *   <li>如果注解中指定了 key，且包含 SpEL 表达式，则解析表达式</li>
     *   <li>如果注解中指定了 key，但不包含表达式，则直接使用</li>
     *   <li>如果 key 为空，在 Web 环境中使用 URI + IP</li>
     *   <li>如果 key 为空，在非 Web 环境中使用方法签名</li>
     * </ol>
     * </p>
     * 
     * @param joinPoint 连接点
     * @param rateLimit 限流注解
     * @return 限流 key
     */
    private String resolveKey(ProceedingJoinPoint joinPoint, RateLimit rateLimit) {
        String key = rateLimit.key();
        
        // 如果 key 不为空，尝试解析 SpEL 表达式
        if (key != null && !key.isEmpty()) {
            // 检查是否包含 SpEL 表达式（简单判断，包含 # 或 $）
            if (key.contains("#") || key.contains("$")) {
                try {
                    // 解析 SpEL 表达式
                    Expression expression = parser.parseExpression(key);
                    EvaluationContext context = new StandardEvaluationContext();
                    
                    // 设置方法参数到上下文
                    Object[] args = joinPoint.getArgs();
                    String[] paramNames = getParameterNames(joinPoint);
                    if (paramNames != null) {
                        for (int i = 0; i < paramNames.length && i < args.length; i++) {
                            context.setVariable(paramNames[i], args[i]);
                        }
                    }
                    
                    // 设置方法参数数组
                    context.setVariable("args", args);
                    
                    // 执行表达式
                    Object value = expression.getValue(context);
                    if (value != null) {
                        key = value.toString();
                    }
                } catch (Exception e) {
                    log.warn("SpEL 表达式解析失败，使用原始 key: key={}", key, e);
                }
            }
            return key;
        }
        
        // 如果 key 为空，自动生成
        ServletRequestAttributes attributes = (ServletRequestAttributes) 
            RequestContextHolder.getRequestAttributes();
        
        if (attributes != null) {
            // Web 环境：使用 URI + IP
            HttpServletRequest request = attributes.getRequest();
            key = request.getRequestURI() + ":" + getClientIp(request);
        } else {
            // 非 Web 环境：使用方法签名
            key = joinPoint.getSignature().toLongString();
        }
        
        return key;
    }

    /**
     * 获取方法参数名
     * <p>
     * 注意：此方法需要编译时保留参数名信息（-parameters 编译选项）
     * </p>
     * 
     * @param joinPoint 连接点
     * @return 参数名数组，如果无法获取则返回 null
     */
    private String[] getParameterNames(ProceedingJoinPoint joinPoint) {
        // 注意：AspectJ 无法直接获取参数名，需要配合编译选项 -parameters
        // 这里返回 null，SpEL 表达式可以使用 args[0]、args[1] 等方式访问参数
        return null;
    }

    /**
     * 获取客户端 IP 地址
     * <p>
     * 支持从以下请求头获取 IP：
     * <ul>
     *   <li>X-Forwarded-For：代理服务器转发的原始 IP</li>
     *   <li>X-Real-IP：Nginx 等代理服务器设置的原始 IP</li>
     *   <li>RemoteAddr：直接连接的客户端 IP</li>
     * </ul>
     * </p>
     * 
     * @param request HTTP 请求对象
     * @return 客户端 IP 地址
     */
    private String getClientIp(HttpServletRequest request) {
        String ip = request.getHeader("X-Forwarded-For");
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("X-Real-IP");
        }
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getRemoteAddr();
        }
        
        // 处理 X-Forwarded-For 可能包含多个 IP 的情况
        if (ip != null && ip.contains(",")) {
            ip = ip.split(",")[0].trim();
        }
        
        return ip;
    }
}

