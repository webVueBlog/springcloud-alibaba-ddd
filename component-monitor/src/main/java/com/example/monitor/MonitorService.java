package com.example.monitor;

import java.util.Map;

/**
 * 监控服务接口
 * <p>
 * 提供应用监控相关的功能，包括：
 * <ul>
 *   <li>获取应用健康状态</li>
 *   <li>获取应用指标信息</li>
 *   <li>获取系统信息</li>
 * </ul>
 * </p>
 * <p>
 * 使用示例：
 * <pre>
 * // 注入监控服务
 * @Autowired
 * private MonitorService monitorService;
 * 
 * // 获取健康状态
 * Map&lt;String, Object&gt; health = monitorService.getHealth();
 * 
 * // 获取应用指标
 * Map&lt;String, Object&gt; metrics = monitorService.getMetrics();
 * 
 * // 获取系统信息
 * Map&lt;String, Object&gt; info = monitorService.getSystemInfo();
 * </pre>
 * </p>
 * 
 * @author system
 * @version 1.0.0
 * @since 1.0.0
 */
public interface MonitorService {
    
    /**
     * 获取应用健康状态
     * <p>
     * 返回应用的健康检查信息，包括：
     * <ul>
     *   <li>status: 健康状态（UP、DOWN、UNKNOWN）</li>
     *   <li>components: 各个组件的健康状态（数据库、Redis、磁盘等）</li>
     * </ul>
     * </p>
     * 
     * @return 健康状态信息
     */
    Map<String, Object> getHealth();
    
    /**
     * 获取应用指标信息
     * <p>
     * 返回应用的各种指标，包括：
     * <ul>
     *   <li>jvm: JVM 相关信息（内存、线程、GC 等）</li>
     *   <li>http: HTTP 请求相关指标</li>
     *   <li>system: 系统资源使用情况</li>
     * </ul>
     * </p>
     * 
     * @return 指标信息
     */
    Map<String, Object> getMetrics();
    
    /**
     * 获取系统信息
     * <p>
     * 返回系统相关信息，包括：
     * <ul>
     *   <li>application: 应用信息（名称、版本等）</li>
     *   <li>java: Java 运行时信息</li>
     *   <li>os: 操作系统信息</li>
     * </ul>
     * </p>
     * 
     * @return 系统信息
     */
    Map<String, Object> getSystemInfo();
}

