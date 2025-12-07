package com.example.i18n.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.support.ResourceBundleMessageSource;
import org.springframework.web.servlet.LocaleResolver;
import org.springframework.web.servlet.i18n.SessionLocaleResolver;

import java.util.Locale;

/**
 * 国际化配置类
 * <p>
 * 配置 Spring 的国际化功能，包括：
 * <ul>
 *   <li>LocaleResolver：语言环境解析器，用于从请求中解析语言</li>
 *   <li>MessageSource：消息源，用于加载和获取国际化消息</li>
 * </ul>
 * </p>
 * <p>
 * 配置说明：
 * <ul>
 *   <li>默认语言：简体中文（zh-CN）</li>
 *   <li>消息文件：messages.properties、messages_zh_CN.properties、messages_en_US.properties</li>
 *   <li>消息文件编码：UTF-8</li>
 *   <li>如果消息不存在，使用 code 作为默认消息</li>
 * </ul>
 * </p>
 * 
 * @author system
 * @version 1.0.0
 * @since 1.0.0
 */
@Configuration
public class I18nConfig {

    /**
     * 创建语言环境解析器
     * <p>
     * 使用 SessionLocaleResolver，支持以下方式设置语言：
     * <ul>
     *   <li>请求头 Accept-Language</li>
     *   <li>Session 中的语言设置</li>
     *   <li>默认语言（简体中文）</li>
     * </ul>
     * </p>
     * <p>
     * 也可以通过 CookieLocaleResolver 使用 Cookie 存储语言设置
     * </p>
     * 
     * @return 语言环境解析器
     */
    @Bean
    public LocaleResolver localeResolver() {
        SessionLocaleResolver resolver = new SessionLocaleResolver();
        // 设置默认语言为简体中文
        resolver.setDefaultLocale(Locale.SIMPLIFIED_CHINESE);
        return resolver;
    }

    /**
     * 创建消息源
     * <p>
     * 配置说明：
     * <ul>
     *   <li>basename: 消息文件的基础名称（不包含语言后缀和扩展名）</li>
     *   <li>defaultEncoding: 消息文件的编码格式，使用 UTF-8 支持中文</li>
     *   <li>useCodeAsDefaultMessage: 如果消息不存在，使用 code 作为默认消息</li>
     * </ul>
     * </p>
     * <p>
     * 消息文件命名规则：
     * <ul>
     *   <li>messages.properties - 默认消息</li>
     *   <li>messages_zh_CN.properties - 简体中文消息</li>
     *   <li>messages_en_US.properties - 美式英语消息</li>
     *   <li>messages_en.properties - 英语消息（通用）</li>
     * </ul>
     * </p>
     * 
     * @return 消息源
     */
    @Bean
    public ResourceBundleMessageSource messageSource() {
        ResourceBundleMessageSource messageSource = new ResourceBundleMessageSource();
        // 设置消息文件的基础名称（不包含语言后缀和扩展名）
        messageSource.setBasename("messages");
        // 设置消息文件的编码格式为 UTF-8，支持中文
        messageSource.setDefaultEncoding("UTF-8");
        // 如果消息不存在，使用 code 作为默认消息
        messageSource.setUseCodeAsDefaultMessage(true);
        return messageSource;
    }
}

