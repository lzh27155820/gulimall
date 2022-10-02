package com.liu.xyz.gulimall.coupon;

import com.liu.xyz.gulimall.coupon.dao.CouponDao;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class GulimallCouponApplicationTests {

    @Autowired
    private CouponDao couponDao;
    @Test
    void contextLoads() {
        System.out.println(couponDao);
    }

}
