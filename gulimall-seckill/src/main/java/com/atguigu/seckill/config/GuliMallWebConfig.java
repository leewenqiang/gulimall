package com.atguigu.seckill.config;

import com.atguigu.seckill.intercepter.LoginUserIntercepter;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * @ClassName GuliMallWebConfig
 * @Description 自定义web配置
 * @Author lwq
 * @Date 2021/1/21 9:28
 * @Version 1.0
 */
@Configuration
public class GuliMallWebConfig implements WebMvcConfigurer {

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(new LoginUserIntercepter()).addPathPatterns("/**");
    }
}
