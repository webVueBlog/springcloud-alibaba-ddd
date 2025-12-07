package com.example.common.util;

import java.util.regex.Pattern;

/**
 * 字符串工具类
 * <p>
 * 提供字符串相关的工具方法，包括：
 * <ul>
 *   <li>字符串判空</li>
 *   <li>手机号验证和脱敏</li>
 *   <li>邮箱验证和脱敏</li>
 * </ul>
 * </p>
 * <p>
 * 使用示例：
 * <pre>
 * // 判断字符串是否为空
 * if (StringUtil.isEmpty(str)) {
 *     // 处理空字符串
 * }
 * 
 * // 验证手机号
 * if (StringUtil.isPhone("13800138000")) {
 *     // 手机号格式正确
 * }
 * 
 * // 脱敏手机号
 * String masked = StringUtil.maskPhone("13800138000");
 * // 输出：138****8000
 * 
 * // 脱敏邮箱
 * String masked = StringUtil.maskEmail("test@example.com");
 * // 输出：te***@example.com
 * </pre>
 * </p>
 * 
 * @author system
 * @version 1.0.0
 * @since 1.0.0
 */
public class StringUtil {

    /** 手机号正则表达式：1开头，第二位3-9，共11位数字 */
    private static final Pattern PHONE_PATTERN = Pattern.compile("^1[3-9]\\d{9}$");
    
    /** 邮箱正则表达式 */
    private static final Pattern EMAIL_PATTERN = Pattern.compile("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$");

    /**
     * 私有构造函数，防止实例化
     */
    private StringUtil() {
        throw new UnsupportedOperationException("StringUtil class cannot be instantiated");
    }

    /**
     * 判断字符串是否为空
     * <p>
     * null 或空字符串（包括只包含空白字符的字符串）都视为空
     * </p>
     * 
     * @param str 待判断的字符串
     * @return true 表示字符串为空，false 表示不为空
     */
    public static boolean isEmpty(String str) {
        return str == null || str.trim().isEmpty();
    }

    /**
     * 判断字符串是否不为空
     * 
     * @param str 待判断的字符串
     * @return true 表示字符串不为空，false 表示为空
     */
    public static boolean isNotEmpty(String str) {
        return !isEmpty(str);
    }

    /**
     * 验证手机号格式
     * <p>
     * 验证规则：1开头，第二位3-9，共11位数字
     * </p>
     * 
     * @param phone 待验证的手机号
     * @return true 表示手机号格式正确，false 表示格式错误或为空
     */
    public static boolean isPhone(String phone) {
        if (isEmpty(phone)) {
            return false;
        }
        return PHONE_PATTERN.matcher(phone).matches();
    }

    /**
     * 验证邮箱格式
     * 
     * @param email 待验证的邮箱地址
     * @return true 表示邮箱格式正确，false 表示格式错误或为空
     */
    public static boolean isEmail(String email) {
        if (isEmpty(email)) {
            return false;
        }
        return EMAIL_PATTERN.matcher(email).matches();
    }

    /**
     * 脱敏手机号
     * <p>
     * 格式：前3位 + **** + 后4位
     * 例如：13800138000 -> 138****8000
     * </p>
     * 
     * @param phone 手机号
     * @return 脱敏后的手机号，如果手机号长度不足11位则返回原字符串
     */
    public static String maskPhone(String phone) {
        if (isEmpty(phone) || phone.length() < 11) {
            return phone;
        }
        return phone.substring(0, 3) + "****" + phone.substring(7);
    }

    /**
     * 脱敏邮箱
     * <p>
     * 格式：前2位（如果前缀长度<=2则只显示第1位）+ *** + @后面的部分
     * 例如：test@example.com -> te***@example.com
     * </p>
     * 
     * @param email 邮箱地址
     * @return 脱敏后的邮箱，如果邮箱格式不正确则返回原字符串
     */
    public static String maskEmail(String email) {
        if (isEmpty(email) || !email.contains("@")) {
            return email;
        }
        int atIndex = email.indexOf("@");
        String prefix = email.substring(0, atIndex);
        String suffix = email.substring(atIndex);
        if (prefix.length() <= 2) {
            return prefix.charAt(0) + "***" + suffix;
        }
        return prefix.substring(0, 2) + "***" + suffix;
    }
}

