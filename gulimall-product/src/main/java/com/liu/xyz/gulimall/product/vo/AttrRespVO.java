package com.liu.xyz.gulimall.product.vo;

import lombok.Data;

/**
 * create liu 2022-10-04
 */
@Data
public class AttrRespVO extends AttrVO{

    /**
     * "catelogName": "手机/数码/手机", //所属分类名字
     * 			"groupName": "主体", //所属分组名字
     */
    private String catelogName;
    private String groupName;

    /**
     * 完整 catgrory路径
     */
    private Long[] catelogPath;
}
