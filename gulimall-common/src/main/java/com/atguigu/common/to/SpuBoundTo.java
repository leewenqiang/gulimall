package com.atguigu.common.to;

import lombok.Data;

import java.math.BigDecimal;

/**
 * @ClassName SpuBoundTo
 * @Description TODO
 * @Author lwq
 * @Date 2020/12/28 11:23
 * @Version 1.0
 */
@Data
public class SpuBoundTo {

    private Long spuId;
    private BigDecimal buyBounds;
    private BigDecimal growBounds;


}
