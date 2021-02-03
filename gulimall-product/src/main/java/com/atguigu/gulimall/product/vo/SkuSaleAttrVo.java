package com.atguigu.gulimall.product.vo;

import lombok.Data;

import java.util.List;

/**
 * @ClassName SkuSaleAttrVo
 * @Description TODO
 * @Author lwq
 * @Date 2021/1/14 10:29
 * @Version 1.0
 */
@Data
public class SkuSaleAttrVo {
    private Long attrId;
    private String attrName;
    private List<AttrValueWithSkuIdsVo> attrValueWithSkuIdsVoList;
}
