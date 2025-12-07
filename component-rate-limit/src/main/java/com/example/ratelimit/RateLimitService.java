package com.example.ratelimit;

/**
 * 限流服务接口
 * <p>
 * 提供限流功能，支持两种限流算法：
 * <ul>
 *   <li>令牌桶算法：适用于平滑限流，允许突发流量</li>
 *   <li>滑动窗口算法：适用于固定时间窗口内的请求数限制</li>
 * </ul>
 * </p>
 * <p>
 * 使用示例：
 * <pre>
 * // 注入限流服务
 * @Autowired
 * private RateLimitService rateLimitService;
 * 
 * // 使用令牌桶算法限流
 * boolean allowed = rateLimitService.tryAcquire("user:1", 1, 10, 100);
 * // 参数说明：key="user:1", permits=1, rate=10/秒, capacity=100
 * 
 * // 使用滑动窗口算法限流
 * boolean allowed = rateLimitService.slidingWindowLimit("api:login", 100, 60);
 * // 参数说明：key="api:login", limit=100次, windowSeconds=60秒
 * </pre>
 * </p>
 * 
 * @author system
 * @version 1.0.0
 * @since 1.0.0
 */
public interface RateLimitService {
    
    /**
     * 尝试获取令牌（令牌桶算法）
     * <p>
     * 令牌桶算法原理：
     * <ul>
     *   <li>系统以固定速率生成令牌并放入桶中</li>
     *   <li>请求需要获取令牌才能通过</li>
     *   <li>如果桶中有足够的令牌，请求通过并消耗令牌</li>
     *   <li>如果桶中没有足够的令牌，请求被限流</li>
     *   <li>桶的容量限制了突发流量的上限</li>
     * </ul>
     * </p>
     * <p>
     * 适用场景：
     * <ul>
     *   <li>需要平滑限流，允许突发流量</li>
     *   <li>需要控制平均速率和峰值速率</li>
     * </ul>
     * </p>
     * 
     * @param key 限流键，用于区分不同的限流对象（如用户ID、IP地址等）
     * @param permits 需要获取的令牌数量，通常为 1
     * @param rate 令牌生成速率（每秒生成的令牌数）
     * @param capacity 令牌桶容量（最大令牌数）
     * @return true 表示获取成功，请求可以通过；false 表示获取失败，请求被限流
     */
    boolean tryAcquire(String key, int permits, int rate, int capacity);

    /**
     * 滑动窗口限流
     * <p>
     * 滑动窗口算法原理：
     * <ul>
     *   <li>将时间划分为多个时间窗口</li>
     *   <li>统计每个时间窗口内的请求数</li>
     *   <li>如果当前窗口内的请求数超过限制，则限流</li>
     *   <li>窗口会随着时间滑动，自动清理过期数据</li>
     * </ul>
     * </p>
     * <p>
     * 适用场景：
     * <ul>
     *   <li>需要限制固定时间窗口内的请求数</li>
     *   <li>API 接口限流</li>
     *   <li>用户行为限流（如登录、注册等）</li>
     * </ul>
     * </p>
     * 
     * @param key 限流键，用于区分不同的限流对象
     * @param limit 时间窗口内的最大请求数
     * @param windowSeconds 时间窗口大小（秒）
     * @return true 表示请求可以通过；false 表示请求被限流
     */
    boolean slidingWindowLimit(String key, int limit, int windowSeconds);
}

