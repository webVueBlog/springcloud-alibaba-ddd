package com.example.order.domain.model;

import com.example.common.domain.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;

/**
 * 订单领域模型
 * <p>
 * 订单聚合根，包含订单的核心业务属性和行为
 * </p>
 * <p>
 * 订单状态说明：
 * <ul>
 *   <li>1 - 待支付：订单已创建，等待用户支付</li>
 *   <li>2 - 已支付：用户已支付，等待发货</li>
 *   <li>3 - 已发货：订单已发货，等待确认收货</li>
 *   <li>4 - 已完成：订单已完成，交易成功</li>
 *   <li>5 - 已取消：订单已取消</li>
 * </ul>
 * </p>
 * <p>
 * 继承说明：
 * <ul>
 *   <li>继承 BaseEntity，包含 id、createTime、updateTime、deleted 等基础字段</li>
 *   <li>使用 MyBatis Plus 的逻辑删除功能</li>
 * </ul>
 * </p>
 * 
 * @author system
 * @version 1.0.0
 * @since 1.0.0
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class Order extends BaseEntity {
    
    /**
     * 订单号（唯一标识）
     * <p>
     * 格式：ORD + 时间戳 + 随机字符串
     * 示例：ORD1704067200000ABCDEFGH
     * </p>
     */
    private String orderNo;
    
    /**
     * 用户ID
     * <p>
     * 订单所属用户的ID
     * </p>
     */
    private Long userId;
    
    /**
     * 商品ID
     * <p>
     * 订单关联的商品ID
     * </p>
     */
    private Long productId;
    
    /**
     * 购买数量
     * <p>
     * 必须大于 0
     * </p>
     */
    private Integer quantity;
    
    /**
     * 订单金额
     * <p>
     * 订单总金额，单位：元
     * 计算公式：price * quantity
     * </p>
     */
    private BigDecimal amount;
    
    /**
     * 订单状态
     * <p>
     * 1-待支付，2-已支付，3-已发货，4-已完成，5-已取消
     * </p>
     */
    private Integer status;
    
    /**
     * 备注信息
     * <p>
     * 订单的备注信息，可选
     * </p>
     */
    private String remark;
}

