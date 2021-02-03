package com.atguigu.gulimall.cart.interceptor;

import com.atguigu.common.constant.AuthConstant;
import com.atguigu.common.constant.CartConstant;
import com.atguigu.common.vo.MemberResponseVo;
import com.atguigu.gulimall.cart.to.UserInfoTo;
import org.apache.commons.lang.StringUtils;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.util.UUID;

/**
 * @ClassName CartInterceptor
 * @Description 在执行目标方法前 判断用户登录状态
 * @Author lwq
 * @Date 2021/1/18 16:18
 * @Version 1.0
 */
public class CartInterceptor implements HandlerInterceptor {

   public static ThreadLocal<UserInfoTo> toThreadLocal = new ThreadLocal<>();


    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {

        UserInfoTo userInfoTo = new UserInfoTo();

        //目标方法执行之前
        HttpSession session = request.getSession();
        MemberResponseVo memberResponseVo = (MemberResponseVo) session.getAttribute(AuthConstant.LOGIN_USER);
        if(memberResponseVo != null){
            //登陆了
            userInfoTo.setUserId(memberResponseVo.getId());
        }
        Cookie[] cookies = request.getCookies();
        if(cookies != null && cookies.length>0){
            for (Cookie cookie : cookies) {
                String name = cookie.getName();
                if(CartConstant.CART_TEMP_COOKIE_NAME.equals(name)){
                    userInfoTo.setUserKey(cookie.getValue());
                    userInfoTo.setTempUser(true);
                }
            }
        }


        //没有临时用户，分配临时用户
        if(StringUtils.isEmpty(userInfoTo.getUserKey())){
            String uuid  = UUID.randomUUID().toString();
            userInfoTo.setUserKey(uuid);
        }

        toThreadLocal.set(userInfoTo);
        return true;
    }

    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception {

        UserInfoTo userInfoTo = toThreadLocal.get();
        if(!userInfoTo.isTempUser()){
            //业务执行之后，让浏览器保存一个cookie
            Cookie cookie = new Cookie(CartConstant.CART_TEMP_COOKIE_NAME,userInfoTo.getUserKey());
            cookie.setDomain("gulimall.com");
            cookie.setMaxAge(CartConstant.CART_TEMP_COOKIE_TIME_OUT);
            response.addCookie(cookie);
        }


    }
}
