package com.example.springcloud.userservice;

import jakarta.validation.constraints.NotNull;
import org.springframework.stereotype.Service;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

@Service
public class UserServiceImpl implements UserService{


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

}
