package com.example.order.interfaces.controller;

import com.example.common.exception.BusinessException;
import com.example.common.result.Result;
import com.example.order.application.dto.OrderDTO;
import com.example.order.application.service.OrderService;
import com.example.order.domain.model.Order;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;
import java.util.List;

/**
 * 订单控制器
 * <p>
 * 提供订单相关的 REST API 接口，属于接口层（interfaces）
 * </p>
 * <p>
 * 功能说明：
 * <ul>
 *   <li>创建订单：接收订单创建请求，调用应用服务创建订单</li>
 *   <li>查询订单：提供多种查询方式（ID、订单号、用户ID）</li>
 *   <li>更新订单状态：更新订单的状态</li>
 * </ul>
 * </p>
 * <p>
 * 请求路径：/api/order
 * </p>
 * <p>
 * 参数验证：
 * <ul>
 *   <li>使用 @Valid 验证请求体</li>
 *   <li>使用 @Validated 验证路径参数和请求参数</li>
 *   <li>使用 Bean Validation 注解进行参数校验</li>
 * </ul>
 * </p>
 * 
 * @author system
 * @version 1.0.0
 * @since 1.0.0
 */
@Slf4j
@RestController
@RequestMapping("/api/order")
@RequiredArgsConstructor
@Validated
public class OrderController {

    /** 订单应用服务 */
    private final OrderService orderService;

    /**
     * 创建订单
     * <p>
     * 接收订单创建请求，调用应用服务创建订单
     * </p>
     * 
     * @param orderDTO 创建订单 DTO，包含用户ID、商品ID、数量、价格等信息
     * @return 创建成功的订单信息
     */
    @PostMapping("/create")
    public Result<Order> createOrder(@Valid @RequestBody OrderDTO orderDTO) {
        try {
            log.info("收到创建订单请求: userId={}, productId={}, quantity={}", 
                    orderDTO.getUserId(), orderDTO.getProductId(), orderDTO.getQuantity());
            
            Order order = orderService.createOrder(orderDTO);
            
            log.info("创建订单成功: orderId={}, orderNo={}", order.getId(), order.getOrderNo());
            return Result.success(order);
        } catch (BusinessException e) {
            log.warn("创建订单失败: {}", e.getMessage());
            return Result.error(e.getMessage());
        } catch (Exception e) {
            log.error("创建订单异常", e);
            return Result.error("创建订单失败: " + e.getMessage());
        }
    }

    /**
     * 根据ID查询订单
     * 
     * @param id 订单ID，必须大于 0
     * @return 订单信息，如果不存在返回错误
     */
    @GetMapping("/{id}")
    public Result<Order> getOrderById(@PathVariable @NotNull @Positive Long id) {
        try {
            Order order = orderService.getOrderById(id);
            if (order == null) {
                return Result.error("订单不存在");
            }
            return Result.success(order);
        } catch (Exception e) {
            log.error("查询订单失败: orderId={}", id, e);
            return Result.error("查询订单失败: " + e.getMessage());
        }
    }

    /**
     * 根据订单号查询订单
     * 
     * @param orderNo 订单号，不能为空
     * @return 订单信息，如果不存在返回错误
     */
    @GetMapping("/orderNo/{orderNo}")
    public Result<Order> getOrderByOrderNo(@PathVariable @NotNull String orderNo) {
        try {
            Order order = orderService.getOrderByOrderNo(orderNo);
            if (order == null) {
                return Result.error("订单不存在");
            }
            return Result.success(order);
        } catch (Exception e) {
            log.error("查询订单失败: orderNo={}", orderNo, e);
            return Result.error("查询订单失败: " + e.getMessage());
        }
    }

    /**
     * 根据用户ID查询订单列表
     * <p>
     * 查询指定用户的所有订单，按创建时间倒序排列
     * </p>
     * 
     * @param userId 用户ID，必须大于 0
     * @return 订单列表
     */
    @GetMapping("/user/{userId}")
    public Result<List<Order>> getOrdersByUserId(@PathVariable @NotNull @Positive Long userId) {
        try {
            List<Order> orders = orderService.getOrdersByUserId(userId);
            return Result.success(orders);
        } catch (Exception e) {
            log.error("查询用户订单列表失败: userId={}", userId, e);
            return Result.error("查询用户订单列表失败: " + e.getMessage());
        }
    }

    /**
     * 更新订单状态
     * <p>
     * 订单状态说明：
     * <ul>
     *   <li>1 - 待支付</li>
     *   <li>2 - 已支付</li>
     *   <li>3 - 已发货</li>
     *   <li>4 - 已完成</li>
     *   <li>5 - 已取消</li>
     * </ul>
     * </p>
     * 
     * @param id 订单ID，必须大于 0
     * @param status 订单状态，1-待支付，2-已支付，3-已发货，4-已完成，5-已取消
     * @return 操作结果
     */
    @PutMapping("/{id}/status")
    public Result<Void> updateOrderStatus(
            @PathVariable @NotNull @Positive Long id, 
            @RequestParam @NotNull @Positive Integer status) {
        try {
            orderService.updateOrderStatus(id, status);
            return Result.success();
        } catch (BusinessException e) {
            log.warn("更新订单状态失败: {}", e.getMessage());
            return Result.error(e.getMessage());
        } catch (Exception e) {
            log.error("更新订单状态异常: orderId={}, status={}", id, status, e);
            return Result.error("更新订单状态失败: " + e.getMessage());
        }
    }
}

