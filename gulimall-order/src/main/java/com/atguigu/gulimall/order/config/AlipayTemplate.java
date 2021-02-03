package com.atguigu.gulimall.order.config;

import com.alipay.api.AlipayApiException;
import com.alipay.api.AlipayClient;
import com.alipay.api.DefaultAlipayClient;
import com.alipay.api.request.AlipayTradePagePayRequest;
import com.atguigu.gulimall.order.vo.PayVo;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@ConfigurationProperties(prefix = "alipay")
@Component
@Data
public class AlipayTemplate {

    //在支付宝创建的应用的id
    private   String app_id = "2021000117607395";

    // 商户私钥，您的PKCS8格式RSA2私钥
    private  String merchant_private_key = "MIIEvQIBADANBgkqhkiG9w0BAQEFAASCBKcwggSjAgEAAoIBAQC4rnAqYCxCFJ0ozCFXWYM1TJxNxoavbH9TpC8Cp3spOAUJIWPJCuXYfZqHNbG78e9LUIv1Hqc6pN8gdGzN4ILbvZtkRgqe7KVG/tr90luTo9XFvMbxpKqQ/MFNf/cD8UMa2NpDiV/3fZ2tcMJmiQY2TER870QSinvz78q8wripbpAkpzd71Klx078fwUMWPnW7kSvm4k6bPgTcLv2tjOh0E2sHsSbkiqyeoUHBQPY9IG6Hdq38u7ip9U17myVcLQ9em2yo1duCAfHrS4/r2BX+qPqc4StOrIWNYvmbYHz/JXUQkXjTx03divzB1Lw6bIVej3VNlfRCygm/SKXL0z79AgMBAAECggEAeIkxoBbBDCgUIwqIQFCl+GWWJpUKVviDaf3gyOSy18Y+UyU90XuRZGkmMFV1OWvAePGLR2LIVthA4Rfb48tC3VS1VQ7545wki1/PYpanOodWAQci+Zg10weEyqZYfHQ9Dgq2+hlxT0DnZsoPt4h7S3kfwkMPtOI54I6DTgvxJnNtCVl5bOZ0yuH5FpzW2bPq7BJF4RQDf2qgtthcR2bzEuidOJZsFAJqD1Hv2uE9f4w5QgLmUWnkYcCZfRJH/azmYU/W9XaBPJX0SFeJwfbeLCAqlHhcSqzTJt/crUSMouwwv5OP525YZFGHG7brym4rJlb9vGv5kZxSQsbcClD1qQKBgQDiBeDBfcPR55cppT1q2roqyWCV7J6EvJ4R4a4HDZZZGOSB8OzH6vzWZn/z+AXtUezuABx+K8BEJY2EO1D/+oUTp13XTzvtKcdkY4rA8eFbL7BAPTFh00TpwFEMD+r8Gii/IIC8ECAysAetiXnCTs6wmegbw3/x8ruNvbIQgyy/QwKBgQDRLOWKxRoPg+BFtS4MNuddIN19vhGhmzi7ZL33mIjRNBFIxN8JMi4+rpuqPJDeX4CVVLsOnzjfta34WsfGjmgA/kTAch5r6gntDFg9LJe5UXotmqULpTiINUko32uhADnFZGw9APL13RmF5CKTvMExVApHyqQs6oE0Xy+gD8aEvwKBgFAqv45BmkwANZh62G7nB7MXXB+xHoR8FiDHiB9OTW5qqtn/5yv9iBJPpsCUzx1euoesGG8T+4zwOOKW22L/q2XOLjWpD9tNv0A3IkbPRBxArOYMDlDhS4cRRG3f03v5l0w7Lg4GNjVkRETkBwDJTPxMiowWanIZozZh+x9qN1vbAoGAAX81wVF5x0SlVTpfsvytmTAEBcNXcvWeaVDmPyBbUkvyF7g49GLki9RKDuTSEJVUYT0y/X7MNGWjchSMO3KHcgJf0ZS/fPw2x/h/pShtiYj4u9zQc43iIq9m76DxrzxGLZBzadhY5MOJTDj+ulnudADq9eqQVDX1aL+JoraaMBcCgYEAo/y+lIFFPW4k24nhmDStCOT3l9qHGlqITUFbYF/CD5CVl4d+h9RJVNOIkTH+Be+GASKLcLAeGN1/eOJCJNVIcr51PD4H3EPcvOUvAa2NCo1HxK8cbeQ5orGUFTyWm37UMdF/Owzkvnhm3qsDJ21qEoJvMFLc0KDMlSR5h5F1l1g=";
    // 支付宝公钥,查看地址：https://openhome.alipay.com/platform/keyManage.htm 对应APPID下的支付宝公钥。
    private  String alipay_public_key = "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAq2LlfkcDkdNL3/dXB931gSNaZ15nucVHuL6clfm+QDqbblGFkR5LLaqOzp3fh9VGPq0wxUh0F1iS9hbJYVYFT1GlcbnUhpxpgWx3k1uxezYSzaeoxmB2M5imO6a0CuccfpaS+Lm8XmCR+kzdazunNJLm+9RT3T44RvklT4jk+KsD01iV74UAbNuMR1twP4lh6u6/yxnB0RWcOwjUC61KD7t0Jg3Mw5d0cuLkN6bvMfm41x8JgSjrFtU5HyekXe6JwpbOyO7EEFhRbizI2BfK1mEHzNRvKLy51UL1TWDK7eqQpcbC/7NFqPimRkdmHTRN71jRRxzRAHd7eQzrrustEQIDAQAB";
    // 服务器[异步通知]页面路径  需http://格式的完整路径，不能加?id=123这类自定义参数，必须外网可以正常访问
    // 支付宝会悄悄的给我们发送一个请求，告诉我们支付成功的信息
    private  String notify_url="http://gea0g2qsus.52http.tech/payed/notify";

