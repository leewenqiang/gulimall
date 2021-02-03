//package com.atguigu.gulimall.thirdparty;
//
//import com.aliyun.oss.OSS;
//import com.atguigu.gulimall.thirdparty.compont.SmsCompont;
//import org.junit.Test;
//import org.junit.runner.RunWith;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.test.context.SpringBootTest;
//import org.springframework.test.context.junit4.SpringRunner;
//
//import java.io.FileInputStream;
//import java.io.FileNotFoundException;
//import java.io.InputStream;
//
//@RunWith(SpringRunner.class)
//@SpringBootTest
//public class GulimallThirdpartyApplicationTests {
//
//
//    @Autowired
//    OSS oss;
//
//    @Autowired
//    SmsCompont smsCompont;
//
//    @Test
//   public void contextLoads() {
//        smsCompont.sendSms("18549816236","112233");
//    }
//
//    @Test
//    public void testUpload() throws FileNotFoundException {
//        // Endpoint以杭州为例，其它Region请按实际情况填写。
////        String endpoint = "oss-cn-shanghai.aliyuncs.com";
////// 云账号AccessKey有所有API访问权限，建议遵循阿里云安全最佳实践，创建并使用RAM子账号进行API访问或日常运维，请登录 https://ram.console.aliyun.com 创建。
////        String accessKeyId = "LTAI4GJuHzLQoaCRymgp4Aj7";
////        String accessKeySecret = "jmJAvgOONqONuSjeZg4PaBsDtOjO3R";
////// 创建OSSClient实例。
////        OSS ossClient = new OSSClientBuilder().build(endpoint, accessKeyId, accessKeySecret);
//// 上传文件流。
//        InputStream inputStream = new FileInputStream("E:\\files\\硅谷商城\\尚硅谷谷粒商城电商项目（分布式基础）\\尚硅谷谷粒商城电商项目（分布式基础）\\资料源码\\docs\\pics\\63e862164165f483.jpg");
//       oss.putObject("gulimall-hello-lwq", "测试.jpg", inputStream);
//////// 关闭OSSClient。
//       oss.shutdown();
//
//        System.out.println("上传成功");
//
//    }
//
//}
