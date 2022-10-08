package com.liu.xyz.gulimall.product.config;

import com.liu.xyz.common.productUtils.BizCodeEnume;
import com.liu.xyz.common.utils.R;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.HashMap;

/**
 * create liu 2022-10-03
 */
@Slf4j
@RestControllerAdvice
public class UniformlyException {


    @ExceptionHandler(value = MethodArgumentNotValidException.class)
    public R exx(MethodArgumentNotValidException bindingResult){

        HashMap<String, String> map = new HashMap<>();
        if(bindingResult.hasErrors()){
            log.info(bindingResult.getObjectName(),"xxxx");
            bindingResult.getFieldErrors().forEach((itme)->{
                //校验错误消息
                String defaultMessage = itme.getDefaultMessage();
                //校验错误字段
                String field = itme.getField();
                map.put(field,defaultMessage);
            });
            return R.error(BizCodeEnume.VAILD_EXCEPTION.getCode(), BizCodeEnume.VAILD_EXCEPTION.getMsg()).put("data",map);
        }
        return null;
    }

    @ExceptionHandler(value = Exception.class)
    public R ex(Exception e){

        String message = e.getMessage();

        return R.error(BizCodeEnume.UNKNOW_EXCEPTION.getCode(),BizCodeEnume.UNKNOW_EXCEPTION.getMsg());
    }
}