    // 页面跳转同步通知页面路径 需http://格式的完整路径，不能加?id=123这类自定义参数，必须外网可以正常访问
    //同步通知，支付成功，一般跳转到成功页
    private  String return_url ="http://member.gulimall.com/memberOrder.html";

    // 签名方式
    private  String sign_type = "RSA2";

    // 字符编码格式
    private  String charset = "utf-8";

    // 支付宝网关； https://openapi.alipaydev.com/gateway.do
    private  String gatewayUrl = "https://openapi.alipaydev.com/gateway.do";

    public  String pay(PayVo vo) throws AlipayApiException {

        //AlipayClient alipayClient = new DefaultAlipayClient(AlipayTemplate.gatewayUrl, AlipayTemplate.app_id, AlipayTemplate.merchant_private_key, "json", AlipayTemplate.charset, AlipayTemplate.alipay_public_key, AlipayTemplate.sign_type);
        //1、根据支付宝的配置生成一个支付客户端
        AlipayClient alipayClient = new DefaultAlipayClient(gatewayUrl,
                app_id, merchant_private_key, "json",
                charset, alipay_public_key, sign_type);

        //2、创建一个支付请求 //设置请求参数
        AlipayTradePagePayRequest alipayRequest = new AlipayTradePagePayRequest();
        alipayRequest.setReturnUrl(return_url);
        alipayRequest.setNotifyUrl(notify_url);

        //商户订单号，商户网站订单系统中唯一订单号，必填
        String out_trade_no = vo.getOut_trade_no();
        //付款金额，必填
        String total_amount = vo.getTotal_amount();
        //订单名称，必填
        String subject = vo.getSubject();
        //商品描述，可空
        String body = vo.getBody();

        alipayRequest.setBizContent("{\"out_trade_no\":\""+ out_trade_no +"\","
                + "\"total_amount\":\""+ total_amount +"\","
                + "\"subject\":\""+ subject +"\","
                + "\"body\":\""+ body +"\","
                + "\"timeout_express\":\"1m\","
                + "\"product_code\":\"FAST_INSTANT_TRADE_PAY\"}");

        String result = alipayClient.pageExecute(alipayRequest).getBody();

        //会收到支付宝的响应，响应的是一个页面，只要浏览器显示这个页面，就会自动来到支付宝的收银台页面
        System.out.println("支付宝的响应："+result);

        return result;

    }
}
