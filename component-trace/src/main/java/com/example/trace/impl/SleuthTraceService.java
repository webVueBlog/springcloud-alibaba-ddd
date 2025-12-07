package com.example.trace.impl;

import com.example.trace.TraceContext;
import com.example.trace.TraceService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

/**
 * Sleuth 追踪服务实现
 * <p>
 * 基于 Spring Cloud Sleuth 实现分布式追踪
 * 需要添加 spring-cloud-starter-sleuth 依赖
 * </p>
 * <p>
 * 注意：
 * <ul>
 *   <li>此实现需要 Sleuth 依赖，如果没有 Sleuth，会使用 MDCTraceService</li>
 *   <li>Sleuth 会自动管理 TraceId 和 SpanId</li>
 *   <li>Sleuth 支持与 Zipkin 集成，实现完整的分布式追踪</li>
 * </ul>
 * </p>
 * <p>
 * 使用 Sleuth 时，需要在 pom.xml 中添加依赖：
 * <pre>
 * &lt;dependency&gt;
 *     &lt;groupId&gt;org.springframework.cloud&lt;/groupId&gt;
 *     &lt;artifactId&gt;spring-cloud-starter-sleuth&lt;/artifactId&gt;
 * &lt;/dependency&gt;
 * </pre>
 * </p>
 * 
 * @author system
 * @version 1.0.0
 * @since 1.0.0
 */
@Slf4j
@Service
@ConditionalOnClass(name = "org.springframework.cloud.sleuth.Tracer")
public class SleuthTraceService implements TraceService {

    /**
     * 获取当前追踪ID
     * <p>
     * 从 Sleuth 的 MDC 中获取 TraceId
     * Sleuth 会自动将 TraceId 设置到 MDC 中，键名为 "traceId"
     * </p>
     * 
     * @return 追踪ID
     */
    @Override
    public String getTraceId() {
        // Sleuth 会将 TraceId 设置到 MDC 中
        String traceId = org.slf4j.MDC.get("traceId");
        if (!StringUtils.hasText(traceId)) {
            log.warn("Sleuth TraceId 不存在，可能 Sleuth 未正确配置");
        }
        return traceId;
    }

    /**
     * 获取当前Span ID
     * <p>
     * 从 Sleuth 的 MDC 中获取 SpanId
     * Sleuth 会自动将 SpanId 设置到 MDC 中，键名为 "spanId"
     * </p>
     * 
     * @return Span ID
     */
    @Override
    public String getSpanId() {
        // Sleuth 会将 SpanId 设置到 MDC 中
        String spanId = org.slf4j.MDC.get("spanId");
        if (!StringUtils.hasText(spanId)) {
            log.warn("Sleuth SpanId 不存在，可能 Sleuth 未正确配置");
        }
        return spanId;
    }

    /**
     * 创建新的Span
     * <p>
     * 注意：Sleuth 会自动管理 Span 生命周期，此方法主要用于获取当前 Span 信息
     * 如果需要创建新的 Span，应该使用 Sleuth 的 Tracer API
     * </p>
     * 
     * @param name Span 名称
     * @return Span 上下文
     */
    @Override
    public TraceContext createSpan(String name) {
        if (!StringUtils.hasText(name)) {
            throw new IllegalArgumentException("Span 名称不能为空");
        }
        
        TraceContext context = new TraceContext();
        context.setName(name);
        context.setTraceId(getTraceId());
        context.setSpanId(getSpanId());
        log.debug("创建 Sleuth Span 上下文: name={}, traceId={}, spanId={}", 
                name, context.getTraceId(), context.getSpanId());
        return context;
    }

    /**
     * 结束当前Span
     * <p>
     * Sleuth 会自动管理 Span 生命周期，此方法主要用于兼容接口
     * </p>
     */
    @Override
    public void finishSpan() {
        // Sleuth 会自动管理 Span 生命周期
        log.debug("Sleuth 会自动管理 Span 生命周期");
    }

    /**
     * 添加追踪标签
     * <p>
     * 注意：此实现只是记录日志，实际应该使用 Sleuth 的 Tracer API 添加标签
     * </p>
     * 
     * @param key 标签键
     * @param value 标签值
     */
    @Override
    public void addTag(String key, String value) {
        if (!StringUtils.hasText(key)) {
            throw new IllegalArgumentException("标签键不能为空");
        }
        if (value == null) {
            throw new IllegalArgumentException("标签值不能为 null");
        }
        
        // 注意：实际应该使用 Sleuth 的 Tracer API 添加标签
        // 这里只是记录日志，实际使用时应该注入 Tracer 并调用相应方法
        log.debug("添加追踪标签（Sleuth）: {} = {}", key, value);
        log.warn("SleuthTraceService.addTag() 仅记录日志，实际应该使用 Sleuth 的 Tracer API");
    }
}

