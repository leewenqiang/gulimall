package com.atguigu.seckill.controlor;

import com.atguigu.common.utils.R;
import com.atguigu.seckill.service.SecondKillService;
import com.atguigu.seckill.to.SecondKillRedisTo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @ClassName SecKillControlor
 * @Description TODO
 * @Author lwq
 * @Date 2021/2/1 14:25
 * @Version 1.0
 */

@Controller
public class SecKillControlor {


    @Autowired
    SecondKillService secondKillService;

    @ResponseBody
    @GetMapping("/getCurrentSecKill")
    public R getCurrentSecKill(){
        System.out.println("getCurrentSecKill正在执行======================");
        List<SecondKillRedisTo> secondKillRedisTos = secondKillService.getCurrentSecKill();

        return R.ok().setData(secondKillRedisTos);
    }
    @ResponseBody
    @GetMapping("/sku/seckill/{skuId}")
    public R getSKuSecKill(@PathVariable("skuId") Long skuId){
        SecondKillRedisTo secondKillRedisTo = secondKillService.getSKuSecKill(skuId);
        return R.ok().setData(secondKillRedisTo);
    }

    @GetMapping("kill")
    public String  secKill(@RequestParam("killId") String killId,
                           @RequestParam("code") String code,
                           @RequestParam("num") Integer num,
                           Model model){
        String orderSn = secondKillService.kill(killId,code,num);
        model.addAttribute("orderSn",orderSn);
        return "success";
    }



}
