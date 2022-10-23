package com.liu.xyz.gulimall.product.dao;

import com.liu.xyz.gulimall.product.entity.AttrEntity;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 商品属性
 * 
 * @author chenshun
 * @email sunlightcs@gmail.com
 * @date 2022-09-29 22:18:42
 */
@Mapper
public interface AttrDao extends BaseMapper<AttrEntity> {

    List<Long> getSearchAttrIds(@Param("attrIds") List<Long> attrIds);
}
