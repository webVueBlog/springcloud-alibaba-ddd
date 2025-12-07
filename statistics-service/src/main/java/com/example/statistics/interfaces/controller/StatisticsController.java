package com.example.statistics.interfaces.controller;

import com.example.common.result.Result;
import com.example.statistics.application.service.StatisticsService;
import com.example.statistics.domain.model.EventLog;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.Map;

/**
 * 统计分析控制器
 * 
 * @author system
 * @version 1.0.0
 * @since 1.0.0
 */
@Slf4j
@RestController
@RequestMapping("/api/statistics")
@RequiredArgsConstructor
public class StatisticsController {
    
    private final StatisticsService statisticsService;
    
    /**
     * 记录事件日志
     */
    @PostMapping("/event")
    public Result<Void> recordEvent(@RequestBody EventLog eventLog) {
        statisticsService.recordEvent(eventLog);
        return Result.success();
    }
    
    /**
     * 获取PV/UV统计
     */
    @GetMapping("/visit")
    public Result<Map<String, Object>> getVisitStatistics(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        Map<String, Object> result = statisticsService.getVisitStatistics(startDate, endDate);
        return Result.success(result);
    }
    
    /**
     * 获取内容排行
     */
    @GetMapping("/ranking")
    public Result<Map<String, Object>> getContentRanking(
            @RequestParam(required = false) String contentType,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate statDate) {
        Map<String, Object> result = statisticsService.getContentRanking(contentType, statDate);
        return Result.success(result);
    }
    
    /**
     * 获取用户画像
     */
    @GetMapping("/user-profile/{userId}")
    public Result<Map<String, Object>> getUserProfile(@PathVariable Long userId) {
        Map<String, Object> result = statisticsService.getUserProfile(userId);
        return Result.success(result);
    }
}

