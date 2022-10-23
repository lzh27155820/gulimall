package com.liu.xyz.gulimall.product.config;

import org.redisson.Redisson;
import org.redisson.api.RedissonClient;
import org.redisson.config.Config;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * create liu 2022-10-18
 */
@Configuration
public class Myredisson {

    @Bean
    public RedissonClient redissonClient(){

        Config config = new Config();
        config.useSingleServer().setAddress("redis://192.168.229.151:6379");

        return Redisson.create(config);
    }
}
