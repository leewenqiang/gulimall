package com.atguigu.gulimall.order.web;

import com.atguigu.gulimall.order.entity.OrderEntity;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.Date;
import java.util.UUID;

/**
 * @ClassName HelloControlor
 * @Description TODO
 * @Author lwq
 * @Date 2021/1/20 16:31
 * @Version 1.0
 */

@Controller
public class HelloControlor {

    @Autowired
    RabbitTemplate rabbitTemplate;



    @GetMapping("/{page}.html")
    public String page(@PathVariable("page") String page){
        return page;
    }
    @ResponseBody
    @GetMapping("/test/order")
    public String createOrder(){
        OrderEntity orderEntity = new OrderEntity();
        orderEntity.setOrderSn(UUID.randomUUID().toString());
        orderEntity.setModifyTime(new Date());
        //给mq发消息
        rabbitTemplate.convertAndSend("order-event-exchange","order.create.order",orderEntity);
        return "发送成功!";
    }


}
