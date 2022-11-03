package com.liu.xyz.gulimall.product.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.liu.xyz.common.utils.PageUtils;
import com.liu.xyz.gulimall.product.entity.SkuSaleAttrValueEntity;
import com.liu.xyz.gulimall.product.web.vo.SkuItemSaleAttrVo;

import java.util.List;
import java.util.Map;

/**
 * sku销售属性&值
 *
 * @author chenshun
 * @email sunlightcs@gmail.com
 * @date 2022-09-29 22:18:42
 */
public interface SkuSaleAttrValueService extends IService<SkuSaleAttrValueEntity> {

    PageUtils queryPage(Map<String, Object> params);

    /**
     * 重点
     * 查询skuId 查询对应的销售属性
     * @param
     * @return
     */
    List<SkuItemSaleAttrVo> listSkuSaleAttrValue(Long spuId);

    /**
     *
     * @param skuId 根据skuid获取对应的销售属性值和名
     * @return
     */
    List<String> stringList(Long skuId);
}

