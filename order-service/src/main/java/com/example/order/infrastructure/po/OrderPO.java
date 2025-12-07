package com.example.order.infrastructure.po;

import com.baomidou.mybatisplus.annotation.TableName;
import com.example.common.domain.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import java.math.BigDecimal;

/**
 * 订单持久化对象（PO）
 * <p>
 * 用于 MyBatis Plus 的数据持久化，对应数据库表 t_order
 * </p>
 * <p>
 * 与领域模型的区别：
 * <ul>
 *   <li>PO 属于基础设施层，依赖 MyBatis Plus 注解</li>
 *   <li>领域模型属于领域层，不依赖任何框架</li>
 *   <li>通过仓储实现进行 PO 和领域模型的转换</li>
 * </ul>
 * </p>
 * <p>
 * 表名：t_order
 * </p>
 * 
 * @author system
 * @version 1.0.0
 * @since 1.0.0
 */
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@TableName("t_order")
public class OrderPO extends BaseEntity {
    
    /**
     * 订单号（唯一标识）
     */
    private String orderNo;
    
    /**
     * 用户ID
     */
    private Long userId;
    
    /**
     * 商品ID
     */
    private Long productId;
    
    /**
     * 购买数量
     */
    private Integer quantity;
    
    /**
     * 订单金额
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
     */
    private String remark;
}

