package com.atguigu.gulimall.order.listener;

import com.atguigu.gulimall.order.entity.OrderEntity;
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
 * @ClassName OrderCloseListener
 * @Description TODO
 * @Author lwq
 * @Date 2021/1/28 18:55
 * @Version 1.0
 */
@RabbitListener(queues = {"order.release.order.queue"})
@Service
@Slf4j
public class OrderCloseListener {


    @Autowired
    private OrderService orderService;



    @RabbitHandler
    public void listenQueue(OrderEntity orderEntity, Channel channel, Message message) throws IOException {
        log.info("收到过期的清单信息...准备关闭订单"+orderEntity.getOrderSn()+"=="+orderEntity.getModifyTime());
        try {
            //尝试关单
            orderService.closeOrder(orderEntity);
            //手动调用支付宝收单

            channel.basicAck(message.getMessageProperties().getDeliveryTag(),false);
        } catch (Exception e) {
            channel.basicReject(message.getMessageProperties().getDeliveryTag(),true);
            e.printStackTrace();
        }


    }


}
