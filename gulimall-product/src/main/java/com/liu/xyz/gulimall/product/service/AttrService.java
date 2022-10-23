package com.liu.xyz.gulimall.product.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.liu.xyz.common.utils.PageUtils;
import com.liu.xyz.gulimall.product.entity.AttrEntity;
import com.liu.xyz.gulimall.product.vo.AttrRespVO;
import com.liu.xyz.gulimall.product.vo.AttrVO;

import java.util.List;
import java.util.Map;

/**
 * 商品属性
 *
 * @author chenshun
 * @email sunlightcs@gmail.com
 * @date 2022-09-29 22:18:42
 */
public interface AttrService extends IService<AttrEntity> {

    PageUtils queryPage(Map<String, Object> params);

    /**
     *  保存对象 ,及对应的关联关系
     * @param attr
     */
    void saveAttr(AttrVO attr);

    /**
     *  分页查询，带key 和catgoryId 的属性
     * @param params
     * @param catgoryId
     * @return
     */
    PageUtils select(Map<String, Object> params, Long catgoryId,String attrType);

    /**
     * 更具id 查询属性详情
     * @param attrId
     * @return
     */
    AttrRespVO getByIdAttrRespVO(Long attrId);

    /**
     * 修改
     * @param attr
     */
    void updateAttr(AttrVO attr);

    /**
     *
     * @param attrIds
     * @return
     */
    List<Long> getSearchAttrIds(List<Long> attrIds);
}

