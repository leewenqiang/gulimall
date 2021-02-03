package com.atguigu.gulimall.ware.config;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.Exchange;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.HashMap;
import java.util.Map;

/**
 * @ClassName MyRabbitMqConfig
 * @Description TODO
 * @Author lwq
 * @Date 2021/1/27 22:20
 * @Version 1.0
 */
@Configuration
public class MyRabbitMqConfig {

    /**
     * 使用json序列化机制 进行消息转换
     * @return
     */
    @Bean
    public MessageConverter messageConverter(){
        return new Jackson2JsonMessageConverter();
    }

   /* @RabbitListener(queues = {"stock.delay.queue"})
    public void test(Message message){

    }*/


    /**
     * 队列（设置超时时间 延迟队列）
     * @return
     */
    @Bean
    public Queue stockDelayQueue(){
        //String name, boolean durable, boolean exclusive, boolean autoDelete, Map<String, Object> arguments
        Map<String,Object> arguments = new HashMap<>();
        //死信交换机
        arguments.put("x-dead-letter-exchange","stock-event-exchange");
        //死信路由键
        arguments.put("x-dead-letter-routing-key","stock.release");
        //超时时间两分钟
        arguments.put("x-message-ttl",120000);

        Queue queue = new Queue("stock.delay.queue", true, false, false,arguments);
        return queue;
    }

    /**
     * 队列
     * @return
     */
    @Bean
    public Queue stockReleaseStockQueue(){
        Queue queue = new Queue("stock.release.stock.queue", true, false, false);
        return queue;
    }

    /**
     * 交换机
     * @return
     */
    @Bean
    public Exchange stockEventExchange(){
        //String name, boolean durable, boolean autoDelete, Map<String, Object> arguments
        TopicExchange topicExchange = new TopicExchange("stock-event-exchange",true,false);
        return topicExchange;
    }


    @Bean
    public Binding stockReleaseBinding(){
        //String destination, DestinationType destinationType, String exchange, String routingKey,
        //			Map<String, Object> arguments
        Binding binding = new Binding("stock.release.stock.queue", Binding.DestinationType.QUEUE,
                "stock-event-exchange",
                "stock.release.#",null);
        return binding;
    }
    @Bean
    public Binding stockLockedBinding(){
        Binding binding = new Binding("stock.delay.queue",
                Binding.DestinationType.QUEUE,"stock-event-exchange",
                "stock.locked",null);
        return binding;
    }

}
