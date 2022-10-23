package com.liu.xyz.gulimall.product.feign;

import com.liu.xyz.common.to.es.SkuESModel;
import com.liu.xyz.common.utils.R;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

/**
 * create liu 2022-10-12
 */
@FeignClient("guilimall-search")
public interface SearchFeignSerive {

    @RequestMapping("search/product")
    public R get(@RequestBody List<SkuESModel> skuESModels);
}
