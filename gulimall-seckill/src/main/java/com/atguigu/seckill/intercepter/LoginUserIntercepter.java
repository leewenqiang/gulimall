package com.atguigu.seckill.intercepter;

import com.atguigu.common.constant.AuthConstant;
import com.atguigu.common.vo.MemberResponseVo;
import org.springframework.util.AntPathMatcher;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

/**
 * @ClassName LoginUserIntercepter
 * @Description 登录拦截器
 * @Author lwq
 * @Date 2021/1/21 9:19
 * @Version 1.0
 */

public class LoginUserIntercepter implements HandlerInterceptor {

    public static ThreadLocal<MemberResponseVo> threadLocal = new ThreadLocal<>();

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {

        String requestURI = request.getRequestURI();
        AntPathMatcher antPathMatcher = new AntPathMatcher();
        if(antPathMatcher.match("/kill",requestURI)){
            //判断是否登录
            HttpSession session = request.getSession();
            MemberResponseVo memberResponseVo = (MemberResponseVo) session.getAttribute(AuthConstant.LOGIN_USER);
            if(memberResponseVo != null){
                //登陆了
                //放行去执行目标方法
                threadLocal.set(memberResponseVo);
                return true;
            }else{
                //去登陆
                response.sendRedirect("http://auth.gulimall.com/login.html");
                return false;
            }
        }else{
            return true;
        }



    }
}
