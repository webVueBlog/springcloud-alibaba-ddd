package com.example.trace;

import lombok.Data;

/**
 * 追踪上下文
 * <p>
 * 用于存储追踪相关的上下文信息，包括 TraceId、SpanId、父 SpanId 和 Span 名称
 * </p>
 * <p>
 * 使用场景：
 * <ul>
 *   <li>在服务间传递追踪信息</li>
 *   <li>记录 Span 的创建信息</li>
 *   <li>用于日志关联和问题排查</li>
 * </ul>
 * </p>
 * 
 * @author system
 * @version 1.0.0
 * @since 1.0.0
 */
@Data
public class TraceContext {
    
    /**
     * 追踪ID（TraceId）
     * <p>
     * 用于标识一次完整的请求链路，在整个链路中保持不变
     * </p>
     */
    private String traceId;
    
    /**
     * 当前Span ID
     * <p>
     * 用于标识当前操作单元，每个 Span 都有唯一的 SpanId
     * </p>
     */
    private String spanId;
    
    /**
     * 父Span ID
     * <p>
     * 用于标识父级 Span，形成树形结构
     * 如果为 null，表示这是根 Span
     * </p>
     */
    private String parentSpanId;
    
    /**
     * Span 名称
     * <p>
     * 用于描述操作，如 "user-service.getUser"、"order-service.createOrder"
     * </p>
     */
    private String name;
}

