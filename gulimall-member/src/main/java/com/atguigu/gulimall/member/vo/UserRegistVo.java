package com.atguigu.gulimall.member.vo;

import lombok.Data;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Pattern;

/**
 * @ClassName UserRegistVo
 * @Description TODO
 * @Author lwq
 * @Date 2021/1/16 8:43
 * @Version 1.0
 */

@Data
public class UserRegistVo {

    private String userName;

    private String password;

    private String phone;




}
