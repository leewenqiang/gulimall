package com.atguigu.gulimall.order.vo;

import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

/**
 * @ClassName OrderItem
 * @Description TODO
 * @Author lwq
 * @Date 2021/1/21 10:23
 * @Version 1.0
 */
@Data
public class OrderItem {
    /**
     * 商品ID
     */
    private Long skuId;

    private String image;

    private String title;

    private List<String> skuAttr;

    private BigDecimal price;

    private Integer count;

    private BigDecimal totalPrice;

    private boolean hasStock = true;

}
