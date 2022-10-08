package com.liu.xyz.common.to;

import lombok.Data;

import java.math.BigDecimal;

/**
 * create liu 2022-10-06
 */
@Data
public class SpuBoundsTo {
    private Long spuId;
    private BigDecimal buyBounds;
    private BigDecimal growBounds;
}
