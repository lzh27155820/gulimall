package com.liu.xyz.gulimall.product;

import org.junit.jupiter.api.Test;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

/**
 * create liu 2022-10-18
 */
@SpringBootTest
public class Myredisson {

    @Autowired
    private RedissonClient redissonClient;
    @Test
    public void test(){
        System.out.println(redissonClient);
    }
}
