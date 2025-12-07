package com.example.monitor.impl;

import com.example.monitor.MonitorService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.actuate.health.HealthComponent;
import org.springframework.boot.actuate.metrics.MetricsEndpoint;
import org.springframework.stereotype.Service;

import java.lang.management.ManagementFactory;
import java.lang.management.MemoryMXBean;
import java.lang.management.RuntimeMXBean;
import java.lang.management.ThreadMXBean;
import java.util.HashMap;
import java.util.Map;

/**
 * 监控服务实现
 * <p>
 * 基于 Spring Boot Actuator 实现应用监控功能
 * 提供健康检查、指标收集、系统信息等功能
 * </p>
 * <p>
 * 功能说明：
 * <ul>
 *   <li>健康检查：通过 Actuator 的 HealthIndicator 获取健康状态</li>
 *   <li>指标收集：通过 MetricsEndpoint 获取应用指标</li>
 *   <li>系统信息：通过 JMX 获取 JVM 和系统信息</li>
 * </ul>
 * </p>
 * 
 * @author system
 * @version 1.0.0
 * @since 1.0.0
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class MonitorServiceImpl implements MonitorService {

    /** 健康指示器，用于获取健康状态 */
    private final org.springframework.boot.actuate.health.HealthEndpoint healthEndpoint;
    
    /** 指标端点，用于获取应用指标 */
    private final MetricsEndpoint metricsEndpoint;

    /**
     * 获取应用健康状态
     * 
     * @return 健康状态信息
     */
    @Override
    public Map<String, Object> getHealth() {
        try {
            HealthComponent health = healthEndpoint.health();
            Map<String, Object> result = new HashMap<>();
            
            // 获取健康状态
            if (health instanceof org.springframework.boot.actuate.health.Health) {
                org.springframework.boot.actuate.health.Health healthObj = 
                    (org.springframework.boot.actuate.health.Health) health;
                result.put("status", healthObj.getStatus().getCode());
                result.put("components", healthObj.getDetails());
                log.debug("获取健康状态: status={}", healthObj.getStatus());
            } else {
                // 如果是 CompositeHealth，转换为 Map
                result.put("status", "UP");
                result.put("components", convertHealthComponent(health));
            }
            
            return result;
        } catch (Exception e) {
            log.error("获取健康状态失败", e);
            Map<String, Object> result = new HashMap<>();
            result.put("status", "UNKNOWN");
            result.put("error", e.getMessage());
            return result;
        }
    }
    
    /**
     * 转换 HealthComponent 为 Map
     * 
     * @param health 健康组件
     * @return Map 格式的健康信息
     */
    private Map<String, Object> convertHealthComponent(HealthComponent health) {
        Map<String, Object> result = new HashMap<>();
        if (health instanceof org.springframework.boot.actuate.health.Health) {
            org.springframework.boot.actuate.health.Health healthObj = 
                (org.springframework.boot.actuate.health.Health) health;
            result.put("status", healthObj.getStatus().getCode());
            result.putAll(healthObj.getDetails());
        } else if (health instanceof org.springframework.boot.actuate.health.CompositeHealth) {
            org.springframework.boot.actuate.health.CompositeHealth composite = 
                (org.springframework.boot.actuate.health.CompositeHealth) health;
            result.put("status", composite.getStatus().getCode());
            Map<String, Object> components = new HashMap<>();
            composite.getComponents().forEach((key, value) -> {
                components.put(key, convertHealthComponent(value));
            });
            result.put("components", components);
        }
        return result;
    }

    /**
     * 获取应用指标信息
     * 
     * @return 指标信息
     */
    @Override
    public Map<String, Object> getMetrics() {
        try {
            Map<String, Object> result = new HashMap<>();
            
            // JVM 指标
            Map<String, Object> jvm = getJvmMetrics();
            result.put("jvm", jvm);
            
            // HTTP 指标
            Map<String, Object> http = getHttpMetrics();
            result.put("http", http);
            
            // 系统指标
            Map<String, Object> system = getSystemMetrics();
            result.put("system", system);
            
            log.debug("获取应用指标成功");
            return result;
        } catch (Exception e) {
            log.error("获取应用指标失败", e);
            Map<String, Object> result = new HashMap<>();
            result.put("error", e.getMessage());
            return result;
        }
    }

    /**
     * 获取系统信息
     * 
     * @return 系统信息
     */
    @Override
    public Map<String, Object> getSystemInfo() {
        try {
            Map<String, Object> result = new HashMap<>();
            
            // 应用信息
            Map<String, Object> application = new HashMap<>();
            application.put("name", System.getProperty("spring.application.name", "unknown"));
            result.put("application", application);
            
            // Java 信息
            Map<String, Object> java = getJavaInfo();
            result.put("java", java);
            
            // 操作系统信息
            Map<String, Object> os = getOsInfo();
            result.put("os", os);
            
            log.debug("获取系统信息成功");
            return result;
        } catch (Exception e) {
            log.error("获取系统信息失败", e);
            Map<String, Object> result = new HashMap<>();
            result.put("error", e.getMessage());
            return result;
        }
    }

    /**
     * 获取 JVM 指标
     * 
     * @return JVM 指标信息
     */
    private Map<String, Object> getJvmMetrics() {
        Map<String, Object> jvm = new HashMap<>();
        
        // 内存信息
        MemoryMXBean memoryBean = ManagementFactory.getMemoryMXBean();
        Map<String, Object> memory = new HashMap<>();
        memory.put("heapUsed", memoryBean.getHeapMemoryUsage().getUsed());
        memory.put("heapMax", memoryBean.getHeapMemoryUsage().getMax());
        memory.put("heapCommitted", memoryBean.getHeapMemoryUsage().getCommitted());
        memory.put("nonHeapUsed", memoryBean.getNonHeapMemoryUsage().getUsed());
        memory.put("nonHeapMax", memoryBean.getNonHeapMemoryUsage().getMax());
        jvm.put("memory", memory);
        
        // 线程信息
        ThreadMXBean threadBean = ManagementFactory.getThreadMXBean();
        Map<String, Object> thread = new HashMap<>();
        thread.put("threadCount", threadBean.getThreadCount());
        thread.put("peakThreadCount", threadBean.getPeakThreadCount());
        thread.put("daemonThreadCount", threadBean.getDaemonThreadCount());
        jvm.put("thread", thread);
        
        // 运行时信息
        RuntimeMXBean runtimeBean = ManagementFactory.getRuntimeMXBean();
        Map<String, Object> runtime = new HashMap<>();
        runtime.put("uptime", runtimeBean.getUptime());
        runtime.put("startTime", runtimeBean.getStartTime());
        jvm.put("runtime", runtime);
        
        return jvm;
    }

    /**
     * 获取 HTTP 指标
     * 
     * @return HTTP 指标信息
     */
    private Map<String, Object> getHttpMetrics() {
        Map<String, Object> http = new HashMap<>();
        
        try {
            // 尝试从 MetricsEndpoint 获取 HTTP 指标
            MetricsEndpoint.MetricResponse requests = metricsEndpoint.metric("http.server.requests", null);
            if (requests != null && requests.getMeasurements() != null) {
                for (MetricsEndpoint.Sample sample : requests.getMeasurements()) {
                    String statistic = sample.getStatistic().name();
                    http.put(statistic, sample.getValue());
                }
            }
        } catch (Exception e) {
            log.debug("获取 HTTP 指标失败", e);
        }
        
        return http;
    }

    /**
     * 获取系统指标
     * 
     * @return 系统指标信息
     */
    private Map<String, Object> getSystemMetrics() {
        Map<String, Object> system = new HashMap<>();
        
        Runtime runtime = Runtime.getRuntime();
        system.put("processors", runtime.availableProcessors());
        system.put("totalMemory", runtime.totalMemory());
        system.put("freeMemory", runtime.freeMemory());
        system.put("maxMemory", runtime.maxMemory());
        
        return system;
    }

    /**
     * 获取 Java 信息
     * 
     * @return Java 信息
     */
    private Map<String, Object> getJavaInfo() {
        Map<String, Object> java = new HashMap<>();
        RuntimeMXBean runtimeBean = ManagementFactory.getRuntimeMXBean();
        
        java.put("version", System.getProperty("java.version"));
        java.put("vendor", System.getProperty("java.vendor"));
        java.put("vmName", runtimeBean.getVmName());
        java.put("vmVersion", runtimeBean.getVmVersion());
        java.put("vmVendor", runtimeBean.getVmVendor());
        
        return java;
    }

    /**
     * 获取操作系统信息
     * 
     * @return 操作系统信息
     */
    private Map<String, Object> getOsInfo() {
        Map<String, Object> os = new HashMap<>();
        
        os.put("name", System.getProperty("os.name"));
        os.put("version", System.getProperty("os.version"));
        os.put("arch", System.getProperty("os.arch"));
        
        return os;
    }
}

