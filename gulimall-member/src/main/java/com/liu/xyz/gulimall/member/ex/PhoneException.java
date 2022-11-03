package com.liu.xyz.gulimall.member.ex;

/**
 * create liu 2022-10-30
 */
public class PhoneException extends RuntimeException{
    public PhoneException() {
        super("手机号已注册过了");
    }
}
