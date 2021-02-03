package com.atguigu.gulimall.member.config;

import com.atguigu.gulimall.member.intercepter.LoginUserIntercepter;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * @ClassName MemberWebConfig
 * @Description TODO
 * @Author lwq
 * @Date 2021/1/29 11:36
 * @Version 1.0
 */
@Configuration
public class MemberWebConfig implements WebMvcConfigurer {
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(new LoginUserIntercepter()).addPathPatterns("/**");
    }
}
