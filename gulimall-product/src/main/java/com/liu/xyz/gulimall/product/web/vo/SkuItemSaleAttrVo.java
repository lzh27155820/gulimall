package com.liu.xyz.gulimall.product.web.vo;

import lombok.Data;
import lombok.ToString;

import java.util.List;

/**
 * create liu 2022-10-26
 */
@ToString
@Data
public class SkuItemSaleAttrVo {
    private Long attrId;

    private String attrName;

    private List<AttrValueWithSkuIdVo> attrValues;
}
