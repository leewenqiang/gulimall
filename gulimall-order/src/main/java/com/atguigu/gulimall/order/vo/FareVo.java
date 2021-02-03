package com.atguigu.gulimall.order.vo;

import lombok.Data;

import java.math.BigDecimal;

/**
 * @ClassName FareVo
 * @Description TODO
 * @Author lwq
 * @Date 2021/1/25 11:34
 * @Version 1.0
 */
@Data
public class FareVo {

    private MemberReceiveAddressVo memberReceiveAddressVo;

    private BigDecimal fare;
}
