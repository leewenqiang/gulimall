package com.atguigu.gulimall.order.config;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.Exchange;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.HashMap;
import java.util.Map;

/**
 * @ClassName MqMqConfig
 * @Description TODO
 * @Author lwq
 * @Date 2021/1/27 18:14
 * @Version 1.0
 */

@Configuration
public class MqMqConfig {


    /**
     * 自动创建(mq没有）
     * @return
     */
    @Bean
    public Queue orderDelayQueue(){
        //String name, boolean durable, boolean exclusive, boolean autoDelete, Map<String, Object> arguments
        Map<String,Object> arguments = new HashMap<>();
        arguments.put("x-dead-letter-exchange","order-event-exchange");
        arguments.put("x-dead-letter-routing-key","order.release.order");
        arguments.put("x-message-ttl",60000);

        Queue queue = new Queue("order.delay.queue", true, false, false,arguments);
        return queue;
    }
    @Bean
    public Queue orderReleaseOrderQueue(){
        Queue queue = new Queue("order.release.order.queue", true, false, false);
        return queue;
    }

    @Bean
    public Queue orderSeckillOrderQueue(){
        //流量消峰
        Queue queue = new Queue("order.seckill.order.queue", true, false, false);
       //order.seckill.order
        return queue;
    }


    @Bean
    public Exchange orderEventExchange(){
        //String name, boolean durable, boolean autoDelete, Map<String, Object> arguments
        TopicExchange topicExchange = new TopicExchange("order-event-exchange",true,false);
        return topicExchange;
    }
    @Bean
    public Binding orderCreateOrderBinding(){
        //String destination, DestinationType destinationType, String exchange, String routingKey,
        //			Map<String, Object> arguments
        Binding binding = new Binding("order.delay.queue", Binding.DestinationType.QUEUE,"order-event-exchange",
                "order.create.order",null);
        return binding;
    }
    @Bean
    public Binding orderReleaseOrderBinding(){
        Binding binding = new Binding("order.release.order.queue", Binding.DestinationType.QUEUE,"order-event-exchange",
                "order.release.order",null);
        return binding;
    }


    @Bean
    public Binding orderReleaseOtherBinding(){
        Binding binding = new Binding("stock.release.stock.queue",
                Binding.DestinationType.QUEUE,
                "order-event-exchange",
                "order.release.other",null);
        return binding;
    }

    @Bean
    public Binding orderSecKillOrderBinding(){
        Binding binding = new Binding("order.seckill.order.queue",
                Binding.DestinationType.QUEUE,
                "order-event-exchange",
                "order.seckill.order",null);
        return binding;
    }

}
