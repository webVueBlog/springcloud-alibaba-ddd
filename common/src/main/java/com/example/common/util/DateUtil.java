package com.example.common.util;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * 日期时间工具类
 * <p>
 * 提供日期时间格式化、获取当前时间等常用功能
 * </p>
 * <p>
 * 使用示例：
 * <pre>
 * // 格式化日期时间
 * String dateTimeStr = DateUtil.formatDateTime(LocalDateTime.now());
 * // 输出：2024-01-01 12:00:00
 * 
 * // 格式化日期
 * String dateStr = DateUtil.formatDate(LocalDateTime.now());
 * // 输出：2024-01-01
 * 
 * // 获取当前时间
 * LocalDateTime now = DateUtil.now();
 * </pre>
 * </p>
 * 
 * @author system
 * @version 1.0.0
 * @since 1.0.0
 */
public class DateUtil {

    /** 日期时间格式化器：yyyy-MM-dd HH:mm:ss */
    private static final DateTimeFormatter DATETIME_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    
    /** 日期格式化器：yyyy-MM-dd */
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    /**
     * 私有构造函数，防止实例化
     */
    private DateUtil() {
        throw new UnsupportedOperationException("DateUtil class cannot be instantiated");
    }

    /**
     * 格式化日期时间为字符串
     * <p>
     * 格式：yyyy-MM-dd HH:mm:ss
     * </p>
     * 
     * @param dateTime 日期时间对象
     * @return 格式化后的字符串，如果 dateTime 为 null 则返回 null
     */
    public static String formatDateTime(LocalDateTime dateTime) {
        if (dateTime == null) {
            return null;
        }
        return dateTime.format(DATETIME_FORMATTER);
    }

    /**
     * 格式化日期为字符串
     * <p>
     * 格式：yyyy-MM-dd
     * </p>
     * 
     * @param dateTime 日期时间对象
     * @return 格式化后的日期字符串，如果 dateTime 为 null 则返回 null
     */
    public static String formatDate(LocalDateTime dateTime) {
        if (dateTime == null) {
            return null;
        }
        return dateTime.format(DATE_FORMATTER);
    }

    /**
     * 获取当前日期时间
     * 
     * @return 当前日期时间对象
     */
    public static LocalDateTime now() {
        return LocalDateTime.now();
    }
}

