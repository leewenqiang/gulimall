package com.atguigu.gulimall.order.config;

import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.connection.CorrelationData;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.annotation.PostConstruct;

/**
 * @ClassName MyRabbitConfig
 * @Description TODO
 * @Author lwq
 * @Date 2021/1/20 11:36
 * @Version 1.0
 */
@Configuration
public class MyRabbitConfig {


    @Autowired
    RabbitTemplate rabbitTemplate;

    /**
     * 使用json序列化机制 进行消息转换
     *
     *
     * @return
     */
    @Bean
    public MessageConverter messageConverter(){
        return new Jackson2JsonMessageConverter();
    }

    /**
     * 定制RabbitTemplate  MyRabbitConfig创建完成以后执行initRabbitTemplate
     * 设置确认回调
     */
    @PostConstruct
    public void initRabbitTemplate(){
        rabbitTemplate.setConfirmCallback(new RabbitTemplate.ConfirmCallback() {
            /**
             * 只要消息抵达服务器 ack是true
             * @param correlationData  当前消息的唯一关联数据 (消息的唯一id)
             * @param ack (是否成功收到)
             * @param cause (失败原因)
             */
            @Override
            public void confirm(CorrelationData correlationData, boolean ack, String cause) {
                System.out.println("confirm....correlationData:"+correlationData+"==>ack:"+ack+"==>cause:"+cause);
            }
        });

        // 消息正确抵达队列 rtnCallback
        rabbitTemplate.setReturnCallback(new RabbitTemplate.ReturnCallback() {
            /**
             *  只要消息没有投递给指定的队列 触发回调
             * @param message
             * @param replyCode
             * @param replyText
             * @param exchange
             * @param routingKey
             */
            @Override
            public void returnedMessage(Message message, int replyCode, String replyText, String exchange, String routingKey) {
                System.out.println("setReturnCallback....message:"+message+"==>replyCode:"+replyCode+"==>replyText:"+replyText+"==>exchange:"+exchange+"==>routingKey:"+routingKey);
            }
        });

    }





}
