package com.liu.xyz.gulimall.cart.to;

import lombok.Data;
import lombok.ToString;


@ToString
@Data
public class UserInfoTo {
    /**
     * 用户id
     */
    private Long userId;
    /**
     *  零时用乎
      */
    private String userKey;

    /**
     * 是否临时用户
     */
    private Boolean tempUser = false;

}
