package com.example.common.exception;

import lombok.Getter;

/**
 * 业务异常类
 * <p>
 * 用于在业务逻辑中抛出业务异常，会被 {@link GlobalExceptionHandler} 统一处理
 * </p>
 * <p>
 * 使用示例：
 * <pre>
 * // 抛出默认错误码（500）的业务异常
 * throw new BusinessException("用户名或密码错误");
 * 
 * // 抛出默认错误码（500）的业务异常，带异常原因
 * throw new BusinessException("查询订单失败: " + e.getMessage(), e);
 * 
 * // 抛出指定错误码的业务异常
 * throw new BusinessException(400, "参数不能为空");
 * 
 * // 抛出指定错误码的业务异常，带异常原因
 * throw new BusinessException(400, "参数不能为空", e);
 * </pre>
 * </p>
 * 
 * @author system
 * @version 1.0.0
 * @since 1.0.0
 */
@Getter
public class BusinessException extends RuntimeException {
    private static final long serialVersionUID = 1L;
    
    /** 错误状态码，默认500 */
    private final Integer code;

    /**
     * 构造函数（使用默认错误码500）
     * 
     * @param message 错误消息
     */
    public BusinessException(String message) {
        super(message);
        this.code = 500;
    }

    /**
     * 构造函数（使用默认错误码500，带异常原因）
     * 
     * @param message 错误消息
     * @param cause 异常原因
     */
    public BusinessException(String message, Throwable cause) {
        super(message, cause);
        this.code = 500;
    }

    /**
     * 构造函数（指定错误码）
     * 
     * @param code 错误状态码（如400、401、403、404、500等）
     * @param message 错误消息
     */
    public BusinessException(Integer code, String message) {
        super(message);
        this.code = code;
    }

    /**
     * 构造函数（指定错误码，带异常原因）
     * 
     * @param code 错误状态码（如400、401、403、404、500等）
     * @param message 错误消息
     * @param cause 异常原因
     */
    public BusinessException(Integer code, String message, Throwable cause) {
        super(message, cause);
        this.code = code;
    }
}

