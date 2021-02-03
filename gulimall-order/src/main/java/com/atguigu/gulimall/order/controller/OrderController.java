package com.atguigu.gulimall.order.controller;

import com.atguigu.common.utils.PageUtils;
import com.atguigu.common.utils.R;
import com.atguigu.gulimall.order.entity.OrderEntity;
import com.atguigu.gulimall.order.service.OrderService;
import com.rabbitmq.client.Channel;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageProperties;
import org.springframework.amqp.rabbit.annotation.RabbitHandler;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.Date;
import java.util.Map;



/**
 * 订单
 *
 * @author lwq
 * @email lwqmrl@163.com
 * @date 2020-12-04 16:27:49
 */
@RabbitListener(queues = {"hello-hava-queue"})
@RestController
@RequestMapping("order/order")
public class OrderController {
    @Autowired
    private OrderService orderService;

    @Autowired
    RabbitTemplate rabbitTemplate;




    @GetMapping("/status/{orderSn}")
    public R getOrderStatus(@PathVariable("orderSn") String orderSn){
       OrderEntity orderEntity =  orderService.getOrderBySn(orderSn);
       return R.ok().setData(orderEntity);
    }


    @GetMapping("/snedMq")
    @ResponseBody
    public String sendMq(@RequestParam(value = "num",defaultValue = "10") Integer num){

        for(int i=0;i<num;i++){
//            if(i%2==0){
//                OrderReturnReasonEntity orderReturnReasonEntity = new OrderReturnReasonEntity();
//                orderReturnReasonEntity.setId(1L);
//                orderReturnReasonEntity.setCreateTime(new Date());
//                orderReturnReasonEntity.setName("哈哈"+i);
//                rabbitTemplate.convertAndSend("hello-java-exchange","hello.java",orderReturnReasonEntity);
//            }else{
                OrderEntity orderEntity = new OrderEntity();
                orderEntity.setCreateTime(new Date());
                orderEntity.setNote("测试"+i);
                rabbitTemplate.convertAndSend("hello-java-exchange","hello.java",orderEntity);

//            }
        }

        return "ok!";

    }


    /**
     * 列表
     */
    @RequestMapping("/list")
    //@RequiresPermissions("order:order:list")
    public R list(@RequestParam Map<String, Object> params){
        PageUtils page = orderService.queryPage(params);

        return R.ok().put("page", page);
    }


    /**
     * 列表
     */
    @PostMapping("/listWithItem")
    //@RequiresPermissions("order:order:list")
    public R listWithItem(@RequestBody Map<String, Object> params){
        PageUtils page = orderService.queryPageWithItem(params);

        return R.ok().put("page", page);
    }


    /**
     * 信息
     */
    @RequestMapping("/info/{id}")
    //@RequiresPermissions("order:order:info")
    public R info(@PathVariable("id") Long id){
		OrderEntity order = orderService.getById(id);

        return R.ok().put("order", order);
    }

    /**
     * 保存
     */
    @RequestMapping("/save")
    // @RequiresPermissions("order:order:save")
    public R save(@RequestBody OrderEntity order){
		orderService.save(order);

        return R.ok();
    }

    /**
     * 修改
     */
    @RequestMapping("/update")
    //@RequiresPermissions("order:order:update")
    public R update(@RequestBody OrderEntity order){
		orderService.updateById(order);

        return R.ok();
    }

    /**
     * 删除
     */
    @RequestMapping("/delete")
    //@RequiresPermissions("order:order:delete")
    public R delete(@RequestBody Long[] ids){
		orderService.removeByIds(Arrays.asList(ids));

        return R.ok();
    }

    @RabbitHandler()
    public void test1(Message message, OrderEntity orderEntity, Channel channel){
        System.out.println();
//        System.out.println("test1消费者消费："+orderEntity);
        MessageProperties messageProperties = message.getMessageProperties();
        long deliveryTag = messageProperties.getDeliveryTag();
//        System.out.println("deliveryTag==>"+deliveryTag);
        try {
            //确认签收
//            channel.basicAck(deliveryTag,false);

            if(deliveryTag%2==0){
                channel.basicAck(deliveryTag,false);
                System.out.println("签收了货物："+deliveryTag);
            }else{
                //deliveryTag, boolean multiple, boolean requeue
                //重新入队boolean requeue
                channel.basicNack(deliveryTag,false,true);
                System.out.println("没有签收货物："+deliveryTag);
            }

        } catch (Exception e) {
//            channel.basicReject();
            e.printStackTrace();
        }
    }

   /* @RabbitHandler()
    public void test2(Message message,OrderReturnReasonEntity returnReasonEntity,Channel channel){
//        System.out.println("test2消费者消费："+returnReasonEntity);
        MessageProperties messageProperties = message.getMessageProperties();
        long deliveryTag = messageProperties.getDeliveryTag();
        try {
            //确认签收
            channel.basicAck(deliveryTag,false);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }*/

}
