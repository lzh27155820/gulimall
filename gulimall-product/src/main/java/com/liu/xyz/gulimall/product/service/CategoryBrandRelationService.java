package com.liu.xyz.gulimall.product.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.liu.xyz.common.utils.PageUtils;
import com.liu.xyz.gulimall.product.entity.BrandEntity;
import com.liu.xyz.gulimall.product.entity.CategoryBrandRelationEntity;

import java.util.List;
import java.util.Map;

/**
 * 品牌分类关联
 *
 * @author chenshun
 * @email sunlightcs@gmail.com
 * @date 2022-09-29 22:18:42
 */
public interface CategoryBrandRelationService extends IService<CategoryBrandRelationEntity> {

    PageUtils queryPage(Map<String, Object> params);

    /**
     *  保存全部信息 包含冗余字段
     * @param categoryBrandRelation
     */
    void saveDetali(CategoryBrandRelationEntity categoryBrandRelation);

    /**
     * 修改 冗余字段的catId
     * @param brandId
     * @param name
     */
    void updateBrand(Long brandId, String name);

    /**
     * 修改 冗余字段的catId
     * @param catId
     * @param name
     */
    void updateCategory(Long catId, String name);

    /**
     * 根据 三级分类id 查询品牌 id
     * @param catId
     * @return
     */
    List<BrandEntity> getBrandsByCategoryId(Long catId);
}

