package com.liu.xyz.gulimall.product.vo;

import com.liu.xyz.gulimall.product.entity.AttrEntity;
import lombok.Data;

import java.util.List;

/**
 * create liu 2022-10-06
 */
@Data
public class AttrGroupWithAttrs {
    /**
     * 分组id
     */
    private Long attrGroupId;
    /**
     * 组名
     */
    private String attrGroupName;
    /**
     * 排序
     */
    private Integer sort;
    /**
     * 描述
     */
    private String descript;
    /**
     * 组图标
     */
    private String icon;
    /**
     * 所属分类id
     */
    private Long catelogId;
    /**
     * 关联属性
     */
    private List<AttrEntity> attrs;
}
