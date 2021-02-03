package com.atguigu.gulimall.order.vo;

import lombok.Data;

import java.math.BigDecimal;

/**
 * @ClassName OrderSubmitVo
 * @Description 订单提交数据
 * @Author lwq
 * @Date 2021/1/22 17:28
 * @Version 1.0
 */
@Data
public class OrderSubmitVo {
    /**
     * 收获地址ID
     */
    private Long addrId;

    /**
     * 支付方式
     */
    private Integer payType;

    /**
     * 防重令牌
     */
    private String orderToken;

    /** 应付价格 **/
    private BigDecimal payPrice;

    /** 订单备注 **/
    private String remarks;

}
