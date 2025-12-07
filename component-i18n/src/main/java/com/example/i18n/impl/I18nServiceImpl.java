package com.example.i18n.impl;

import com.example.i18n.I18nService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.LocaleResolver;

import javax.servlet.http.HttpServletRequest;
import java.util.Locale;

/**
 * 国际化服务实现
 * <p>
 * 基于 Spring 的 MessageSource 和 LocaleResolver 实现国际化功能
 * 支持从 properties 文件加载多语言消息，支持参数替换
 * </p>
 * <p>
 * 实现说明：
 * <ul>
 *   <li>使用 MessageSource 加载和获取消息</li>
 *   <li>使用 LocaleResolver 解析语言环境</li>
 *   <li>支持 LocaleContextHolder 获取当前线程的语言环境</li>
 *   <li>支持消息参数替换（使用 {0}、{1} 等占位符）</li>
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
public class I18nServiceImpl implements I18nService {

    /** 消息源，用于加载和获取国际化消息 */
    private final MessageSource messageSource;
    
    /** 语言解析器，用于从请求中解析语言环境 */
    private final LocaleResolver localeResolver;

    /**
     * 获取国际化消息
     * <p>
     * 从 LocaleContextHolder 获取当前线程的语言环境，然后从 MessageSource 获取消息
     * </p>
     * 
     * @param code 消息代码
     * @param args 消息参数
     * @return 国际化后的消息
     */
    @Override
    public String getMessage(String code, Object... args) {
        try {
            Locale locale = LocaleContextHolder.getLocale();
            String message = messageSource.getMessage(code, args, code, locale);
            log.debug("获取国际化消息: code={}, locale={}, message={}", code, locale, message);
            return message;
        } catch (Exception e) {
            log.error("获取国际化消息失败: code={}", code, e);
            // 如果获取失败，返回 code 本身
            return code;
        }
    }

    /**
     * 获取国际化消息（指定语言）
     * <p>
     * 根据指定的语言环境获取消息
     * </p>
     * 
     * @param code 消息代码
     * @param locale 语言环境字符串，格式如 "zh-CN"、"en-US" 等
     * @param args 消息参数
     * @return 国际化后的消息
     */
    @Override
    public String getMessage(String code, String locale, Object... args) {
        try {
            Locale targetLocale = Locale.forLanguageTag(locale);
            String message = messageSource.getMessage(code, args, code, targetLocale);
            log.debug("获取国际化消息: code={}, locale={}, message={}", code, locale, message);
            return message;
        } catch (Exception e) {
            log.error("获取国际化消息失败: code={}, locale={}", code, locale, e);
            // 如果获取失败，返回 code 本身
            return code;
        }
    }

    /**
     * 从请求中获取语言环境
     * <p>
     * 使用 LocaleResolver 从 HTTP 请求中解析语言环境
     * </p>
     * 
     * @param request HTTP 请求对象
     * @return 语言环境标签，格式如 "zh-CN"、"en-US" 等
     */
    @Override
    public String getLocaleFromRequest(HttpServletRequest request) {
        try {
            Locale locale = localeResolver.resolveLocale(request);
            String localeTag = locale.toLanguageTag();
            log.debug("从请求中获取语言环境: locale={}", localeTag);
            return localeTag;
        } catch (Exception e) {
            log.error("从请求中获取语言环境失败", e);
            // 如果获取失败，返回默认语言
            return Locale.SIMPLIFIED_CHINESE.toLanguageTag();
        }
    }
}

