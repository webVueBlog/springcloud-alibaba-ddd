package com.example.order.application.dto;

import lombok.Data;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;
import java.math.BigDecimal;

/**
 * 创建订单 DTO
 * <p>
 * 用于接收创建订单的请求参数，包含订单的基本信息
 * </p>
 * <p>
 * 使用场景：
 * <ul>
 *   <li>在 Controller 中接收 HTTP 请求参数</li>
 *   <li>在应用服务中转换为领域模型</li>
 *   <li>使用 Bean Validation 进行参数校验</li>
 * </ul>
 * </p>
 * <p>
 * 验证规则：
 * <ul>
 *   <li>userId、productId、quantity、price 不能为空</li>
 *   <li>quantity 必须大于 0</li>
 *   <li>price 必须大于 0</li>
 *   <li>remark 为可选字段</li>
 * </ul>
 * </p>
 * 
 * @author system
 * @version 1.0.0
 * @since 1.0.0
 */
@Data
public class OrderDTO {
    
    /**
     * 用户ID
     * <p>
     * 订单所属用户的ID，不能为空
     * </p>
     */
    @NotNull(message = "用户ID不能为空")
    private Long userId;

    /**
     * 商品ID
     * <p>
     * 订单关联的商品ID，不能为空
     * </p>
     */
    @NotNull(message = "商品ID不能为空")
    private Long productId;

    /**
     * 购买数量
     * <p>
     * 必须大于 0
     * </p>
     */
    @NotNull(message = "数量不能为空")
    @Positive(message = "数量必须大于0")
    private Integer quantity;

    /**
     * 商品单价
     * <p>
     * 商品单价，单位：元
     * 订单总金额 = price * quantity
     * </p>
     */
    @NotNull(message = "价格不能为空")
    @Positive(message = "价格必须大于0")
    private BigDecimal price;

    /**
     * 备注信息
     * <p>
     * 订单的备注信息，可选
     * </p>
     */
    private String remark;
}

