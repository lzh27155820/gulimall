package com.liu.xyz.gulimall.cart.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * create liu 2022-10-27
 */
@Data
@ConfigurationProperties(prefix = "gulimall.thread")
@Component
public class ThreadConfig {
    private Integer coreSize;

    private Integer maxSize;

    private Integer keepAliveTime;

}
