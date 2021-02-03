package com.atguigu.seckill.to;

import com.atguigu.seckill.vo.SkuInfoVo;
import lombok.Data;

import java.math.BigDecimal;

/**
 * @ClassName SecondKillRedisTo
 * @Description TODO
 * @Author lwq
 * @Date 2021/2/1 10:02
 * @Version 1.0
 */
@Data
public class SecondKillRedisTo {

    /**
     * 活动id
     */
    private Long promotionId;
    /**
     * 活动场次id
     */
    private Long promotionSessionId;
    /**
     * 商品id
     */
    private Long skuId;
    /**
     * 秒杀价格
     */
    private BigDecimal seckillPrice;
    /**
     * 秒杀总量
     */
    private BigDecimal seckillCount;
    /**
     * 每人限购数量
     */
    private BigDecimal seckillLimit;
    /**
     * 排序
     */
    private Integer seckillSort;

    private  SkuInfoVo skuInfoVo;

    /**
     * 开始时间
     */
    private Long startTime;
    private Long endTime;


    /**
     * 随机码
     */
    private String randomCode;


}
