package com.example.monitor.controller;

import com.example.common.result.Result;
import com.example.monitor.MonitorService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

/**
 * 监控控制器
 * <p>
 * 提供监控相关的 REST API 接口，包括：
 * <ul>
 *   <li>健康检查接口</li>
 *   <li>指标查询接口</li>
 *   <li>系统信息查询接口</li>
 * </ul>
 * </p>
 * <p>
 * 注意：这些接口应该配置访问权限，避免敏感信息泄露
 * </p>
 * 
 * @author system
 * @version 1.0.0
 * @since 1.0.0
 */
@Slf4j
@RestController
@RequestMapping("/api/monitor")
@RequiredArgsConstructor
public class MonitorController {

    /** 监控服务 */
    private final MonitorService monitorService;

    /**
     * 获取应用健康状态
     * <p>
     * 返回应用的健康检查信息，可用于负载均衡器的健康检查
     * </p>
     * 
     * @return 健康状态信息
     */
    @GetMapping("/health")
    public Result<Map<String, Object>> getHealth() {
        Map<String, Object> health = monitorService.getHealth();
        return Result.success(health);
    }

    /**
     * 获取应用指标信息
     * <p>
     * 返回应用的各种指标，包括 JVM、HTTP、系统指标等
     * </p>
     * 
     * @return 指标信息
     */
    @GetMapping("/metrics")
    public Result<Map<String, Object>> getMetrics() {
        Map<String, Object> metrics = monitorService.getMetrics();
        return Result.success(metrics);
    }

    /**
     * 获取系统信息
     * <p>
     * 返回系统相关信息，包括应用信息、Java 信息、操作系统信息等
     * </p>
     * 
     * @return 系统信息
     */
    @GetMapping("/info")
    public Result<Map<String, Object>> getSystemInfo() {
        Map<String, Object> info = monitorService.getSystemInfo();
        return Result.success(info);
    }
}

