package com.liu.xyz.gulimall.product.feign;

import com.liu.xyz.common.to.SkuReductionTo;
import com.liu.xyz.common.to.SpuBoundsTo;
import com.liu.xyz.common.utils.R;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

/**
 * create liu 2022-10-06
 */
@FeignClient("gulimall-coupon")
public interface CouponFeignService {
    @PostMapping("coupon/spubounds/save")
    R saveSpuBouds(@RequestBody SpuBoundsTo spuBoundsTo);
    @PostMapping("coupon/skufullreduction/saveinfo")
    R saveSkuRedution(@RequestBody SkuReductionTo skuReductionTo);
}
