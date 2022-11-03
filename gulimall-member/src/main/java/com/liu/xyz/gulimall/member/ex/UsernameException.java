package com.liu.xyz.gulimall.member.ex;

/**
 * create liu 2022-10-30
 */
public class UsernameException extends RuntimeException{

    public UsernameException() {
        super("用户民相同");
    }
}
