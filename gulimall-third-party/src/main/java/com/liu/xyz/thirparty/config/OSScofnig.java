package com.liu.xyz.thirparty.config;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/*
 *@date 2022/7/9-15:18
 */
@Component
@ConfigurationProperties(prefix = "aliyun.oss")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class OSScofnig {


    private String endPoint;
    private String bucketName;
    private String accessKeyId;
    private String accessKeySecret;
    private String bucketDomain;
}
