package com.liu.xyz.gulimall.product;


import com.liu.xyz.gulimall.product.service.CategoryService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.SetOperations;
import org.springframework.data.redis.core.StringRedisTemplate;

/**
 *  在spring 2.3 以前需要加 @RunWith(SpringRunner.class)
 */
//@RunWith(SpringRunner.class)
@SpringBootTest
class GulimallProductApplicationTests {

    @Autowired
    private CategoryService categoryService;
    @Autowired
    private StringRedisTemplate redisTemplate;

    @Test
    public void test(){
        redisTemplate.opsForValue().set("hello","liu");
        String hello = redisTemplate.opsForValue().get("hello");
        System.out.println(hello);
    }
    @Test
    public void test1(){
        SetOperations<String, String> stringSetOperations =
                redisTemplate.opsForSet();

        stringSetOperations.add("h","xx","xxx");
        String h = stringSetOperations.pop("h");
         h = stringSetOperations.pop("h");
        System.out.println(h);

    }




}
