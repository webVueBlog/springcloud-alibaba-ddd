package com.example.trace.util;

import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;

import java.util.UUID;

/**
 * 追踪工具类
 * <p>
 * 提供静态方法用于操作 MDC（Mapped Diagnostic Context）中的追踪信息
 * MDC 是 SLF4J 提供的功能，用于在日志中自动包含追踪信息
 * </p>
 * <p>
 * 使用场景：
 * <ul>
 *   <li>在日志配置中使用 %X{traceId} 和 %X{spanId} 输出追踪信息</li>
 *   <li>在服务间传递追踪信息</li>
 *   <li>手动设置和获取追踪信息</li>
 * </ul>
 * </p>
 * <p>
 * 日志配置示例（logback-spring.xml）：
 * <pre>
 * &lt;pattern&gt;
 *     %d{yyyy-MM-dd HH:mm:ss.SSS} [%thread] %-5level [%X{traceId},%X{spanId}] %logger{36} - %msg%n
 * &lt;/pattern&gt;
 * </pre>
 * </p>
 * 
 * @author system
 * @version 1.0.0
 * @since 1.0.0
 */
@Slf4j
public class TraceUtil {
    
    /** MDC 中 TraceId 的键名 */
    private static final String TRACE_ID_KEY = "traceId";
    
    /** MDC 中 SpanId 的键名 */
    private static final String SPAN_ID_KEY = "spanId";
    
    /** MDC 中父 SpanId 的键名 */
    private static final String PARENT_SPAN_ID_KEY = "parentSpanId";

    /**
     * 获取当前追踪ID
     * <p>
     * 从 MDC 中获取 TraceId
     * </p>
     * 
     * @return 追踪ID，如果不存在返回 null
     */
    public static String getTraceId() {
        return MDC.get(TRACE_ID_KEY);
    }
    
    /**
     * 设置追踪ID
     * <p>
     * 将 TraceId 设置到 MDC 中，后续的日志会自动包含此 TraceId
     * </p>
     * 
     * @param traceId 追踪ID，如果为 null 则从 MDC 中移除
     */
    public static void setTraceId(String traceId) {
        if (traceId != null) {
            MDC.put(TRACE_ID_KEY, traceId);
            log.debug("设置 TraceId: {}", traceId);
        } else {
            MDC.remove(TRACE_ID_KEY);
            log.debug("移除 TraceId");
        }
    }
    
    /**
     * 获取当前Span ID
     * <p>
     * 从 MDC 中获取 SpanId
     * </p>
     * 
     * @return Span ID，如果不存在返回 null
     */
    public static String getSpanId() {
        return MDC.get(SPAN_ID_KEY);
    }
    
    /**
     * 设置Span ID
     * <p>
     * 将 SpanId 设置到 MDC 中，后续的日志会自动包含此 SpanId
     * </p>
     * 
     * @param spanId Span ID，如果为 null 则从 MDC 中移除
     */
    public static void setSpanId(String spanId) {
        if (spanId != null) {
            MDC.put(SPAN_ID_KEY, spanId);
            log.debug("设置 SpanId: {}", spanId);
        } else {
            MDC.remove(SPAN_ID_KEY);
            log.debug("移除 SpanId");
        }
    }
    
    /**
     * 获取父Span ID
     * <p>
     * 从 MDC 中获取父 SpanId
     * </p>
     * 
     * @return 父Span ID，如果不存在返回 null
     */
    public static String getParentSpanId() {
        return MDC.get(PARENT_SPAN_ID_KEY);
    }
    
    /**
     * 设置父Span ID
     * <p>
     * 将父 SpanId 设置到 MDC 中
     * </p>
     * 
     * @param parentSpanId 父Span ID，如果为 null 则从 MDC 中移除
     */
    public static void setParentSpanId(String parentSpanId) {
        if (parentSpanId != null) {
            MDC.put(PARENT_SPAN_ID_KEY, parentSpanId);
            log.debug("设置父 SpanId: {}", parentSpanId);
        } else {
            MDC.remove(PARENT_SPAN_ID_KEY);
            log.debug("移除父 SpanId");
        }
    }
    
    /**
     * 生成新的追踪ID
     * <p>
     * 使用 UUID 生成一个唯一的追踪ID
     * </p>
     * 
     * @return 新的追踪ID
     */
    public static String generateTraceId() {
        String traceId = UUID.randomUUID().toString().replace("-", "");
        log.debug("生成新的 TraceId: {}", traceId);
        return traceId;
    }
    
    /**
     * 生成新的Span ID
     * <p>
     * 使用 UUID 生成一个唯一的 SpanId
     * </p>
     * 
     * @return 新的Span ID
     */
    public static String generateSpanId() {
        String spanId = UUID.randomUUID().toString().replace("-", "");
        log.debug("生成新的 SpanId: {}", spanId);
        return spanId;
    }
    
    /**
     * 清除追踪信息
     * <p>
     * 清除 MDC 中的所有追踪信息
     * 应该在请求处理完成后调用，避免追踪信息泄露到其他请求
     * </p>
     */
    public static void clear() {
        MDC.clear();
        log.debug("清除所有追踪信息");
    }
    
    /**
     * 清除追踪信息（保留其他 MDC 信息）
     * <p>
     * 只清除追踪相关的信息，保留其他 MDC 中的信息
     * </p>
     */
    public static void clearTrace() {
        MDC.remove(TRACE_ID_KEY);
        MDC.remove(SPAN_ID_KEY);
        MDC.remove(PARENT_SPAN_ID_KEY);
        log.debug("清除追踪信息");
    }
}

