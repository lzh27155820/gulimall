package com.liu.xyz.common.to;

import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

/**
 * create liu 2022-10-06
 */
@Data
public class SkuReductionTo {
    private Long skuId;
    private int fullCount;
    private BigDecimal discount;
    private int countStatus;
    private BigDecimal fullPrice;
    private BigDecimal reducePrice;
    private int priceStatus;
    private List<MemberPrice> memberPrice;
}
