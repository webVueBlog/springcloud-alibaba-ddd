package com.example.order.application.service;

import com.example.common.exception.BusinessException;
import com.example.message.MessageProducer;
import com.example.order.application.dto.OrderDTO;
import com.example.order.domain.model.Order;
import com.example.order.domain.repository.OrderRepository;
import io.seata.spring.annotation.GlobalTransactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

/**
 * 订单应用服务
 * <p>
 * 负责协调领域对象完成业务用例，是应用层的核心
 * </p>
 * <p>
 * 职责说明：
 * <ul>
 *   <li>接收 DTO 参数，转换为领域模型</li>
 *   <li>调用领域服务或仓储完成业务逻辑</li>
 *   <li>管理事务边界</li>
 *   <li>发送领域事件（通过消息队列）</li>
 * </ul>
 * </p>
 * <p>
 * 事务管理：
 * <ul>
 *   <li>使用 @GlobalTransactional 管理分布式事务（Seata）</li>
 *   <li>使用 @Transactional 管理本地事务</li>
 *   <li>异常时自动回滚</li>
 * </ul>
 * </p>
 * 
 * @author system
 * @version 1.0.0
 * @since 1.0.0
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class OrderService {

    /** 订单仓储接口 */
    private final OrderRepository orderRepository;
    
    /** 消息生产者（可选，如果未配置则跳过消息发送） */
    @Autowired(required = false)
    private MessageProducer messageProducer;

    /**
     * 创建订单（使用分布式事务）
     * <p>
     * 创建订单的完整流程：
     * <ol>
     *   <li>验证订单参数</li>
     *   <li>构建订单领域模型</li>
     *   <li>生成订单号</li>
     *   <li>计算订单金额</li>
     *   <li>保存订单到数据库</li>
     *   <li>发送订单创建消息（可选）</li>
     * </ol>
     * </p>
     * <p>
     * 事务说明：
     * <ul>
     *   <li>使用 @GlobalTransactional 开启分布式事务（Seata）</li>
     *   <li>使用 @Transactional 开启本地事务</li>
     *   <li>如果发生异常，自动回滚</li>
     * </ul>
     * </p>
     * 
     * @param orderDTO 创建订单 DTO，不能为 null
     * @return 创建成功的订单领域模型
     * @throws BusinessException 如果订单参数无效
     */
    @GlobalTransactional(rollbackFor = Exception.class)
    @Transactional(rollbackFor = Exception.class)
    public Order createOrder(OrderDTO orderDTO) {
        Assert.notNull(orderDTO, "订单DTO不能为空");
        
        try {
            log.info("开始创建订单: userId={}, productId={}, quantity={}", 
                    orderDTO.getUserId(), orderDTO.getProductId(), orderDTO.getQuantity());
            
            // 构建订单领域模型
            Order order = new Order();
            order.setUserId(orderDTO.getUserId());
            order.setProductId(orderDTO.getProductId());
            order.setQuantity(orderDTO.getQuantity());
            
            // 计算订单金额：单价 * 数量
            BigDecimal amount = orderDTO.getPrice().multiply(new BigDecimal(orderDTO.getQuantity()));
            order.setAmount(amount);
            
            // 设置订单状态：1-待支付
            order.setStatus(1);
            
            // 生成订单号
            order.setOrderNo(generateOrderNo());
            
            // 设置备注
            order.setRemark(orderDTO.getRemark());
            
            // BaseEntity 的字段（id、createTime、updateTime、deleted）会在保存时自动设置

            // 保存订单到数据库
            order = orderRepository.save(order);
            log.info("创建订单成功: orderId={}, orderNo={}, userId={}, amount={}", 
                    order.getId(), order.getOrderNo(), order.getUserId(), order.getAmount());

            // 发送订单创建消息（如果消息生产者可用）
            sendOrderCreatedMessage(order);

            return order;
        } catch (Exception e) {
            log.error("创建订单失败: userId={}, productId={}", 
                    orderDTO.getUserId(), orderDTO.getProductId(), e);
            throw new BusinessException("创建订单失败: " + e.getMessage(), e);
        }
    }

    /**
     * 根据ID查询订单
     * 
     * @param id 订单ID，不能为 null
     * @return 订单领域模型，如果不存在返回 null
     */
    public Order getOrderById(Long id) {
        Assert.notNull(id, "订单ID不能为空");
        
        try {
            Order order = orderRepository.findById(id);
            if (order != null) {
                log.debug("查询订单成功: orderId={}, orderNo={}", id, order.getOrderNo());
            } else {
                log.warn("订单不存在: orderId={}", id);
            }
            return order;
        } catch (Exception e) {
            log.error("查询订单失败: orderId={}", id, e);
            throw new BusinessException("查询订单失败: " + e.getMessage(), e);
        }
    }

    /**
     * 根据订单号查询订单
     * 
     * @param orderNo 订单号，不能为 null 或空字符串
     * @return 订单领域模型，如果不存在返回 null
     */
    public Order getOrderByOrderNo(String orderNo) {
        Assert.hasText(orderNo, "订单号不能为空");
        
        try {
            Order order = orderRepository.findByOrderNo(orderNo);
            if (order != null) {
                log.debug("查询订单成功: orderNo={}, orderId={}", orderNo, order.getId());
            } else {
                log.warn("订单不存在: orderNo={}", orderNo);
            }
            return order;
        } catch (Exception e) {
            log.error("查询订单失败: orderNo={}", orderNo, e);
            throw new BusinessException("查询订单失败: " + e.getMessage(), e);
        }
    }

    /**
     * 根据用户ID查询订单列表
     * <p>
     * 查询指定用户的所有订单，按创建时间倒序排列
     * </p>
     * 
     * @param userId 用户ID，不能为 null
     * @return 订单列表，如果不存在返回空列表
     */
    public List<Order> getOrdersByUserId(Long userId) {
        Assert.notNull(userId, "用户ID不能为空");
        
        try {
            List<Order> orders = orderRepository.findByUserId(userId);
            log.debug("查询用户订单列表成功: userId={}, count={}", userId, orders.size());
            return orders;
        } catch (Exception e) {
            log.error("查询用户订单列表失败: userId={}", userId, e);
            throw new BusinessException("查询用户订单列表失败: " + e.getMessage(), e);
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
     * @param id 订单ID，不能为 null
     * @param status 订单状态，1-待支付，2-已支付，3-已发货，4-已完成，5-已取消
     * @throws BusinessException 如果订单不存在或状态无效
     */
    @Transactional(rollbackFor = Exception.class)
    public void updateOrderStatus(Long id, Integer status) {
        Assert.notNull(id, "订单ID不能为空");
        Assert.notNull(status, "订单状态不能为空");
        
        // 验证状态值
        if (status < 1 || status > 5) {
            throw new BusinessException("订单状态无效: " + status);
        }
        
        try {
            // 检查订单是否存在
            Order order = orderRepository.findById(id);
            if (order == null) {
                throw new BusinessException("订单不存在: orderId=" + id);
            }
            
            // 更新订单状态
            orderRepository.updateStatus(id, status);
            log.info("更新订单状态成功: orderId={}, orderNo={}, oldStatus={}, newStatus={}", 
                    id, order.getOrderNo(), order.getStatus(), status);
        } catch (BusinessException e) {
            throw e;
        } catch (Exception e) {
            log.error("更新订单状态失败: orderId={}, status={}", id, status, e);
            throw new BusinessException("更新订单状态失败: " + e.getMessage(), e);
        }
    }

    /**
     * 生成订单号
     * <p>
     * 订单号格式：ORD + 时间戳 + 8位随机字符串（大写）
     * 示例：ORD1704067200000ABCDEFGH
     * </p>
     * 
     * @return 订单号
     */
    private String generateOrderNo() {
        String orderNo = "ORD" + System.currentTimeMillis() + 
                UUID.randomUUID().toString().substring(0, 8).toUpperCase();
        log.debug("生成订单号: {}", orderNo);
        return orderNo;
    }

    /**
     * 发送订单创建消息
     * <p>
     * 如果消息生产者可用，发送订单创建消息到 RocketMQ
     * 消息发送失败不影响订单创建
     * </p>
     * 
     * @param order 订单领域模型
     */
    private void sendOrderCreatedMessage(Order order) {
        if (messageProducer != null) {
            try {
                messageProducer.send("order-topic", "create", order);
                log.info("订单创建消息发送成功: orderNo={}", order.getOrderNo());
            } catch (Exception e) {
                log.error("订单创建消息发送失败: orderNo={}", order.getOrderNo(), e);
                // 消息发送失败不影响订单创建，只记录日志
            }
        } else {
            log.debug("消息生产者未配置，跳过消息发送: orderNo={}", order.getOrderNo());
        }
    }
}

