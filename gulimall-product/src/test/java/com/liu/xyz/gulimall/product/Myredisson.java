package com.liu.xyz.gulimall.product;

import com.liu.xyz.gulimall.product.dao.AttrGroupDao;
import com.liu.xyz.gulimall.product.web.vo.SpuItemAttrGroupVo;
import org.junit.jupiter.api.Test;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

/**
 * create liu 2022-10-18
 */
@SpringBootTest
public class Myredisson {

    @Autowired
    private RedissonClient redissonClient;
    @Test
    public void test(){
        System.out.println(redissonClient);
    }

    @Autowired
    private AttrGroupDao attrGroupService;

    @Test
    public void tests(){

        List<SpuItemAttrGroupVo> bySpuId = attrGroupService.getAttrGroupWithAttrsBySpuId(5l, 225l);

        System.out.println(bySpuId);
    }

}
