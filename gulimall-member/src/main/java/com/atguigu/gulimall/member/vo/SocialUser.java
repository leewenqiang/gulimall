package com.atguigu.gulimall.member.vo;

import lombok.Data;

/**
 * @ClassName SocialUser
 * @Description TODO
 * @Author lwq
 * @Date 2021/1/16 13:50
 * @Version 1.0
 */
@Data
public class SocialUser {

    private String access_token;
    private String remind_in;
    private String expires_in;
    private String uid;
    private String isRealName;

}
