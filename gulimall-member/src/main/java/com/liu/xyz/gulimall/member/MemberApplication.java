package com.liu.xyz.gulimall.member;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

/**
 * create liu 2022-09-30
 */
@SpringBootApplication
@EnableFeignClients(basePackages = "com.liu.xyz.gulimall.member.feign")
@MapperScan("com.liu.xyz.gulimall.member.dao")
public class MemberApplication {

    public static void main(String[] args) {
        SpringApplication.run(MemberApplication.class,args);
    }
}
