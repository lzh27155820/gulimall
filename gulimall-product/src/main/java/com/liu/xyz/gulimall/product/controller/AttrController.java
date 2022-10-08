package com.liu.xyz.gulimall.product.controller;

import com.liu.xyz.common.utils.PageUtils;
import com.liu.xyz.common.utils.R;
import com.liu.xyz.gulimall.product.entity.ProductAttrValueEntity;
import com.liu.xyz.gulimall.product.service.AttrService;
import com.liu.xyz.gulimall.product.service.ProductAttrValueService;
import com.liu.xyz.gulimall.product.vo.AttrRespVO;
import com.liu.xyz.gulimall.product.vo.AttrVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.List;
import java.util.Map;



/**
 * 商品属性
 *
 * @author chenshun
 * @email sunlightcs@gmail.com
 * @date 2022-09-29 22:18:42
 */
@RestController
@RequestMapping("product/attr")
public class AttrController {
    @Autowired
    private AttrService attrService;

    @Autowired
    private ProductAttrValueService productAttrValueService;

    /**
     * 23、获取sku规格
     * /product/attr/base/listforspu/{spuId}
     */
    @RequestMapping("base/listforspu/{spuId}")
    public R baseAttrListforspu(@PathVariable("spuId") Long spuId){

        List<ProductAttrValueEntity> list=productAttrValueService.baseAttrListforspu(spuId);
        return R.ok().put("data",list);
    }

    /**
     * 销售属性
     * @param params
     * @param catgoryId
     * @param attrType
     * @return
     */
    @RequestMapping("{attrType}/list/{catgoryId}" )
    public R BaseList(@RequestParam Map<String, Object> params,
                      @PathVariable("catgoryId") Long catgoryId,
                      @PathVariable("attrType") String attrType){
        PageUtils page = attrService.select(params,catgoryId,attrType);
        return R.ok().put("page",page);
    }
    /**
     * 列表
     */
    @RequestMapping("/list")
    public R list(@RequestParam Map<String, Object> params){
        PageUtils page = attrService.queryPage(params);

        return R.ok().put("page", page);
    }


    /**
     * 信息
     */
    @RequestMapping("/info/{attrId}")
    public R info(@PathVariable("attrId") Long attrId){
	//	AttrEntity attr = attrService.getById(attrId);
        AttrRespVO attr = attrService.getByIdAttrRespVO(attrId);
        return R.ok().put("attr", attr);
    }

    /**
     * 保存
     */
    @RequestMapping("/save")
    public R save(@RequestBody AttrVO attr){
		attrService.saveAttr(attr);

        return R.ok();
    }

    /**
     * 修改
     */
    @RequestMapping("/update")
    public R update(@RequestBody AttrVO attr){
		attrService.updateAttr(attr);

        return R.ok();
    }

    /**
     * 删除
     */
    @RequestMapping("/delete")
    public R delete(@RequestBody Long[] attrIds){
		attrService.removeByIds(Arrays.asList(attrIds));

        return R.ok();
    }

}
