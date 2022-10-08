package com.liu.xyz.gulimall.ware.config;

import com.baomidou.mybatisplus.extension.plugins.inner.PaginationInnerInterceptor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * create liu 2022-10-04
 */
@Configuration
public class MybatisPlusConfig {

    @Bean
    public PaginationInnerInterceptor paginationInnerInterceptor(){
        PaginationInnerInterceptor interceptor = new PaginationInnerInterceptor();
        //如果请求页面大于最大页 就返回首页
        interceptor.setOverflow(true);

        return interceptor;
    }
}
