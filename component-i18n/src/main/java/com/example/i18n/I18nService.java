package com.example.i18n;

import javax.servlet.http.HttpServletRequest;

/**
 * 国际化服务接口
 * <p>
 * 提供国际化（i18n）消息获取功能，支持多语言切换
 * 基于 Spring 的 MessageSource 实现，支持从 properties 文件加载消息
 * </p>
 * <p>
 * 使用场景：
 * <ul>
 *   <li>API 响应消息的多语言支持</li>
 *   <li>错误消息的多语言显示</li>
 *   <li>业务提示信息的多语言支持</li>
 * </ul>
 * </p>
 * <p>
 * 使用示例：
 * <pre>
 * // 注入国际化服务
 * @Autowired
 * private I18nService i18nService;
 * 
 * // 获取当前语言环境的消息
 * String message = i18nService.getMessage("common.success");
 * // 中文环境：操作成功
 * // 英文环境：Operation successful
 * 
 * // 获取带参数的消息
 * String message = i18nService.getMessage("user.welcome", "张三");
 * 
 * // 获取指定语言的消息
 * String message = i18nService.getMessage("common.success", "en-US");
 * 
 * // 从请求中获取语言
 * String locale = i18nService.getLocaleFromRequest(request);
 * </pre>
 * </p>
 * 
 * @author system
 * @version 1.0.0
 * @since 1.0.0
 */
public interface I18nService {
    
    /**
     * 获取国际化消息
     * <p>
     * 根据当前语言环境获取对应的消息，如果消息不存在则返回 code 本身
     * </p>
     * <p>
     * 语言环境通过以下方式确定（按优先级）：
     * <ol>
     *   <li>请求头 Accept-Language</li>
     *   <li>Session 中的语言设置</li>
     *   <li>默认语言（简体中文）</li>
     * </ol>
     * </p>
     * 
     * @param code 消息代码，对应 properties 文件中的 key
     * @param args 消息参数，用于替换消息中的占位符 {0}、{1} 等
     * @return 国际化后的消息，如果消息不存在则返回 code
     */
    String getMessage(String code, Object... args);

    /**
     * 获取国际化消息（指定语言）
     * <p>
     * 根据指定的语言环境获取对应的消息，如果消息不存在则返回 code 本身
     * </p>
     * 
     * @param code 消息代码，对应 properties 文件中的 key
     * @param locale 语言环境，格式如 "zh-CN"、"en-US"、"en" 等
     * @param args 消息参数，用于替换消息中的占位符 {0}、{1} 等
     * @return 国际化后的消息，如果消息不存在则返回 code
     */
    String getMessage(String code, String locale, Object... args);

    /**
     * 从请求中获取语言环境
     * <p>
     * 从 HTTP 请求中解析语言环境，支持以下方式：
     * <ul>
     *   <li>请求头 Accept-Language</li>
     *   <li>Session 中的语言设置</li>
     *   <li>默认语言（简体中文）</li>
     * </ul>
     * </p>
     * 
     * @param request HTTP 请求对象
     * @return 语言环境标签，格式如 "zh-CN"、"en-US" 等
     */
    String getLocaleFromRequest(HttpServletRequest request);
}

