package com.atguigu.gulimall.product;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.session.data.redis.config.annotation.web.http.EnableRedisHttpSession;


/**
 * JSR303数据校验
 *  步骤：
 *  1、在需要校验的contolor方法入参加上 @Valid 注解
 *  2、在实体类字段上加上相关注解 @@NotBlank(message = "品牌名必须提交..")
 *
 *
 *  统一异常梳理：
 *  GuliMallControlorAdvice
 *
 *  分组校验:
 *  属性上加上分组
 *  在需要校验的contolor方法入参加上
 *  @Validated(value = {UpdateGroup.class})
 *
 *  没有指定分组的属性在 @Validated 不指定分组的情况下生效
 *
 *  自定义校验注解：
 *  自定义的校验注解
 *  自定义的校验器
 *
 *   缓存：
 *   读模式：
 *   缓存穿透：查询一个用不存在的数据 null 危险操作 缓存一个空数据
 *   缓存击穿：大量请求进来 查询一个正好过期的数据。加锁
 *   缓存雪崩：大量的key同时过期。加随机时间
 *
 *   写模式：
 *   缓存与数据库的一致  读写加锁 有序进行
 *   引入canal
 *   读多写多 直接操作数据库
 *
 *   总结：常规数据（读多写少，及时性要求不高的数据） 可以用spring cache  写模式：只要缓存的数据有过期时间就够了
 *        特殊数据（） 特殊设计
 *
 *
 */

@EnableRedisHttpSession
@EnableDiscoveryClient
@MapperScan("com.atguigu.gulimall.product.dao")
@SpringBootApplication
@EnableFeignClients(basePackages = "com.atguigu.gulimall.product.feign")
public class GulimallProductApplication {

    public static void main(String[] args) {
        SpringApplication.run(GulimallProductApplication.class, args);
    }

}
