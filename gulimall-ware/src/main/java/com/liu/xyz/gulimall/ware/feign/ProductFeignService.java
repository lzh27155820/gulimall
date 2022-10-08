package com.liu.xyz.gulimall.ware.feign;

import com.liu.xyz.common.utils.R;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * create liu 2022-10-07
 */
@FeignClient("gulimall-produect")
public interface ProductFeignService {

    @RequestMapping("product/skuinfo/info/{skuId}")
    public R info(@PathVariable("skuId") Long skuId);
}
