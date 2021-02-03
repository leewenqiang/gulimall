package com.atguigu.gulimall.ware.listner;

import com.atguigu.common.to.OrderTo;
import com.atguigu.common.to.StockLockedTo;
import com.atguigu.gulimall.ware.service.WareInfoService;
import com.rabbitmq.client.Channel;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitHandler;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;

/**
 * @ClassName StockReleaseListener
 * @Description TODO
 * @Author lwq
 * @Date 2021/1/28 16:44
 * @Version 1.0
 */
@Service
@Slf4j
@RabbitListener(queues = {"stock.release.stock.queue"})
public class StockReleaseListener {

    @Autowired
    WareInfoService wareInfoService;



    /**
     *
     * 库存自动解锁
     *
     * 1、】下订单成功  库存锁定成功  接下来的业务失败  导致订单回滚 之前锁定的库存就要自动回滚
     * 2、锁库存失败
     *  解锁失败 不能删除消息
     * @param stockLockedTo
     * @param message
     */
    @RabbitHandler
    public void handlerStockLockRelease(StockLockedTo stockLockedTo, Message message, Channel channel) throws IOException {
        log.info("收到解锁库存的消息..");
        try {
            wareInfoService.unLockStock(stockLockedTo);
            channel.basicAck(message.getMessageProperties().getDeliveryTag(),false);
        } catch (Exception e) {
           channel.basicReject(message.getMessageProperties().getDeliveryTag(),true);
            e.printStackTrace();
        }
    }

    @RabbitHandler
    public void handlerOrderClose(OrderTo orderTo, Message message, Channel channel) throws IOException {
        log.info("收到订单关系的消息..准备关闭库存");
        try {
            wareInfoService.unLockStock(orderTo);
            channel.basicAck(message.getMessageProperties().getDeliveryTag(),false);
        } catch (Exception e) {
            channel.basicReject(message.getMessageProperties().getDeliveryTag(),true);
            e.printStackTrace();
        }
    }

}
