package com.atguigu.common.to;

import lombok.Data;

import java.math.BigDecimal;

/**
 * @ClassName SecKIllTo
 * @Description TODO
 * @Author lwq
 * @Date 2021/2/2 16:45
 * @Version 1.0
 */

@Data
public class SecKIllTo {


    private String oderSn;


    private Long promotionSessionId;
    /**
     * 商品id
     */
    private Long skuId;
    /**
     * 秒杀价格
     */
    private BigDecimal seckillPrice;

    private Integer num;

    private Long memberId;




}
