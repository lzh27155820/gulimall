package com.liu.xyz.gulimall.ware.vo;

import lombok.Data;

import java.util.List;

/**
 * create liu 2022-10-07
 */
@Data
public class MergeVo {
    /**
     * {
     *   purchaseId: 1, //整单id
     *   items:[1,2,3,4] //合并项集合
     * }
     */
    private Long purchaseId;
    private List<Long> items;
}
