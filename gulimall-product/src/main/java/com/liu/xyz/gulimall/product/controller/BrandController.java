package com.liu.xyz.gulimall.product.controller;


import com.liu.xyz.common.utils.PageUtils;
import com.liu.xyz.common.utils.R;
import com.liu.xyz.gulimall.product.entity.BrandEntity;
import com.liu.xyz.gulimall.product.service.BrandService;
import com.liu.xyz.gulimall.product.valid.AddGroup;
import com.liu.xyz.gulimall.product.valid.UpdateGroup;
import com.liu.xyz.gulimall.product.valid.UpdateStatus;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.Map;

/**
 * 品牌
 *
 * @author chenshun
 * @email sunlightcs@gmail.com
 * @date 2022-09-29 22:18:42
 */
@Slf4j
@RestController
@RequestMapping("product/brand")
public class BrandController {
    @Autowired
    private BrandService brandService;

    /**
     * 列表
     */
    @RequestMapping("/list")
    public R list(@RequestParam Map<String, Object> params){
        PageUtils page = brandService.queryPage(params);

        return R.ok().put("page", page);
    }


    /**
     * 信息
     */
    @RequestMapping("/info/{brandId}")
    public R info(@PathVariable("brandId") Long brandId){
		BrandEntity brand = brandService.getById(brandId);

        return R.ok().put("brand", brand);
    }

    /**
     * 保存
     */
    @RequestMapping("/save")
    public  R save(@Validated({AddGroup.class}) @RequestBody BrandEntity brand){


		brandService.save(brand);

        return R.ok();
    }

    /**
     * 修改
     */
    @RequestMapping("/update")
    public R update(@Validated({UpdateGroup.class}) @RequestBody BrandEntity brand){
        log.info("修的值"+brand);
		//brandService.updateById(brand);
        brandService.updateByIdDeatil(brand);
        return R.ok();
    }
    /**
     * 修改
     */
    @RequestMapping("/update/status")
    public R updateStatus(@Validated({UpdateStatus.class}) @RequestBody BrandEntity brand){
        log.info("修的值"+brand);
        //brandService.updateById(brand);

        brandService.updateByIdDeatil(brand);
        return R.ok();
    }


    /**
     * 删除
     */
    @RequestMapping("/delete")
    public R delete(@RequestBody Long[] brandIds){
		brandService.removeByIds(Arrays.asList(brandIds));

        return R.ok();
    }

}
