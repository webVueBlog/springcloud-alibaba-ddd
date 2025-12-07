package com.example.order.infrastructure.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.order.infrastructure.po.OrderPO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * 订单 Mapper 接口
 * <p>
 * MyBatis Plus Mapper 接口，用于数据访问操作
 * </p>
 * <p>
 * 功能说明：
 * <ul>
 *   <li>继承 BaseMapper，提供基础的 CRUD 操作</li>
 *   <li>使用 @Select 注解定义自定义查询</li>
 *   <li>所有查询都包含逻辑删除条件（deleted = 0）</li>
 * </ul>
 * </p>
 * <p>
 * 注意：
 * <ul>
 *   <li>使用 MyBatis Plus 的逻辑删除功能</li>
 *   <li>查询时自动过滤已删除的记录</li>
 * </ul>
 * </p>
 * 
 * @author system
 * @version 1.0.0
 * @since 1.0.0
 */
@Mapper
public interface OrderMapper extends BaseMapper<OrderPO> {
    
    /**
     * 根据订单号查询订单
     * <p>
     * 只查询未删除的订单（deleted = 0）
     * </p>
     * 
     * @param orderNo 订单号，不能为 null 或空字符串
     * @return 订单持久化对象，如果不存在返回 null
     */
    @Select("SELECT * FROM t_order WHERE order_no = #{orderNo} AND deleted = 0")
    OrderPO selectByOrderNo(@Param("orderNo") String orderNo);

    /**
     * 根据用户ID查询订单列表
     * <p>
     * 查询指定用户的所有订单，按创建时间倒序排列
     * 只查询未删除的订单（deleted = 0）
     * </p>
     * 
     * @param userId 用户ID，不能为 null
     * @return 订单列表，如果不存在返回空列表
     */
    @Select("SELECT * FROM t_order WHERE user_id = #{userId} AND deleted = 0 ORDER BY create_time DESC")
    List<OrderPO> selectByUserId(@Param("userId") Long userId);
}

