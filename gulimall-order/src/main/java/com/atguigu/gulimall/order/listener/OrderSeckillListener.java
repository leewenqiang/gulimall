package com.atguigu.gulimall.order.listener;

import com.atguigu.common.to.SecKIllTo;
import com.atguigu.gulimall.order.service.OrderService;
import com.rabbitmq.client.Channel;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitHandler;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;

/**
 * @ClassName OrderSeckillListener
 * @Description TODO
 * @Author lwq
 * @Date 2021/2/2 17:02
 * @Version 1.0
 */
@Service
@Slf4j
@RabbitListener(queues = {"order.seckill.order.queue"})
public class OrderSeckillListener {

    @Autowired
    private OrderService orderService;



    @RabbitHandler
    public void listenQueue(SecKIllTo secKIllTo, Channel channel, Message message) throws IOException {

        log.info("准备创建秒杀单详细信息::"+secKIllTo.getOderSn());
        try {
            //尝试关单
            orderService.createSecKillOrder(secKIllTo);
            channel.basicAck(message.getMessageProperties().getDeliveryTag(),false);
        } catch (Exception e) {
            channel.basicReject(message.getMessageProperties().getDeliveryTag(),true);
            e.printStackTrace();
        }


    }

}
