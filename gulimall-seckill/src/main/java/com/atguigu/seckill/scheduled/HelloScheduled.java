package com.atguigu.seckill.scheduled;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * @ClassName HelloScheduled
 * @Description TODO
 * @Author lwq
 * @Date 2021/1/31 21:18
 * @Version 1.0
 */

//@EnableAsync
@Component
@Slf4j
//@EnableScheduling
public class HelloScheduled {


//    @Async
//    @Scheduled(cron = "* * * * * ?")
    public void test1() throws InterruptedException {
        log.info("hello");
        Thread.sleep(3000);
    }
}
