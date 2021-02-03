package com.atguigu.seckill.vo;

import lombok.Data;

import java.math.BigDecimal;

/**
 * @ClassName SeckillSkuRelationVo
 * @Description TODO
 * @Author lwq
 * @Date 2021/2/1 9:48
 * @Version 1.0
 */
@Data
public class SeckillSkuRelationVo {
    private Long id;
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
}
