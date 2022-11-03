package com.liu.xyz.gulimall.product.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.liu.xyz.common.utils.PageUtils;
import com.liu.xyz.gulimall.product.entity.SkuInfoEntity;
import com.liu.xyz.gulimall.product.web.vo.SkuItemVo;

import java.util.Map;

/**
 * sku信息
 *
 * @author chenshun
 * @email sunlightcs@gmail.com
 * @date 2022-09-29 22:18:42
 */
public interface SkuInfoService extends IService<SkuInfoEntity> {

    PageUtils queryPage(Map<String, Object> params);

    void saveSkuInfo(SkuInfoEntity skuInfoEntity);

    /**
     * 通过skuid 查询每件商品的详情
     * @return
     */
    SkuItemVo searchToskuId(Long skuId);
}

