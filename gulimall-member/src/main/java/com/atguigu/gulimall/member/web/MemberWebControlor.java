package com.atguigu.gulimall.member.web;

import com.atguigu.common.utils.R;
import com.atguigu.gulimall.member.feign.OrderFeignSerice;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.HashMap;
import java.util.Map;

/**
 * @ClassName MemberWebControlor
 * @Description TODO
 * @Author lwq
 * @Date 2021/1/29 11:34
 * @Version 1.0
 */
@Controller
public class MemberWebControlor {

    @Autowired
    OrderFeignSerice orderFeignSerice;

    @GetMapping("/memberOrder.html")
    public String memberOrderPage(@RequestParam(value = "pageNum",
            defaultValue = "1") String pageNum, Model model){

        //获取支付宝给我们的带来的请求参数 验证签名
        Map<String,Object> map = new HashMap<>();
        map.put("page",pageNum);
        //查出用户订单
        R r = orderFeignSerice.listWithItem(map);
        model.addAttribute("orders",r);

        return "orderlist.html";
    }
}
