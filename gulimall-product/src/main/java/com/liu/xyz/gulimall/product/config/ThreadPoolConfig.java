package com.liu.xyz.gulimall.product.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.*;

/**
 * create liu 2022-10-27
 */
@Configuration
public class ThreadPoolConfig {

    @Bean
    public ThreadPoolExecutor threadPoolExecutor(ThreadConfig pool){

        /**
         * 参数
         *  1.corePoolSize :当前线程池的活动线程数量 ，会一直存在
         *  2.maximumPoolSize: 线程池最大线程数
         *  3.keepAliveTime: 线程存活时间，释放maximumPoolSize-corePoolSize的线程
         *  4.unit: 存活时间单位
         *  5.workQueue: 阻塞队列，如果有很多线程任务就会存储在队列中，等corePoolSize中的线程取，
         *              如果超过队列长度了就开启线程(maximumPoolSize),
         *  6.threadFactory: 线程工厂
         *  7.handler: 如果队列满了,线程次中的线程都有在执行，在进来 我们可以指端策略拒绝执行任务
         */
       return  new ThreadPoolExecutor(pool.getCoreSize(),pool.getMaxSize(),
               pool.getKeepAliveTime(),TimeUnit.SECONDS,
               new LinkedBlockingDeque<>(1000),Executors.defaultThreadFactory(),
               new ThreadPoolExecutor.AbortPolicy());
    }
}
