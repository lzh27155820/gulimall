package com.liu.xyz.gulimall.product;


import com.liu.xyz.gulimall.product.service.CategoryService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Arrays;

@SpringBootTest
class GulimallProductApplicationTests {

    @Autowired
    private CategoryService categoryService;

    @Test
    public void test(){
        Long[] byIdCatelogPath = categoryService.getByIdCatelogPath(225L);

        System.out.println(Arrays.toString(byIdCatelogPath));

    }




}
