package com.example.auth.interfaces.controller;

import com.example.common.result.Result;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 健康检查控制器
 * <p>
 * 提供服务的健康检查接口，用于监控服务运行状态
 * </p>
 * 
 * @author system
 * @version 1.0.0
 * @since 1.0.0
 */
@RestController
@RequestMapping("/api/auth")
public class HealthController {

    /**
     * 健康检查接口
     * <p>
     * 返回服务运行状态，可用于负载均衡器或监控系统的健康检查
     * </p>
     * 
     * @return 服务运行状态信息
     */
    @GetMapping("/health")
    public Result<String> health() {
        return Result.success("auth-service is running");
    }
}
