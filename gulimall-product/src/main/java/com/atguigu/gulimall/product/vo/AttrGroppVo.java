package com.atguigu.gulimall.product.vo;

import lombok.Data;
import lombok.ToString;

import java.util.List;

/**
 * @ClassName AttrGroppVo
 * @Description TODO
 * @Author lwq
 * @Date 2021/1/14 10:27
 * @Version 1.0
 */
@Data
@ToString
public class AttrGroppVo {
    private Long attrGropupId;
    private String attrGroupName;
    private List<SpuBaseAttrVo> baseAttrVos;
}
