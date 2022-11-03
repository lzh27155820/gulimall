package com.liu.xyz.feign;

import com.liu.xyz.common.utils.R;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * create liu 2022-10-27
 */
@FeignClient("gulimall-third-party")
public interface ThirdPartyFeignService {
    @GetMapping(value = "/sms/sendCode")
    public R sendCode(@RequestParam("phone") String phone, @RequestParam("code") String code);
}
