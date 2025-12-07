package com.example.lock.aspect;

import com.example.lock.DistributedLock;
import com.example.lock.annotation.DistributedLockAnnotation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.expression.EvaluationContext;
import org.springframework.expression.Expression;
import org.springframework.expression.ExpressionParser;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.expression.spel.support.StandardEvaluationContext;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;

/**
 * 分布式锁切面
 * <p>
 * 拦截带有 {@link DistributedLockAnnotation} 注解的方法，自动为其添加分布式锁
 * </p>
 * <p>
 * 功能：
 * <ul>
 *   <li>解析注解中的 key（支持 SpEL 表达式）</li>
 *   <li>在方法执行前获取锁</li>
 *   <li>在方法执行后释放锁</li>
 *   <li>如果获取锁失败，抛出异常</li>
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
public class DistributedLockAspect {

    /** 分布式锁服务 */
    private final DistributedLock distributedLock;
    
    /** SpEL 表达式解析器 */
    private final ExpressionParser parser = new SpelExpressionParser();

    /**
     * 环绕通知：在方法执行前后添加分布式锁
     * 
     * @param joinPoint 连接点
     * @param distributedLockAnnotation 分布式锁注解
     * @return 方法返回值
     * @throws Throwable 方法执行异常或获取锁失败
     */
    @Around("@annotation(distributedLockAnnotation)")
    public Object around(ProceedingJoinPoint joinPoint, DistributedLockAnnotation distributedLockAnnotation) throws Throwable {
        // 解析锁的 key（支持 SpEL 表达式）
        String key = parseKey(distributedLockAnnotation.key(), joinPoint);
        long waitTime = distributedLockAnnotation.waitTime();
        long leaseTime = distributedLockAnnotation.leaseTime();

        log.debug("执行分布式锁切面: method={}, key={}, waitTime={}s, leaseTime={}s", 
                joinPoint.getSignature().toShortString(), key, waitTime, leaseTime);

        // 执行带锁的业务逻辑
        return distributedLock.executeWithLock(key, waitTime, leaseTime, TimeUnit.SECONDS, () -> {
            try {
                return joinPoint.proceed();
            } catch (Throwable e) {
                log.error("方法执行异常: method={}, key={}", joinPoint.getSignature().toShortString(), key, e);
                throw new RuntimeException(e);
            }
        });
    }

    /**
     * 解析锁的 key
     * <p>
     * 如果 key 包含 "#"，则使用 SpEL 表达式解析，从方法参数中获取值
     * 例如：key = "lock:user:#{#id}" 会从方法参数 id 中获取值
     * </p>
     * 
     * @param key 锁的 key（可能包含 SpEL 表达式）
     * @param joinPoint 连接点
     * @return 解析后的 key
     */
    private String parseKey(String key, ProceedingJoinPoint joinPoint) {
        // 如果 key 包含 "#"，说明使用了 SpEL 表达式
        if (key.contains("#")) {
            MethodSignature signature = (MethodSignature) joinPoint.getSignature();
            String[] paramNames = signature.getParameterNames();
            Object[] args = joinPoint.getArgs();

            // 创建 SpEL 上下文，将方法参数添加到上下文中
            EvaluationContext context = new StandardEvaluationContext();
            for (int i = 0; i < paramNames.length; i++) {
                context.setVariable(paramNames[i], args[i]);
            }

            // 解析 SpEL 表达式
            Expression expression = parser.parseExpression(key);
            String parsedKey = expression.getValue(context, String.class);
            log.debug("解析 SpEL 表达式: original={}, parsed={}", key, parsedKey);
            return parsedKey;
        }
        
        // 如果 key 不包含 "#"，直接返回
        return key;
    }
}

