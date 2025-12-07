package com.example.common.exception;

import com.example.common.result.Result;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.validation.BindException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import java.util.stream.Collectors;

/**
 * 全局异常处理器
 * <p>
 * 统一处理应用中抛出的各种异常，将异常转换为统一的 {@link Result} 响应格式
 * </p>
 * <p>
 * 处理的异常类型包括：
 * <ul>
 *   <li>{@link BusinessException}: 业务异常</li>
 *   <li>{@link MethodArgumentNotValidException}: 方法参数验证异常（@RequestBody）</li>
 *   <li>{@link BindException}: 参数绑定异常（@ModelAttribute）</li>
 *   <li>{@link ConstraintViolationException}: 约束验证异常（@RequestParam、@PathVariable）</li>
 *   <li>{@link IllegalArgumentException}: 非法参数异常</li>
 *   <li>{@link RuntimeException}: 运行时异常</li>
 *   <li>{@link Exception}: 其他所有异常</li>
 * </ul>
 * </p>
 * 
 * @author system
 * @version 1.0.0
 * @since 1.0.0
 */
@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    /**
     * 处理业务异常
     * 
     * @param e 业务异常
     * @return 错误响应结果，使用异常中的错误码和消息
     */
    @ExceptionHandler(BusinessException.class)
    public Result<?> handleBusinessException(BusinessException e) {
        log.warn("业务异常: {}", e.getMessage());
        return Result.error(e.getCode(), e.getMessage());
    }

    /**
     * 处理参数验证异常
     * <p>
     * 处理 @RequestBody 和 @ModelAttribute 的参数验证异常
     * </p>
     * 
     * @param e 参数验证异常（MethodArgumentNotValidException 或 BindException）
     * @return 错误响应结果，code=400，message=验证失败的具体信息
     */
    @ExceptionHandler({MethodArgumentNotValidException.class, BindException.class})
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Result<?> handleValidationException(Exception e) {
        String message = "参数验证失败";
        if (e instanceof MethodArgumentNotValidException) {
            MethodArgumentNotValidException ex = (MethodArgumentNotValidException) e;
            message = ex.getBindingResult().getFieldErrors().stream()
                    .map(FieldError::getDefaultMessage)
                    .collect(Collectors.joining(", "));
        } else if (e instanceof BindException) {
            BindException ex = (BindException) e;
            message = ex.getBindingResult().getFieldErrors().stream()
                    .map(FieldError::getDefaultMessage)
                    .collect(Collectors.joining(", "));
        }
        log.warn("参数验证异常: {}", message);
        return Result.error(400, message);
    }

    /**
     * 处理约束验证异常
     * <p>
     * 处理 @RequestParam、@PathVariable 等参数的约束验证异常
     * </p>
     * 
     * @param e 约束验证异常
     * @return 错误响应结果，code=400，message=验证失败的具体信息
     */
    @ExceptionHandler(ConstraintViolationException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Result<?> handleConstraintViolationException(ConstraintViolationException e) {
        String message = e.getConstraintViolations().stream()
                .map(ConstraintViolation::getMessage)
                .collect(Collectors.joining(", "));
        log.warn("约束验证异常: {}", message);
        return Result.error(400, message);
    }

    /**
     * 处理非法参数异常
     * 
     * @param e 非法参数异常
     * @return 错误响应结果，code=400，message=异常消息
     */
    @ExceptionHandler(IllegalArgumentException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Result<?> handleIllegalArgumentException(IllegalArgumentException e) {
        log.warn("参数异常: {}", e.getMessage());
        return Result.error(400, e.getMessage());
    }

    /**
     * 处理运行时异常
     * 
     * @param e 运行时异常
     * @return 错误响应结果，code=500，message=异常消息
     */
    @ExceptionHandler(RuntimeException.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public Result<?> handleRuntimeException(RuntimeException e) {
        log.error("运行时异常", e);
        return Result.error("系统异常：" + e.getMessage());
    }

    /**
     * 处理所有其他异常
     * <p>
     * 作为兜底处理器，捕获所有未被其他处理器处理的异常
     * </p>
     * 
     * @param e 异常
     * @return 错误响应结果，code=500，message=异常消息或异常类名
     */
    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public Result<?> handleException(Exception e) {
        log.error("系统异常", e);
        return Result.error("系统异常：" + (e.getMessage() != null ? e.getMessage() : e.getClass().getSimpleName()));
    }
}

