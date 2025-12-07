package com.example.seckill.domain.service.impl;

import com.example.seckill.domain.model.SeckillActivity;
import com.example.seckill.domain.service.SeckillDomainService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import java.time.LocalDateTime;

/**
 * 秒杀领域服务实现
 * <p>
 * 实现秒杀活动的业务规则和业务逻辑
 * </p>
 * <p>
 * 功能说明：
 * <ul>
 *   <li>活动有效性检查：验证活动状态和时间</li>
 *   <li>库存管理：检查库存、扣减库存</li>
 *   <li>业务规则验证：验证业务规则是否满足</li>
 * </ul>
 * </p>
 * 
 * @author system
 * @version 1.0.0
 * @since 1.0.0
 */
@Slf4j
@Service
public class SeckillDomainServiceImpl implements SeckillDomainService {
    
    /**
     * 检查活动是否有效
     * 
     * @param activity 秒杀活动
     * @return true 表示活动有效
     */
    @Override
    public boolean isActivityValid(SeckillActivity activity) {
        if (activity == null) {
            log.warn("活动对象为 null");
            return false;
        }
        
        // 检查活动状态：必须为 1（进行中）
        if (activity.getStatus() == null || activity.getStatus() != 1) {
            log.warn("活动状态无效: activityId={}, status={}", activity.getId(), activity.getStatus());
            return false;
        }
        
        // 检查时间：当前时间必须在活动时间范围内
        LocalDateTime now = LocalDateTime.now();
        if (activity.getStartTime() != null && now.isBefore(activity.getStartTime())) {
            log.warn("活动未开始: activityId={}, startTime={}, now={}", 
                    activity.getId(), activity.getStartTime(), now);
            return false;
        }
        
        if (activity.getEndTime() != null && now.isAfter(activity.getEndTime())) {
            log.warn("活动已结束: activityId={}, endTime={}, now={}", 
                    activity.getId(), activity.getEndTime(), now);
            return false;
        }
        
        log.debug("活动有效: activityId={}", activity.getId());
        return true;
    }
    
    /**
     * 检查活动是否在进行中
     * 
     * @param activity 秒杀活动
     * @return true 表示活动正在进行
     */
    @Override
    public boolean isActivityActive(SeckillActivity activity) {
        if (!isActivityValid(activity)) {
            return false;
        }
        
        LocalDateTime now = LocalDateTime.now();
        boolean active = (activity.getStartTime() == null || !now.isBefore(activity.getStartTime()))
                && (activity.getEndTime() == null || !now.isAfter(activity.getEndTime()));
        
        log.debug("活动是否进行中: activityId={}, active={}", activity.getId(), active);
        return active;
    }
    
    /**
     * 检查库存是否充足
     * 
     * @param activity 秒杀活动
     * @return true 表示库存充足
     */
    @Override
    public boolean hasStock(SeckillActivity activity) {
        if (activity == null) {
            log.warn("活动对象为 null");
            return false;
        }
        
        Integer stock = activity.getStock();
        Integer sold = activity.getSold() != null ? activity.getSold() : 0;
        
        boolean hasStock = stock != null && stock > sold;
        log.debug("库存检查: activityId={}, stock={}, sold={}, hasStock={}", 
                activity.getId(), stock, sold, hasStock);
        return hasStock;
    }
    
    /**
     * 扣减库存
     * 
     * @param activity 秒杀活动
     * @return 扣减后的剩余库存
     */
    @Override
    public Integer deductStock(SeckillActivity activity) {
        Assert.notNull(activity, "活动对象不能为空");
        
        Integer stock = activity.getStock();
        Integer sold = activity.getSold() != null ? activity.getSold() : 0;
        
        // 检查库存是否充足
        if (stock == null || stock <= sold) {
            log.warn("库存不足，无法扣减: activityId={}, stock={}, sold={}", 
                    activity.getId(), stock, sold);
            return null;
        }
        
        // 扣减库存：已售数量加 1
        activity.setSold(sold + 1);
        Integer remainingStock = stock - sold - 1;
        
        log.info("扣减库存成功: activityId={}, stock={}, sold={} -> {}, remainingStock={}", 
                activity.getId(), stock, sold, sold + 1, remainingStock);
        return remainingStock;
    }
}

