package com.example.audit.infrastructure.aspect;

import com.example.audit.application.service.AuditLogService;
import com.example.audit.domain.model.AuditLog;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;
import java.util.Arrays;

/**
 * 审计日志切面
 * <p>
 * 自动记录Controller层的操作日志
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
public class AuditAspect {
    
    private final AuditLogService auditLogService;
    private final ObjectMapper objectMapper = new ObjectMapper();
    
    /**
     * 环绕通知，记录所有Controller方法的操作日志
     */
    @Around("execution(* com.example..*Controller.*(..))")
    public Object around(ProceedingJoinPoint joinPoint) throws Throwable {
        long startTime = System.currentTimeMillis();
        String operationStatus = "SUCCESS";
        String errorMessage = null;
        Object result = null;
        
        try {
            result = joinPoint.proceed();
            return result;
        } catch (Exception e) {
            operationStatus = "FAILED";
            errorMessage = e.getMessage();
            throw e;
        } finally {
            long executionTime = System.currentTimeMillis() - startTime;
            try {
                recordAuditLog(joinPoint, operationStatus, errorMessage, executionTime);
            } catch (Exception e) {
                log.error("记录审计日志失败", e);
                // 审计日志记录失败不应该影响主业务流程
            }
        }
    }
    
    /**
     * 记录审计日志
     */
    private void recordAuditLog(ProceedingJoinPoint joinPoint, String operationStatus, 
                                String errorMessage, long executionTime) {
        try {
            ServletRequestAttributes attributes = 
                (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
            if (attributes == null) {
                return;
            }
            
            HttpServletRequest request = attributes.getRequest();
            String method = request.getMethod();
            String url = request.getRequestURI();
            String ipAddress = getIpAddress(request);
            String userAgent = request.getHeader("User-Agent");
            
            // 获取方法信息
            String className = joinPoint.getTarget().getClass().getSimpleName();
            String methodName = joinPoint.getSignature().getName();
            Object[] args = joinPoint.getArgs();
            
            // 构建审计日志
            AuditLog auditLog = new AuditLog();
            auditLog.setOperationType(getOperationType(method));
            auditLog.setModule(getModule(className));
            auditLog.setRequestMethod(method);
            auditLog.setRequestUrl(url);
            auditLog.setIpAddress(ipAddress);
            auditLog.setUserAgent(userAgent);
            auditLog.setOperationStatus(operationStatus);
            auditLog.setErrorMessage(errorMessage);
            auditLog.setExecutionTime((int) executionTime);
            
            // 记录请求参数（简化处理，只记录前3个参数）
            try {
                if (args != null && args.length > 0) {
                    Object[] params = Arrays.copyOf(args, Math.min(3, args.length));
                    auditLog.setRequestParams(objectMapper.writeValueAsString(params));
                }
            } catch (Exception e) {
                log.warn("序列化请求参数失败", e);
            }
            
            auditLogService.saveAuditLog(auditLog);
        } catch (Exception e) {
            log.error("构建审计日志失败", e);
        }
    }
    
    /**
     * 获取操作类型
     */
    private String getOperationType(String method) {
        switch (method.toUpperCase()) {
            case "GET":
                return "VIEW";
            case "POST":
                return "CREATE";
            case "PUT":
                return "UPDATE";
            case "DELETE":
                return "DELETE";
            default:
                return "OTHER";
        }
    }
    
    /**
     * 获取模块名称
     */
    private String getModule(String className) {
        if (className.contains("User")) {
            return "USER";
        } else if (className.contains("Role")) {
            return "ROLE";
        } else if (className.contains("Permission")) {
            return "PERMISSION";
        } else if (className.contains("Menu")) {
            return "MENU";
        } else if (className.contains("Article")) {
            return "ARTICLE";
        } else if (className.contains("Topic")) {
            return "TOPIC";
        } else if (className.contains("Comment")) {
            return "COMMENT";
        } else {
            return "OTHER";
        }
    }
    
    /**
     * 获取IP地址
     */
    private String getIpAddress(HttpServletRequest request) {
        String ip = request.getHeader("X-Forwarded-For");
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("Proxy-Client-IP");
        }
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("WL-Proxy-Client-IP");
        }
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getRemoteAddr();
        }
        return ip;
    }
}

