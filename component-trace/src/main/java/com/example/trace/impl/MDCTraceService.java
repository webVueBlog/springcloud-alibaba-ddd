package com.example.trace.impl;

import com.example.trace.TraceContext;
import com.example.trace.TraceService;
import com.example.trace.util.TraceUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.Stack;

/**
 * 基于 MDC 的追踪服务实现
 * <p>
 * 使用 SLF4J 的 MDC（Mapped Diagnostic Context）实现分布式追踪
 * 不依赖外部追踪框架，适合简单的追踪需求
 * </p>
 * <p>
 * 特性：
 * <ul>
 *   <li>基于 MDC，自动在日志中包含追踪信息</li>
 *   <li>支持 Span 嵌套，形成树形结构</li>
 *   <li>线程安全，使用 ThreadLocal 存储 Span 栈</li>
 *   <li>轻量级，无需额外依赖</li>
 * </ul>
 * </p>
 * <p>
 * 注意：
 * <ul>
 *   <li>此实现是简化版本，适合简单的追踪需求</li>
 *   <li>如果需要完整的分布式追踪功能，建议使用 Sleuth 或 Zipkin</li>
 *   <li>Span 栈使用 ThreadLocal，确保在请求处理完成后清理</li>
 * </ul>
 * </p>
 * 
 * @author system
 * @version 1.0.0
 * @since 1.0.0
 */
@Slf4j
@Service
public class MDCTraceService implements TraceService {

    /** Span 栈，用于管理嵌套的 Span */
    private static final ThreadLocal<Stack<TraceContext>> SPAN_STACK = ThreadLocal.withInitial(Stack::new);

    /**
     * 获取当前追踪ID
     * 
     * @return 追踪ID
     */
    @Override
    public String getTraceId() {
        String traceId = TraceUtil.getTraceId();
        if (!StringUtils.hasText(traceId)) {
            // 如果不存在，生成一个新的 TraceId
            traceId = TraceUtil.generateTraceId();
            TraceUtil.setTraceId(traceId);
            log.debug("自动生成 TraceId: {}", traceId);
        }
        return traceId;
    }

    /**
     * 获取当前Span ID
     * 
     * @return Span ID
     */
    @Override
    public String getSpanId() {
        String spanId = TraceUtil.getSpanId();
        if (!StringUtils.hasText(spanId)) {
            // 如果不存在，生成一个新的 SpanId
            spanId = TraceUtil.generateSpanId();
            TraceUtil.setSpanId(spanId);
            log.debug("自动生成 SpanId: {}", spanId);
        }
        return spanId;
    }

    /**
     * 创建新的Span
     * 
     * @param name Span 名称
     * @return Span 上下文
     */
    @Override
    public TraceContext createSpan(String name) {
        if (!StringUtils.hasText(name)) {
            throw new IllegalArgumentException("Span 名称不能为空");
        }

        try {
            // 获取或生成 TraceId
            String traceId = getTraceId();
            
            // 获取父 SpanId（如果存在）
            String parentSpanId = TraceUtil.getSpanId();
            
            // 生成新的 SpanId
            String spanId = TraceUtil.generateSpanId();
            
            // 创建 Span 上下文
            TraceContext context = new TraceContext();
            context.setTraceId(traceId);
            context.setSpanId(spanId);
            context.setParentSpanId(parentSpanId);
            context.setName(name);
            
            // 将新的 SpanId 设置到 MDC
            TraceUtil.setSpanId(spanId);
            TraceUtil.setParentSpanId(parentSpanId);
            
            // 将 Span 压入栈
            Stack<TraceContext> stack = SPAN_STACK.get();
            stack.push(context);
            
            log.debug("创建新的 Span: name={}, traceId={}, spanId={}, parentSpanId={}", 
                    name, traceId, spanId, parentSpanId);
            
            return context;
        } catch (Exception e) {
            log.error("创建 Span 失败: name={}", name, e);
            throw new RuntimeException("创建 Span 失败: " + e.getMessage(), e);
        }
    }

    /**
     * 结束当前Span
     */
    @Override
    public void finishSpan() {
        try {
            Stack<TraceContext> stack = SPAN_STACK.get();
            if (stack.isEmpty()) {
                log.warn("Span 栈为空，无法结束 Span");
                return;
            }
            
            // 弹出当前 Span
            TraceContext finishedSpan = stack.pop();
            log.debug("结束 Span: name={}, spanId={}", finishedSpan.getName(), finishedSpan.getSpanId());
            
            // 如果栈中还有 Span，恢复父 Span
            if (!stack.isEmpty()) {
                TraceContext parentSpan = stack.peek();
                TraceUtil.setSpanId(parentSpan.getSpanId());
                TraceUtil.setParentSpanId(parentSpan.getParentSpanId());
                log.debug("恢复父 Span: spanId={}", parentSpan.getSpanId());
            } else {
                // 如果栈为空，清除 SpanId
                TraceUtil.setSpanId(null);
                TraceUtil.setParentSpanId(null);
                log.debug("清除 SpanId（栈为空）");
            }
        } catch (Exception e) {
            log.error("结束 Span 失败", e);
        }
    }

    /**
     * 添加追踪标签
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
        
        try {
            // 将标签添加到 MDC（使用 trace.tag.{key} 格式）
            String tagKey = "trace.tag." + key;
            org.slf4j.MDC.put(tagKey, value);
            log.debug("添加追踪标签: {} = {}", key, value);
        } catch (Exception e) {
            log.error("添加追踪标签失败: key={}, value={}", key, value, e);
        }
    }
    
    /**
     * 清理当前线程的追踪信息
     * <p>
     * 应该在请求处理完成后调用，避免追踪信息泄露到其他请求
     * </p>
     */
    public void clear() {
        try {
            SPAN_STACK.remove();
            TraceUtil.clearTrace();
            log.debug("清理追踪信息");
        } catch (Exception e) {
            log.error("清理追踪信息失败", e);
        }
    }
}

