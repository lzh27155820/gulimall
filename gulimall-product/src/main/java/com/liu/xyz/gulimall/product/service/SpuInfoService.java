package com.liu.xyz.gulimall.product.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.liu.xyz.common.utils.PageUtils;
import com.liu.xyz.gulimall.product.entity.SpuInfoEntity;
import com.liu.xyz.gulimall.product.vo.SpuSaveVo;

import java.util.Map;

/**
 * spu信息
 *
 * @author chenshun
 * @email sunlightcs@gmail.com
 * @date 2022-09-29 22:18:42
 */
public interface SpuInfoService extends IService<SpuInfoEntity> {

    PageUtils queryPage(Map<String, Object> params);

    /**
     * 保存商品发布
     * @param spuInfo
     */
    void saveSpuSaveVo(SpuSaveVo spuInfo);

    void saveBaseSpuInfo(SpuInfoEntity spuInfoEntity);


    void up(Long spuId);
}

