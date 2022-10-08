package com.liu.xyz.gulimall.product.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.liu.xyz.common.utils.PageUtils;
import com.liu.xyz.gulimall.product.entity.AttrEntity;
import com.liu.xyz.gulimall.product.entity.AttrGroupEntity;
import com.liu.xyz.gulimall.product.vo.AttrAndGroupID;
import com.liu.xyz.gulimall.product.vo.AttrGroupWithAttrs;

import java.util.List;
import java.util.Map;

/**
 * create liu 2022-10-05
 */
public interface AttrGroupService extends IService<AttrGroupEntity> {
    PageUtils queryPage(Map<String, Object> params);

    /**
     * 获取分类属性分组
     * @param params
     * @param catelogId
     * @return
     */
    PageUtils queryPage(Map<String, Object> params, Long catelogId);

    /**
     * 通过id 查询与 规格参数 联系
     * @param attrgroupId
     * @return
     */

    List<AttrEntity> selectListByAttrgroupId(Long attrgroupId);


    void removeId(AttrAndGroupID[] andGroupIDS);

    /**
     * 属性分组的 新增 查询其他没有关联的 商品属性
     * @param params
     * @param attrGroupId
     * @return
     */
    PageUtils getNoattrRelation(Map<String, Object> params, Long attrGroupId);

    List<AttrGroupWithAttrs> getAttrGroupWithAttrByCategoryId(Long categoryId);
}
