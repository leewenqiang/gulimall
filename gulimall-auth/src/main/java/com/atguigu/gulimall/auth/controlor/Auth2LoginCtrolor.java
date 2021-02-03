package com.atguigu.gulimall.auth.controlor;

/**
 * @ClassName Auth2LoginCtrolor
 * @Description TODO
 * @Author lwq
 * @Date 2021/1/16 13:44
 * @Version 1.0
 */

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.atguigu.common.constant.AuthConstant;
import com.atguigu.common.utils.R;
import com.atguigu.gulimall.auth.feign.MemberFeiginService;
import com.atguigu.gulimall.auth.util.HttpUtils;
import com.atguigu.common.vo.MemberResponseVo;
import com.atguigu.gulimall.auth.vo.SocialUser;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.HttpResponse;
import org.apache.http.util.EntityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.servlet.http.HttpSession;
import java.util.HashMap;
import java.util.Map;

/**
 * 社交登录
 */
@Controller
@Slf4j
public class Auth2LoginCtrolor {

    @Autowired
    MemberFeiginService memberFeiginService;

    @GetMapping("/auth2/login")
    public String login(@RequestParam("code") String code, RedirectAttributes redirectAttributes, HttpSession httpSession) throws Exception {

        //根据code去获取accessToken
        Map<String,String> query = new HashMap<>();
        query.put("client_id","2032265458");
        query.put("client_secret","9e4c20822cf392917982e9eb38f4e054");
        query.put("grant_type","authorization_code");
        query.put("redirect_uri","http://auth.gulimall.com/auth2/login");
        query.put("code",code);

        HttpResponse response = HttpUtils.doPost("https://api.weibo.com", "/oauth2/access_token", "post", new HashMap<String, String>(), null, query);
        if(response.getStatusLine().getStatusCode()==200){
            //请求accessToken成功
           //转为对象
            String json = EntityUtils.toString(response.getEntity());
            SocialUser socialUser = JSON.parseObject(json, SocialUser.class);
            //调用远程服务登录
            R r = memberFeiginService.socialLogin(socialUser);
            if(r.getCode()==0){
                //登录成功
                MemberResponseVo data = r.getData(new TypeReference<MemberResponseVo>() {
                });
                log.info("登录成功,"+data);
                httpSession.setAttribute(AuthConstant.LOGIN_USER,data);
//                redirectAttributes.addFlashAttribute("user",data);
                return "redirect:http://gulimall.com";
            }else{
                return "redirect:http://auth.gulimall.com/login.html";
            }

        }else{
            //失败去登录页
            return "redirect:http://auth.gulimall.com/login.html";
        }
    }

}
