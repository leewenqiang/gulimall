package com.atguigu.gulimall.cart.to;

import lombok.Data;
import lombok.ToString;

/**
 * @ClassName UserInfo
 * @Description TODO
 * @Author lwq
 * @Date 2021/1/18 16:21
 * @Version 1.0
 */

@ToString
@Data
public class UserInfoTo {

    private Long userId;
    private String userKey;

    private boolean tempUser = false;

}
