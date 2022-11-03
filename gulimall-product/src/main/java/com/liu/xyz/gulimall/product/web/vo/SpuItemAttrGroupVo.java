package com.liu.xyz.gulimall.product.web.vo;

import com.liu.xyz.gulimall.product.vo.Attr;
import lombok.Data;
import lombok.ToString;

import java.util.List;

/**
 * create liu 2022-10-26
 */
@ToString
@Data
public class SpuItemAttrGroupVo {


    private String groupName;

    private List<Attr> attrs;
}
