package com.atguigu.gulimall.order;

import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.amqp.core.AmqpAdmin;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

@Slf4j
@RunWith(SpringRunner.class)
@SpringBootTest
public class GulimallOrderApplicationTests {



    @Autowired
    AmqpAdmin amqpAdmin;



    /**
     * 队列 交换器 绑定  收发消息
     */
    @Test
    public void test1(){

        DirectExchange directExchange = new DirectExchange("hello-java-exchange", true, false, null);
        //创建交换机
        amqpAdmin.declareExchange(directExchange);

        log.info("hello-java-exchange创建成功");


    }

}
