package com.atguigu.gulimall.member.config;

import feign.RequestInterceptor;
import feign.RequestTemplate;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;

/**
 * @ClassName GuliMallFeignInterceptor
 * @Description TODO
 * @Author lwq
 * @Date 2021/1/22 9:55
 * @Version 1.0
 */
@Configuration
public class GuliMallFeignInterceptorConfig {

    @Bean
    RequestInterceptor requestInterceptor(){
        return new RequestInterceptor() {
            @Override
            public void apply(RequestTemplate template) {
                System.out.println("RequestInterceptor线程："+Thread.currentThread().getId());
                ServletRequestAttributes requestAttributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
                if(requestAttributes != null){
                    HttpServletRequest request = requestAttributes.getRequest();
                    if(request != null){
                        //获取cookie
                        String cookie = request.getHeader("Cookie");
                        //添加cookie
                        template.header("Cookie",cookie);
                    }
                }

            }
        };
    }

}
