package com.atguigu.gulimall.order.web;

import com.atguigu.gulimall.order.service.OrderService;
import com.atguigu.gulimall.order.vo.OrderConfirmVo;
import com.atguigu.gulimall.order.vo.OrderSubmitVo;
import com.atguigu.gulimall.order.vo.SubmitOrderResponseVo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.concurrent.ExecutionException;

/**
 * @ClassName WebOrderControlor
 * @Description TODO
 * @Author lwq
 * @Date 2021/1/21 10:10
 * @Version 1.0
 */
@Controller
@Slf4j
public class WebOrderControlor {


    @Autowired
    OrderService orderService;

    @GetMapping("/toTrade")
    public String toTrade(Model model) throws ExecutionException, InterruptedException {
        OrderConfirmVo orderConfirmVo = orderService.getConfirmVo();
        model.addAttribute("confirmvo",orderConfirmVo);
        return "confirm";
    }

    @PostMapping("/submitOrder")
    public String submitOrder(OrderSubmitVo orderSubmitVo, Model model, RedirectAttributes redirectAttributes){
        String msg = "";
        SubmitOrderResponseVo submitOrderResponseVo = null;
        try {
            submitOrderResponseVo = orderService.submitOrder(orderSubmitVo);
            if(submitOrderResponseVo.getCode()==0){
                model.addAttribute("orderResponseVo",submitOrderResponseVo);
                return "pay";
            }else{

                Integer code = submitOrderResponseVo.getCode();
                switch (code){
                    case 1 : msg="信息过期，请重新提交！";
                        break;
                    case 2 : msg="订单商品价格发生变化，请确认后再次提交！";
                        break;
                    case 3 : msg="库存不足！";
                        break;
                    default:
                        break;
                }
                redirectAttributes.addFlashAttribute("msg",msg);
                return "redirect:http://order.gulimall.com/toTrade";
            }
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("msg","库存不足");
            return "redirect:http://order.gulimall.com/toTrade";
        }


    }

}
