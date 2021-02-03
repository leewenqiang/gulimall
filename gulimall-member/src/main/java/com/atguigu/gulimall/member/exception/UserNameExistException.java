package com.atguigu.gulimall.member.exception;

/**
 * @ClassName Exception
 * @Description TODO
 * @Author lwq
 * @Date 2021/1/16 9:55
 * @Version 1.0
 */
public class UserNameExistException extends RuntimeException{
    public UserNameExistException() {
        super("用户名已经存在");
    }
}
