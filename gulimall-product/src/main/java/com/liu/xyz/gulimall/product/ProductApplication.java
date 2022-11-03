package com.liu.xyz.gulimall.product;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.session.data.redis.config.annotation.web.http.EnableRedisHttpSession;

/***
 *  数据校验在需要在mvc的controller的身上加上一个
 *  1.使用 springboot2.7
 *  2.使用 springcloud2021    微服务
 *      gateway   网关
 *      openfeign 远程调用
 *          loadbalancer 负载均衡
 *      nacos     配置中心和注册中心
 *  3.使用redis 6.0.8         做缓存
 *  4.使用elasticsearch 7.4   检索
 *  5.使用fastJson2           对json操作
 *  6.使用oss                 存储资源
 *  7.使用druid               做数据源
 *  8.使用devtoos             重启服务
 *  9.使用nginx               服务代理
 *  10.使用validation         做数据校验
 *  11.使用mybaits-plus       对数据库操作
 *  12.使用thymeleaf          模板解析
 *  13 使用redisson           解决分布式锁
 *  14 使用spring-cache       整合redis缓存
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *  spring-cache
 *          能够解决
 *              穿透   cache-null-values: true #可以缓存为null
 *              雪崩    time-to-live: 3600000 #单位毫秒级别 把cache缓存的数据设置过期时间
 *              击穿    sync = true 只能解决本地锁，不能解决分布式锁
 * create liu 2022-09-29
 */

@EnableRedisHttpSession
@EnableCaching //开启cache缓存
@EnableFeignClients(basePackages = "com.liu.xyz.gulimall.product.feign")
@SpringBootApplication
public class ProductApplication {

    public static void main(String[] args) {
        SpringApplication.run(ProductApplication.class,args);
    }
}
