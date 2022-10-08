package com.liu.xyz.gulimall.product.valid;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 *  自定义JSR303校验规则
 * create liu 2022-10-03
 */

@Documented
@Constraint(
        validatedBy = {ListValueConstraintValidator.class}//把校验类放
)
@Target({ElementType.METHOD, ElementType.FIELD, ElementType.ANNOTATION_TYPE, ElementType.CONSTRUCTOR, ElementType.PARAMETER, ElementType.TYPE_USE})
@Retention(RUNTIME)
public @interface ListValue {
    //返回的消息
    String message() default "{com.liu.xyz.gulimall.product.valid.message}";
    //
    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
    //以上默认格式
    int[] value();
}
