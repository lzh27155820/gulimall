package com.liu.xyz.gulimall.ware;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * create liu 2022-09-30
 */
@MapperScan("com.liu.xyz.gulimall.ware.dao")
@SpringBootApplication

public class WareApplication {
    public static void main(String[] args) {
        SpringApplication.run(WareApplication.class,args);
    }
}
