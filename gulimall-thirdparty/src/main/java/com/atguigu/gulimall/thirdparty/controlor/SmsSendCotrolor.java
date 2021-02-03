package com.atguigu.gulimall.thirdparty.controlor;

import com.atguigu.common.utils.R;
import com.atguigu.gulimall.thirdparty.compont.SmsCompont;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * @ClassName SmsSendCotrolor
 * @Description TODO
 * @Author lwq
 * @Date 2021/1/15 14:00
 * @Version 1.0
 */

@RestController
@RequestMapping("/sms")
public class SmsSendCotrolor {

    @Autowired
    private SmsCompont smsCompont;


    @GetMapping("/sendcode")
    public R sendCode(@RequestParam("phone") String phone, @RequestParam("code") String code){
        smsCompont.sendSms(phone,code);
        return R.ok();
    }

}
