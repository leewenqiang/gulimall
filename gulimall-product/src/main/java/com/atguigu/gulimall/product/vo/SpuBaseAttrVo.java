package com.atguigu.gulimall.product.vo;

import lombok.Data;
import lombok.ToString;

/**
 * @ClassName SpuBaseAttrVo
 * @Description TODO
 * @Author lwq
 * @Date 2021/1/14 10:29
 * @Version 1.0
 */
@Data
@ToString
public class SpuBaseAttrVo {
    private Long attrId;
    private String attrName;
    private String attrValues;
}
