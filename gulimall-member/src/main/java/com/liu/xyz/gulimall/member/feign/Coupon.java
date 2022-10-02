package com.liu.xyz.gulimall.member.feign;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.Map;

/**
 * create liu 2022-09-30
 */

@FeignClient("gulimall-coupon")
public interface Coupon {

    @RequestMapping("/coupon/coupon/member/list")
    public Map<String,Object> getx();
}
