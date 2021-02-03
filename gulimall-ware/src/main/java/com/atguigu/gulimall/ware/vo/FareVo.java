package com.atguigu.gulimall.ware.vo;

import lombok.Data;

import java.math.BigDecimal;

/**
 * @ClassName FareVo
 * @Description TODO
 * @Author lwq
 * @Date 2021/1/22 16:31
 * @Version 1.0
 */
@Data
public class FareVo {
    private MemberReceiveAddressVo memberReceiveAddressVo;

    private BigDecimal fare;
}
