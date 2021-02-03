package com.atguigu.gulimall.order.vo;

import com.atguigu.gulimall.order.entity.OrderEntity;
import lombok.Data;

/**
 * @ClassName SubmitOrderResponseVo
 * @Description TODO
 * @Author lwq
 * @Date 2021/1/25 10:38
 * @Version 1.0
 */
@Data
public class SubmitOrderResponseVo {
    private OrderEntity order;

    /** 错误状态码 **/
    private Integer code;
}
