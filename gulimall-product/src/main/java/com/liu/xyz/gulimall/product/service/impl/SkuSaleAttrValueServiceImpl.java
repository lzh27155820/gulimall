package com.liu.xyz.gulimall.product.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.liu.xyz.common.utils.PageUtils;
import com.liu.xyz.common.utils.Query;
import com.liu.xyz.gulimall.product.dao.SkuSaleAttrValueDao;
import com.liu.xyz.gulimall.product.entity.SkuSaleAttrValueEntity;
import com.liu.xyz.gulimall.product.service.SkuSaleAttrValueService;
import com.liu.xyz.gulimall.product.web.vo.SkuItemSaleAttrVo;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;


@Service("skuSaleAttrValueService")
public class SkuSaleAttrValueServiceImpl extends ServiceImpl<SkuSaleAttrValueDao, SkuSaleAttrValueEntity> implements SkuSaleAttrValueService {

    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        IPage<SkuSaleAttrValueEntity> page = this.page(
                new Query<SkuSaleAttrValueEntity>().getPage(params),
                new QueryWrapper<SkuSaleAttrValueEntity>()
        );

        return new PageUtils(page);
    }

    @Override
    public List<SkuItemSaleAttrVo> listSkuSaleAttrValue(Long spuId) {

       return   this.baseMapper.listSkuSaleAttrValue(spuId);
    }

    @Override
    public List<String> stringList(Long skuId) {

        return    this.baseMapper.stringList(skuId);
    }

}