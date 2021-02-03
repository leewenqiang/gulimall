package com.atguigu.seckill.service;

import com.atguigu.seckill.to.SecondKillRedisTo;

import java.util.List;

public interface SecondKillService {
    void uploadSecondKillSkulast3Days();

    List<SecondKillRedisTo> getCurrentSecKill();

    /**
     * 某个sku的秒杀信息
     * @param skuId
     * @return
     */
    SecondKillRedisTo getSKuSecKill(Long skuId);

    String kill(String killId, String code, Integer num);
}
