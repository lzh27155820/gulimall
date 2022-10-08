package com.liu.xyz.gulimall.product.valid;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.util.HashSet;

/**
 * create liu 2022-10-03
 */
public class ListValueConstraintValidator implements ConstraintValidator<ListValue,Integer> {


    private  HashSet<Integer> set=new HashSet<Integer>();

    /**
     * 初始化
     * @param constraintAnnotation
     */
    @Override
    public void initialize(ListValue constraintAnnotation) {
        int[] value = constraintAnnotation.value();

        for (int i = 0; i < value.length; i++) {
            set.add(value[i]);
        }
    }

    /**
     *  数据校验
     * @param integer 校验值
     * @param constraintValidatorContext
     * @return
     */
    @Override
    public boolean isValid(Integer integer, ConstraintValidatorContext constraintValidatorContext) {


        return set.contains(integer);
    }
}
