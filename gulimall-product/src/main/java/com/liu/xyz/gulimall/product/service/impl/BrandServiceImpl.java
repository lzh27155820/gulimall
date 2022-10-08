package com.liu.xyz.gulimall.product.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.liu.xyz.common.utils.PageUtils;
import com.liu.xyz.common.utils.Query;
import com.liu.xyz.gulimall.product.dao.BrandDao;
import com.liu.xyz.gulimall.product.entity.BrandEntity;
import com.liu.xyz.gulimall.product.service.BrandService;
import com.liu.xyz.gulimall.product.service.CategoryBrandRelationService;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Map;


@Service("brandService")
public class BrandServiceImpl extends ServiceImpl<BrandDao, BrandEntity> implements BrandService {

    @Autowired
    private CategoryBrandRelationService categoryBrandRelationService;
    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        String key =(String) params.get("key");
        if(StringUtils.isEmpty(key)){
            IPage<BrandEntity> page = this.page(
                    new Query<BrandEntity>().getPage(params),
                    new QueryWrapper<BrandEntity>()
            );   return new PageUtils(page);
        }

        QueryWrapper<BrandEntity> wrapper = new QueryWrapper<>();

        wrapper.eq("brand_id",key).or().like("name",key);

        return new PageUtils(this.page(new Query<BrandEntity>().getPage(params),wrapper));
    }

    /**
     *
     * @param brand
     */
    @Transactional
    @Override
    public void updateByIdDeatil(BrandEntity brand) {
        baseMapper.updateById(brand);
        if(!StringUtils.isEmpty(brand.getName())){
            categoryBrandRelationService.updateBrand(brand.getBrandId(),brand.getName());
        }
        //TODo  后
    }

}