package com.example.order.domain.repository;

import com.example.order.domain.model.Order;

import java.util.List;

/**
 * 订单仓储接口
 * <p>
 * 定义订单的持久化操作，属于领域层，不依赖具体的技术实现
 * </p>
 * <p>
 * 仓储模式说明：
 * <ul>
 *   <li>仓储接口定义在领域层，保持领域层纯净</li>
 *   <li>仓储实现在基础设施层，使用 MyBatis Plus 进行数据访问</li>
 *   <li>通过仓储模式隔离领域模型和持久化技术</li>
 * </ul>
 * </p>
 * <p>
 * 使用场景：
 * <ul>
 *   <li>在应用服务中通过仓储接口操作订单数据</li>
 *   <li>支持订单的增删改查操作</li>
 *   <li>支持按不同条件查询订单</li>
 * </ul>
 * </p>
 * 
 * @author system
 * @version 1.0.0
 * @since 1.0.0
 */
public interface OrderRepository {
    
    /**
     * 保存订单
     * <p>
     * 如果订单ID为空，则新增订单；否则更新订单
     * </p>
     * 
     * @param order 订单领域模型，不能为 null
     * @return 保存后的订单（包含生成的ID）
     */
    Order save(Order order);

    /**
     * 根据ID查询订单
     * <p>
     * 只查询未删除的订单（deleted = 0）
     * </p>
     * 
     * @param id 订单ID，不能为 null
     * @return 订单领域模型，如果不存在返回 null
     */
    Order findById(Long id);

    /**
     * 根据订单号查询订单
     * <p>
     * 订单号是唯一标识，用于快速查找订单
     * </p>
     * 
     * @param orderNo 订单号，不能为 null 或空字符串
     * @return 订单领域模型，如果不存在返回 null
     */
    Order findByOrderNo(String orderNo);

    /**
     * 根据用户ID查询订单列表
     * <p>
     * 查询指定用户的所有订单，按创建时间倒序排列
     * </p>
     * 
     * @param userId 用户ID，不能为 null
     * @return 订单列表，如果不存在返回空列表
     */
    List<Order> findByUserId(Long userId);

    /**
     * 更新订单状态
     * <p>
     * 更新指定订单的状态
     * </p>
     * 
     * @param id 订单ID，不能为 null
     * @param status 订单状态，1-待支付，2-已支付，3-已发货，4-已完成，5-已取消
     */
    void updateStatus(Long id, Integer status);
}

