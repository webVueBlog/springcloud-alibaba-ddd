package com.example.trace;

/**
 * 分布式追踪服务接口
 * <p>
 * 提供分布式追踪功能，用于跨服务链路追踪和日志关联
 * </p>
 * <p>
 * 功能说明：
 * <ul>
 *   <li>TraceId：追踪ID，用于标识一次完整的请求链路</li>
 *   <li>SpanId：Span ID，用于标识请求链路中的一个操作</li>
 *   <li>Span：表示一个操作单元，可以嵌套形成树形结构</li>
 *   <li>Tag：标签，用于为 Span 添加额外的元数据</li>
 * </ul>
 * </p>
 * <p>
 * 使用场景：
 * <ul>
 *   <li>微服务架构中的请求链路追踪</li>
 *   <li>日志关联和问题排查</li>
 *   <li>性能监控和分析</li>
 *   <li>分布式系统的可观测性</li>
 * </ul>
 * </p>
 * <p>
 * 使用示例：
 * <pre>
 * // 注入追踪服务
 * @Autowired
 * private TraceService traceService;
 * 
 * // 获取当前追踪ID
 * String traceId = traceService.getTraceId();
 * 
 * // 创建新的 Span
 * TraceContext span = traceService.createSpan("user-service.getUser");
 * try {
 *     // 执行业务逻辑
 *     // ...
 * } finally {
 *     traceService.finishSpan();
 * }
 * 
 * // 添加标签
 * traceService.addTag("userId", "12345");
 * traceService.addTag("operation", "getUser");
 * </pre>
 * </p>
 * 
 * @author system
 * @version 1.0.0
 * @since 1.0.0
 */
public interface TraceService {
    
    /**
     * 获取当前追踪ID
     * <p>
     * TraceId 用于标识一次完整的请求链路，在整个链路中保持不变
     * </p>
     * 
     * @return 追踪ID，如果不存在返回 null
     */
    String getTraceId();
    
    /**
     * 获取当前Span ID
     * <p>
     * SpanId 用于标识请求链路中的一个操作，每个 Span 都有唯一的 SpanId
     * </p>
     * 
     * @return Span ID，如果不存在返回 null
     */
    String getSpanId();
    
    /**
     * 创建新的Span
     * <p>
     * 创建一个新的 Span，用于追踪一个操作单元
     * Span 可以嵌套，形成树形结构
     * </p>
     * 
     * @param name Span 名称，用于描述操作，如 "user-service.getUser"
     * @return Span 上下文，包含 TraceId、SpanId 等信息
     */
    TraceContext createSpan(String name);
    
    /**
     * 结束当前Span
     * <p>
     * 结束当前 Span，标记操作完成
     * 应该在 try-finally 块中调用，确保 Span 能够正确结束
     * </p>
     */
    void finishSpan();
    
    /**
     * 添加追踪标签
     * <p>
     * 为当前 Span 添加标签，用于记录额外的元数据
     * 标签可以用于过滤和查询追踪数据
     * </p>
     * 
     * @param key 标签键，不能为 null 或空字符串
     * @param value 标签值，不能为 null
     */
    void addTag(String key, String value);
}

