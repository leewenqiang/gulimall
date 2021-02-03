package com.atguigu.gulimall.product.config;

import org.redisson.Redisson;
import org.redisson.api.RedissonClient;
import org.redisson.config.Config;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.io.IOException;

/**
 * @ClassName MyRedissonConfig
 * @Description 所有对Rdeisson的使用 都要使用 RedissonClient对象
 * @Author lwq
 * @Date 2021/1/5 16:51
 * @Version 1.0
 */
@Configuration
public class MyRedissonConfig {

    @Bean(destroyMethod="shutdown")
    RedissonClient redisson() throws IOException {
//        Config config = new Config();
//        config.useClusterServers()
//                .addNodeAddress("127.0.0.1:7004", "127.0.0.1:7001");
//        return Redisson.create(config);

        // 默认连接地址 127.0.0.1:6379
//        RedissonClient redisson = Redisson.create();

        //创建配置
        Config config = new Config();
        config.useSingleServer().setAddress("redis://192.168.56.10:6379");

        //创建实例
        RedissonClient redisson = Redisson.create(config);
        return redisson;
    }

}
