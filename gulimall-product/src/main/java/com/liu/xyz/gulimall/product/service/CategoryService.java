package com.liu.xyz.gulimall.product.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.liu.xyz.common.utils.PageUtils;
import com.liu.xyz.gulimall.product.entity.CategoryEntity;
import com.liu.xyz.gulimall.product.web.vo.Catelog2Vo;

import java.util.List;
import java.util.Map;

/**
 * 商品三级分类
 *
 * @author chenshun
 * @email sunlightcs@gmail.com
 * @date 2022-09-29 22:18:42
 */
public interface CategoryService extends IService<CategoryEntity> {

    PageUtils queryPage(Map<String, Object> params);

    /**
     * 查询所有商品分类 及子分类，以树型结构展示
     * @return
     */
    List<CategoryEntity> listWithTree();

    /**
     * 更具id  删除
     * @param catIds
     */
    void removeByIdAll(Long[] catIds);

    /**
     * 根据id 查询完整 路径 如 [2,34,225] 对应父类
     * @param catelogId
     * @return
     */
    Long[] getByIdCatelogPath(Long catelogId);

    /**
     * 详细修改 ，保证其表的冗余字段可以
     * @param category
     */
    void updateByIdDeatil(CategoryEntity category);

    /*
     *  获取 parent_cid 为0的数据
     * @return
     */
    List<CategoryEntity> getLevelCategorys();
    /*
        获取
     */
    Map<String, List<Catelog2Vo>> getCatalogJson();
}

