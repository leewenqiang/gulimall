package com.atguigu.gulimall.auth.feign;

import com.atguigu.common.utils.R;
import com.atguigu.gulimall.auth.vo.SocialUser;
import com.atguigu.gulimall.auth.vo.UserLoginVo;
import com.atguigu.gulimall.auth.vo.UserRegistVo;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient("gulimall-member")
public interface MemberFeiginService {

    @PostMapping("/member/member/regist")
    R regist(@RequestBody UserRegistVo userRegistVo);

    @PostMapping("/member/member/login")
     R login(@RequestBody UserLoginVo memberLoginVo);



//    @PostMapping("/social/login")
//    public R socialLogin(@RequestBody SocialUser socialUser){

    @PostMapping("/member/member/social/login")
     R socialLogin(@RequestBody SocialUser socialUser);

}
