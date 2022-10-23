package com.liu.xyz.gulimall.product.feign;

import com.liu.xyz.common.to.SkuHasStockVo;
import com.liu.xyz.common.utils.R;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

/**
 * create liu 2022-10-12
 */
@FeignClient("gulimall-ware")
public interface WareFeignService {

    @RequestMapping("ware/waresku/hastStock")
    public R<List<SkuHasStockVo>> hastStock(@RequestBody List<Long> skuId);
}
