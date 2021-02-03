package com.atguigu.seckill.scheduled;

import com.atguigu.seckill.service.SecondKillService;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

/**
 * @ClassName SecondKillScheduled
 * @Description 每天晚上3点 上架秒杀商品
 *
 * @Author lwq
 * @Date 2021/1/31 21:36
 * @Version 1.0
 */
@Service
@Slf4j
public class SecondKillScheduled {

    @Autowired
    SecondKillService secondKillService;

    @Autowired
    RedissonClient redissonClient;

    private final String uoloadLock = "seckill:upload:lock";

    /**
     * 分布式锁
     */
    @Scheduled(cron = "* */30 * * * ?")
    public void uploadSecondKillSkulast3Days(){

        log.info("上架秒杀的商品信息...");
        //
        //锁住
        RLock lock = redissonClient.getLock(uoloadLock);
        lock.lock(10, TimeUnit.SECONDS);
        try {
            secondKillService.uploadSecondKillSkulast3Days();
        } finally {
            //解锁
            lock.unlock();
        }
    }

}
