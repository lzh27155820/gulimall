package com.liu.xyz.thirparty.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * create liu 2022-10-27
 */
@ConfigurationProperties(prefix = "aliyun.sms")
@Component
@Data
public class SMSConfig {

    private String host;
    private String path;
    private String method;
    private String appcode;
}
