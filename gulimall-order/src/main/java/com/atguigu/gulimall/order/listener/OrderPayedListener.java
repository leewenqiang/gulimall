package com.atguigu.gulimall.order.listener;

import com.alipay.api.AlipayApiException;
import com.alipay.api.internal.util.AlipaySignature;
import com.atguigu.gulimall.order.config.AlipayTemplate;
import com.atguigu.gulimall.order.service.OrderService;
import com.atguigu.gulimall.order.vo.PayAsyncVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.Map;

/**
 * @ClassName OrderPayedListener
 * @Description TODO
 * @Author lwq
 * @Date 2021/1/29 15:15
 * @Version 1.0
 */
@RestController
public class OrderPayedListener {

    @Autowired
    private OrderService orderService;

    @Autowired
    private AlipayTemplate alipayTemplate;

    @PostMapping("/payed/notify")
    public String hanldeAliPayed(PayAsyncVo payAsyncVo,HttpServletRequest request) throws AlipayApiException {
//        //告诉支付宝订单支付成功. 支付宝不再通知
//        Map<String, String[]> map = request.getParameterMap();
//        System.out.println("支付宝返回数据"+map);
//        //trade_status
//        //验签
//
//
//        String trade_status = request.getParameter("trade_status");
//
//
//        Set<String> keySet = map.keySet();
//        for (String key : keySet) {
//            String parameter = request.getParameter(key);
//            System.out.println("参数名:"+key+"参数值"+parameter);
//        }

        //验签
        Map<String, String> params = new HashMap<>();
        Map<String, String[]> requestParams = request.getParameterMap();
        for (String name : requestParams.keySet()) {
            String[] values = requestParams.get(name);
            String valueStr = "";
            for (int i = 0; i < values.length; i++) {
                valueStr = (i == values.length - 1) ? valueStr + values[i]
                        : valueStr + values[i] + ",";
            }
            //乱码解决，这段代码在出现乱码时使用
            // valueStr = new String(valueStr.getBytes("ISO-8859-1"), "utf-8");
            params.put(name, valueStr);
        }

        boolean signVerified = AlipaySignature.rsaCheckV1(params, alipayTemplate.getAlipay_public_key(),
                alipayTemplate.getCharset(), alipayTemplate.getSign_type()); //调用SDK验证签名

        if (signVerified) {
            System.out.println("签名验证成功...");
            //去修改订单状态
            String result = orderService.handlePayResult(payAsyncVo);
            return result;
        } else {
            System.out.println("签名验证失败...");
            return "error";
        }
    }

}
