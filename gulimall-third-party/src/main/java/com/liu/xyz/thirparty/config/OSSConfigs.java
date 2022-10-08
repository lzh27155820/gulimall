package com.liu.xyz.thirparty.config;

import com.aliyun.oss.OSSClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * create liu 2022-10-03
 */
@Configuration
public class OSSConfigs {

    @Autowired
    private OSScofnig osScofnig;
    @Bean
    public OSSClient ossClient()
    {
        return new OSSClient(osScofnig.getEndPoint(),
                osScofnig.getAccessKeyId(),
                osScofnig.getAccessKeySecret());
    }


}
