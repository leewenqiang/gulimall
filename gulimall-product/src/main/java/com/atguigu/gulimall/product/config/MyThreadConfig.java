package com.atguigu.gulimall.product.config;

import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * @ClassName MyThreadConfig
 * @Description TODO
 * @Author lwq
 * @Date 2021/1/14 18:25
 * @Version 1.0
 */
//@EnableConfigurationProperties(ThreadPoolConfigProperties.class)
@Configuration
public class MyThreadConfig {

    @Bean
    public ThreadPoolExecutor threadPoolExecutor(ThreadPoolConfigProperties poolConfigProperties){
        return new ThreadPoolExecutor(poolConfigProperties.getCoreSize(),poolConfigProperties.getMaxSize(),poolConfigProperties.getKeepAliveTime(),
                TimeUnit.SECONDS,new LinkedBlockingQueue<>(100000), Executors.defaultThreadFactory(),
                new  ThreadPoolExecutor.AbortPolicy()
                );
    }

}
