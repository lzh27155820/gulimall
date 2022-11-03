package com.liu.xyz;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.session.data.redis.config.annotation.web.http.EnableRedisHttpSession;

/**
 *
 *  session 的工作原理
 *      在服务端保存session之后就会命令浏览器保存到cook中(cook保存的是key，服务端的session保存的本身就是map),它的作用域就是服务器本身
 *   这样的方式在单体可以，在分布式的情况下会出现
 *      1.服务与服务之间不能共享session，
 *          把session存储在redis中
 *      2.负载均衡情况下会出现不一致问题
 *          redis解决
 * create liu 2022-10-27
 */

@EnableRedisHttpSession
@EnableFeignClients(basePackages = "com.liu.xyz.feign")
@SpringBootApplication
public class AuthServerApplication {
    public static void main(String[] args) {
        SpringApplication.run(AuthServerApplication.class,args);
    }
}
