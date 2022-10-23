package com.liu.xyz.search.feign;

import com.liu.xyz.common.utils.R;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * create liu 2022-10-22
 */
@FeignClient("gulimall-produect")
public interface ProductFeignService {

    @RequestMapping("/product/attr/info/{attrId}")
    public R attrInfo(@PathVariable("attrId") Long attrId);
}
