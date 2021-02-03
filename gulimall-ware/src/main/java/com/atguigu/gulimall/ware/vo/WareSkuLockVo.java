package com.atguigu.gulimall.ware.vo;

import lombok.Data;

import java.util.List;

/**
 * @ClassName WareSkuLockVo
 * @Description TODO
 * @Author lwq
 * @Date 2021/1/25 11:50
 * @Version 1.0
 */
@Data
public class WareSkuLockVo {

    private String orderSn;

    /** 需要锁住的所有库存信息 **/
    private List<OrderItemVo> locks;
}
