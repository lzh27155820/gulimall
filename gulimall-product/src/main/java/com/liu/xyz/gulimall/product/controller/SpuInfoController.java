package com.liu.xyz.gulimall.product.controller;

import com.liu.xyz.common.utils.PageUtils;
import com.liu.xyz.common.utils.R;
import com.liu.xyz.gulimall.product.entity.SpuInfoEntity;
import com.liu.xyz.gulimall.product.service.SpuInfoService;
import com.liu.xyz.gulimall.product.vo.SpuSaveVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.Map;



/**
 * spu信息
 *
 * @author chenshun
 * @email sunlightcs@gmail.com
 * @date 2022-09-29 22:18:42
 */
@RestController
@RequestMapping("product/spuinfo")
public class SpuInfoController {
    @Autowired
    private SpuInfoService spuInfoService;


    /**
     * 商家上架 http://localhost:88/api/product/spuinfo/5/up
     */
    @RequestMapping("/{spuId}/up")
    public R spuUp(@PathVariable("spuId") Long spuId){

        spuInfoService.up(spuId);
        return R.ok();
    }
    /**
     * 列表
     */
    @RequestMapping("/list")
    public R list(@RequestParam Map<String, Object> params){
        PageUtils page = spuInfoService.queryPage(params);

        return R.ok().put("page", page);
    }


    /**
     * 信息
     */
    @RequestMapping("/info/{id}")
    public R info(@PathVariable("id") Long id){
		SpuInfoEntity spuInfo = spuInfoService.getById(id);

        return R.ok().put("spuInfo", spuInfo);
    }

    /**
     * 保存
     */
    @RequestMapping("/save")
    public R save(@RequestBody SpuSaveVo spuInfo){
		//spuInfoService.save(spuInfo);
        spuInfoService.saveSpuSaveVo(spuInfo);
        return R.ok();
    }

    /**
     * 修改
     */
    @RequestMapping("/update")
    public R update(@RequestBody SpuInfoEntity spuInfo){
		spuInfoService.updateById(spuInfo);

        return R.ok();
    }

    /**
     * 删除
     */
    @RequestMapping("/delete")
    public R delete(@RequestBody Long[] ids){
		spuInfoService.removeByIds(Arrays.asList(ids));

        return R.ok();
    }

}
