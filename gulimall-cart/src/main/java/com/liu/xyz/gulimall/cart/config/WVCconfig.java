package com.liu.xyz.gulimall.cart.config;

import com.liu.xyz.gulimall.cart.interceptor.CartInterceptor;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * create liu 2022-11-02
 */
@Configuration
public class WVCconfig implements WebMvcConfigurer {

    @Override
    public void addInterceptors(InterceptorRegistry registry) {

        registry.addInterceptor(new CartInterceptor()).addPathPatterns("/**");
    }
}
