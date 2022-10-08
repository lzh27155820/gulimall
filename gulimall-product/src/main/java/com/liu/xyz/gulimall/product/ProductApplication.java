package com.liu.xyz.gulimall.product;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

/***
 *  数据校验在需要在mvc的controller的身上加上一个
 *
 * create liu 2022-09-29
 */
//@MapperScan("com.liu.xyz.gulimall.product.dao")
@EnableFeignClients(basePackages = "com.liu.xyz.gulimall.product.feign")
@SpringBootApplication
public class ProductApplication {

    public static void main(String[] args) {
        SpringApplication.run(ProductApplication.class,args);
    }
}
