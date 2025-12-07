package com.example.common.result;

import lombok.Data;

import java.io.Serializable;

/**
 * 统一响应结果封装类
 * <p>
 * 用于统一封装所有 API 接口的响应结果，包含以下字段：
 * <ul>
 *   <li>code: 响应状态码（200表示成功，其他表示失败）</li>
 *   <li>message: 响应消息</li>
 *   <li>data: 响应数据（泛型，可以是任意类型）</li>
 *   <li>timestamp: 响应时间戳（毫秒）</li>
 * </ul>
 * </p>
 * <p>
 * 使用示例：
 * <pre>
 * // 成功响应（无数据）
 * return Result.success();
 * 
 * // 成功响应（有数据）
 * return Result.success(user);
 * 
 * // 错误响应
 * return Result.error("操作失败");
 * return Result.error(400, "参数错误");
 * </pre>
 * </p>
 * 
 * @param <T> 响应数据的类型
 * @author system
 * @version 1.0.0
 * @since 1.0.0
 */
@Data
public class Result<T> implements Serializable {
    private static final long serialVersionUID = 1L;

    /** 响应状态码：200-成功，400-参数错误，401-未授权，403-无权限，404-资源不存在，500-服务器错误 */
    private Integer code;
    
    /** 响应消息 */
    private String message;
    
    /** 响应数据 */
    private T data;
    
    /** 响应时间戳（毫秒） */
    private Long timestamp;

    /**
     * 构造函数
     * <p>自动设置当前时间戳</p>
     */
    public Result() {
        this.timestamp = System.currentTimeMillis();
    }

    /**
     * 创建成功响应（无数据）
     * 
     * @param <T> 响应数据类型
     * @return 成功响应结果，code=200，message="操作成功"
     */
    public static <T> Result<T> success() {
        Result<T> result = new Result<>();
        result.setCode(200);
        result.setMessage("操作成功");
        return result;
    }

    /**
     * 创建成功响应（有数据）
     * 
     * @param <T> 响应数据类型
     * @param data 响应数据
     * @return 成功响应结果，code=200，message="操作成功"，data=传入的数据
     */
    public static <T> Result<T> success(T data) {
        Result<T> result = success();
        result.setData(data);
        return result;
    }

    /**
     * 创建错误响应
     * 
     * @param <T> 响应数据类型
     * @param message 错误消息
     * @return 错误响应结果，code=500，message=传入的错误消息
     */
    public static <T> Result<T> error(String message) {
        Result<T> result = new Result<>();
        result.setCode(500);
        result.setMessage(message);
        return result;
    }

    /**
     * 创建错误响应（指定状态码）
     * 
     * @param <T> 响应数据类型
     * @param code 错误状态码（如400、401、403、404、500等）
     * @param message 错误消息
     * @return 错误响应结果，code=传入的状态码，message=传入的错误消息
     */
    public static <T> Result<T> error(Integer code, String message) {
        Result<T> result = new Result<>();
        result.setCode(code);
        result.setMessage(message);
        return result;
    }
}

