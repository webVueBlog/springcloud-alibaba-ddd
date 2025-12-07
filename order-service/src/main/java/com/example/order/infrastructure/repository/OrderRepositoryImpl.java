package com.example.order.infrastructure.repository;

import com.example.order.domain.model.Order;
import com.example.order.domain.repository.OrderRepository;
import com.example.order.infrastructure.mapper.OrderMapper;
import com.example.order.infrastructure.po.OrderPO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;
import org.springframework.util.Assert;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 订单仓储实现
 * <p>
 * 实现订单仓储接口，使用 MyBatis Plus 进行数据访问
 * </p>
 * <p>
 * 职责说明：
 * <ul>
 *   <li>实现领域层的仓储接口</li>
 *   <li>进行领域模型和持久化对象的转换</li>
 *   <li>使用 MyBatis Plus 进行数据访问</li>
 * </ul>
 * </p>
 * <p>
 * 转换说明：
 * <ul>
 *   <li>convertToDomain：将 PO 转换为领域模型</li>
 *   <li>convertToPO：将领域模型转换为 PO</li>
 *   <li>保持领域层和基础设施层的隔离</li>
 * </ul>
 * </p>
 * 
 * @author system
 * @version 1.0.0
 * @since 1.0.0
 */
@Slf4j
@Repository
@RequiredArgsConstructor
public class OrderRepositoryImpl implements OrderRepository {

    /** MyBatis Plus Mapper */
    private final OrderMapper orderMapper;

    /**
     * 保存订单
     * 
     * @param order 订单领域模型
     * @return 保存后的订单
     */
    @Override
    public Order save(Order order) {
        Assert.notNull(order, "订单不能为空");
        
        try {
            OrderPO po = convertToPO(order);
            if (order.getId() == null) {
                // 新增订单
                orderMapper.insert(po);
                order.setId(po.getId());
                log.debug("新增订单成功: orderId={}, orderNo={}", po.getId(), po.getOrderNo());
            } else {
                // 更新订单
                orderMapper.updateById(po);
                log.debug("更新订单成功: orderId={}, orderNo={}", po.getId(), po.getOrderNo());
            }
            return convertToDomain(po);
        } catch (Exception e) {
            log.error("保存订单失败: orderNo={}", order.getOrderNo(), e);
            throw new RuntimeException("保存订单失败: " + e.getMessage(), e);
        }
    }

    /**
     * 根据ID查询订单
     * 
     * @param id 订单ID
     * @return 订单领域模型
     */
    @Override
    public Order findById(Long id) {
        Assert.notNull(id, "订单ID不能为空");
        
        try {
            OrderPO po = orderMapper.selectById(id);
            return convertToDomain(po);
        } catch (Exception e) {
            log.error("查询订单失败: orderId={}", id, e);
            throw new RuntimeException("查询订单失败: " + e.getMessage(), e);
        }
    }

    /**
     * 根据订单号查询订单
     * 
     * @param orderNo 订单号
     * @return 订单领域模型
     */
    @Override
    public Order findByOrderNo(String orderNo) {
        Assert.hasText(orderNo, "订单号不能为空");
        
        try {
            OrderPO po = orderMapper.selectByOrderNo(orderNo);
            return convertToDomain(po);
        } catch (Exception e) {
            log.error("查询订单失败: orderNo={}", orderNo, e);
            throw new RuntimeException("查询订单失败: " + e.getMessage(), e);
        }
    }

    /**
     * 根据用户ID查询订单列表
     * 
     * @param userId 用户ID
     * @return 订单列表
     */
    @Override
    public List<Order> findByUserId(Long userId) {
        Assert.notNull(userId, "用户ID不能为空");
        
        try {
            List<OrderPO> pos = orderMapper.selectByUserId(userId);
            return pos.stream()
                    .map(this::convertToDomain)
                    .collect(Collectors.toList());
        } catch (Exception e) {
            log.error("查询用户订单列表失败: userId={}", userId, e);
            throw new RuntimeException("查询用户订单列表失败: " + e.getMessage(), e);
        }
    }

    /**
     * 更新订单状态
     * 
     * @param id 订单ID
     * @param status 订单状态
     */
    @Override
    public void updateStatus(Long id, Integer status) {
        Assert.notNull(id, "订单ID不能为空");
        Assert.notNull(status, "订单状态不能为空");
        
        try {
            OrderPO po = orderMapper.selectById(id);
            if (po != null) {
                po.setStatus(status);
                orderMapper.updateById(po);
                log.debug("更新订单状态成功: orderId={}, status={}", id, status);
            } else {
                log.warn("订单不存在，无法更新状态: orderId={}", id);
            }
        } catch (Exception e) {
            log.error("更新订单状态失败: orderId={}, status={}", id, status, e);
            throw new RuntimeException("更新订单状态失败: " + e.getMessage(), e);
        }
    }

    /**
     * 将持久化对象转换为领域模型
     * 
     * @param po 持久化对象
     * @return 领域模型
     */
    private Order convertToDomain(OrderPO po) {
        if (po == null) {
            return null;
        }
        
        Order order = new Order();
        order.setId(po.getId());
        order.setOrderNo(po.getOrderNo());
        order.setUserId(po.getUserId());
        order.setProductId(po.getProductId());
        order.setQuantity(po.getQuantity());
        order.setAmount(po.getAmount());
        order.setStatus(po.getStatus());
        order.setRemark(po.getRemark());
        order.setCreateTime(po.getCreateTime());
        order.setUpdateTime(po.getUpdateTime());
        order.setDeleted(po.getDeleted());
        return order;
    }

    /**
     * 将领域模型转换为持久化对象
     * 
     * @param order 领域模型
     * @return 持久化对象
     */
    private OrderPO convertToPO(Order order) {
        if (order == null) {
            return null;
        }
        
        OrderPO po = new OrderPO();
        po.setId(order.getId());
        po.setOrderNo(order.getOrderNo());
        po.setUserId(order.getUserId());
        po.setProductId(order.getProductId());
        po.setQuantity(order.getQuantity());
        po.setAmount(order.getAmount());
        po.setStatus(order.getStatus());
        po.setRemark(order.getRemark());
        po.setCreateTime(order.getCreateTime());
        po.setUpdateTime(order.getUpdateTime());
        po.setDeleted(order.getDeleted());
        return po;
    }
}

