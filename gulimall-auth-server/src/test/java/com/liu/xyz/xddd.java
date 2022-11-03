package com.liu.xyz;

import cn.hutool.captcha.generator.MathGenerator;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Random;
import java.util.UUID;

/**
 * create liu 2022-10-27
 */
@SpringBootTest
public class xddd {

    @Test
    public void test(){
        MathGenerator mathGenerator = new MathGenerator();

      //Math.random()
        Random random = new Random();
        int i = random.nextInt(10);
        System.out.println(i);

        System.out.println(UUID.randomUUID().toString());
    }

    @Test
    public void testx(){

        String uxx="123456_x";

        System.out.println(uxx.substring(0,6));
    }
}
