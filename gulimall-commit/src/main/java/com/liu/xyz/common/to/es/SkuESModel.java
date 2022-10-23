package com.liu.xyz.common.to.es;

import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

/***
 *  要和es中的索引字段名和类型对应
 * create liu 2022-10-12
 */
@Data
public class SkuESModel {

    private Long spuId;

    private Long skuId;

    private String skuTitle;

    private BigDecimal skuPrice;

    private String skuImg;

    private Long saleCount;

    private Boolean hastStock;

    private Long hotScore;

    private Long brandId;

    private Long catalogId;

    private String brandName;

    private String brandImg;

    private String catalogName;

    private List<Attrs> attrs;

    @Data
   public static  class Attrs{
        private Long attrId;
        private String attrName;
        private String attrValue;
    }

}
