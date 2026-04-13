package com.example.springcloud.service;

import com.example.springcloud.mapper.ProductMapper;
import com.example.springcloud.mapper.UserMapper;
import jakarta.validation.constraints.NotNull;
import model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.data.redis.RedisProperties;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ZSetOperations;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Set;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private ProductMapper productMapper;

    public void doCreateThread(){
        //Java 8 Lambda 写法
        Thread thread = new Thread(() -> System.out.println("线程正在运行"));
    }

    //线程池创建
    @NotNull
    private static ThreadPoolExecutor getThreadPoolExecutor(int num) {
        //核心线程数:取5和传入参数的最小值，确保不超过5个核心线程
        int coreSize = Math.min(5, num);
        //最大线程数:线程池最多可以创建15个线程
        int maximumPoolSize = 15;
        //线程存活时间 = 0毫秒
        long keepAliveTime = 0L;
        TimeUnit unit = TimeUnit.MILLISECONDS;
        //默认容量为Integer.MAX_VALUE的无界队列
        BlockingQueue<Runnable> workQueue = new LinkedBlockingQueue<>();
        ThreadPoolExecutor executor = new ThreadPoolExecutor(
                coreSize,
                maximumPoolSize,
                keepAliveTime,
                unit,
                workQueue
        );
        //拒绝策略：当线程池饱和时，由提交任务的线程自己执行该任务,这是一种"慢速"降级策略，会阻塞任务提交者
        executor.setRejectedExecutionHandler(new ThreadPoolExecutor.CallerRunsPolicy());
        return executor;
    }


    /**
     * 从缓存+DB获取用户(用户信息缓存)
     * 热点数据缓存
     * 作用：减轻数据库压力，提高接口速度
     */
    public User getUserById(Long userId) {
        String key = "user:info:" + userId;

        // 1. 先查Redis
        User user = (User) redisTemplate.opsForValue().get(key);

        //缓存空值，防止穿透
        //缓存穿透（查不存在的数据，直接打库）解决方案：缓存空值 + 布隆过滤器
        if (user == null) {
            redisTemplate.opsForValue().set(key, null, 60, TimeUnit.SECONDS);
            return null;
        }

        if (user != null) {
            return user;
        }

        // 2. Redis没有，查数据库
        user = userMapper.selectById(userId);

        // 3. 存入Redis，设置30分钟过期
        if (user != null) {
            redisTemplate.opsForValue().set(key, user, 30, TimeUnit.MINUTES);
        }
        return user;
    }

    /**
     * 更新用户时，同步更新/删除缓存
     * @param user
     */
    public void updateUser(User user) {
        userMapper.updateById(user);
        String key = "user:info:" + user.getId();
        redisTemplate.delete(key); // 缓存失效
    }


    /**
     * 扣减库存：加锁保证安全
     * @description 分布式锁（秒杀、订单、库存必须用）
     * 作用：集群环境下防止并发超卖、重复提交
     * @param productId
     * @return
     */
    public boolean deductStock(Long productId) {
        String lockKey = "lock:product:" + productId;
        // 锁超时时间，防止死锁
        long expire = 10;

        try {
            // SETNX 加锁
            Boolean lock = redisTemplate.opsForValue()
                    .setIfAbsent(lockKey, "locked", expire, TimeUnit.SECONDS);

            if (Boolean.FALSE.equals(lock)) {
                System.out.println("获取锁失败，请求稍后重试");
                return false;
            }

            // 业务逻辑：扣减库存
            int stock = productMapper.getStock(productId);
            if (stock <= 0) {
                return false;
            }
            productMapper.deductStock(productId);
            return true;

        } finally {
            // 释放锁
            redisTemplate.delete(lockKey);
        }
    }

    /**
     * 限制用户1分钟最多请求10次（防止恶意请求、压垮服务）
     * @description 限制 1 分钟内最多访问多少次
     * @param userId
     * @return
     */
    public boolean checkRateLimit(String userId) {
        String key = "limit:user:" + userId;
        // 自增计数器
        Long count = redisTemplate.opsForValue().increment(key, 1);

        // 第一次设置过期时间
        if (count != null && count == 1) {
            redisTemplate.expire(key, 60, TimeUnit.SECONDS);
        }

        // 超过10次拒绝
        return count == null || count <= 10;
    }


    /**
     * 生成订单号：时间戳 + 自增序列
     * @return
     */
    public String generateOrderNo() {
        String date = LocalDate.now().format(DateTimeFormatter.BASIC_ISO_DATE);
        String key = "order:seq:" + date;

        // 每天从1开始自增
        Long seq = redisTemplate.opsForValue().increment(key, 1);
        if (seq == 1) {
            redisTemplate.expire(key, 1, TimeUnit.DAYS);
        }

        return date + String.format("%06d", seq);
    }


    /**
     * 添加用户积分
     * 排行榜（ZSet 最经典用法）
     */
    public void addScore(String userId, int score) {
        String key = "rank:score";
        redisTemplate.opsForZSet().add(key, userId, score);
    }

    /**
     * 获取前10名排行榜
     * 排行榜（ZSet 最经典用法）
     * @return
     */
    public Set<ZSetOperations.TypedTuple<Object>> getTop10() {
        String key = "rank:score";
        return redisTemplate.opsForZSet()
                .reverseRangeWithScores(key, 0, 9);
    }


}
