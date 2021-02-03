package com.atguigu.gulimall.order;

import org.springframework.amqp.rabbit.annotation.EnableRabbit;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;

/**
 * RabbitMq
 * 引入amqp
 * //自动配置 RabbitTemplate  AmqpAdmin CachingConnectionFactory RabbitMessagingTemplate
 *
 * @EnableRabbit 开始rabbitmq自动配置
 *
 *
 *
 * seata
 *   每一个微服务创建  UNDO_LOG
 *   启动服务 安装事务协调器  spring-cloud-starter-alibaba-seata
 *   启动seata-server
 *   registry.conf
 *  使用seata代理数据源
 *
 *  加入 file.conf
 * 加入 registry.conf
 *
 *
 */

//@EnableAspectJAutoProxy(exposeProxy = true)
@EnableFeignClients
@EnableDiscoveryClient
@EnableRabbit
@SpringBootApplication
public class GulimallOrderApplication {

    public static void main(String[] args) {
        SpringApplication.run(GulimallOrderApplication.class, args);
    }

}
