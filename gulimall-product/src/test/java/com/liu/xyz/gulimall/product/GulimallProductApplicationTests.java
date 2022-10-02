package com.liu.xyz.gulimall.product;


import com.liu.xyz.gulimall.product.entity.CategoryEntity;
import com.liu.xyz.gulimall.product.service.CategoryService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

@SpringBootTest
class GulimallProductApplicationTests {

    @Autowired
    private CategoryService categoryService;

    @Test
    public void ss(){

        List<CategoryEntity> list = categoryService.list();
        System.out.println(list);
    }

}
