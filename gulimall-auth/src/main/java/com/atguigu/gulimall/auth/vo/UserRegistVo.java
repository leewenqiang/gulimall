package com.atguigu.gulimall.auth.vo;

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
    @NotEmpty(message = "用户名必须提交")
    @Length(min = 3,max = 18,message = "用户名必须是6-18位")
    private String userName;

    @NotEmpty(message = "密码不能为空")
    @Length(min = 3,max = 18,message = "密码必须是6-18位")
    private String password;

    @NotEmpty(message = "手机号不能为空")
    @Pattern(regexp = "^[1]([3-9])[0-9]{9}$",message = "手机号格式不正确")
    private String phone;

    @NotEmpty(message = "验证码不能为空")
    private String code;


}
