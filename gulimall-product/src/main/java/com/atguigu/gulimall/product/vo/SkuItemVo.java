package com.atguigu.gulimall.product.vo;

import com.atguigu.gulimall.product.entity.SkuImagesEntity;
import com.atguigu.gulimall.product.entity.SkuInfoEntity;
import com.atguigu.gulimall.product.entity.SpuInfoDescEntity;
import lombok.Data;

import java.util.List;

/**
 * @ClassName SkuItemVo
 * @Description TODO
 * @Author lwq
 * @Date 2021/1/13 9:39
 * @Version 1.0
 */

@Data
public class SkuItemVo {

    /**
     * Sku基本信息
     */
    private SkuInfoEntity skuInfoEntity;

    private boolean hasStock = true;

    /**
     * Sku图片信息
     */
    private List<SkuImagesEntity> skuImagesEntitys;

    /**
     * Spu介绍
     */
    private SpuInfoDescEntity spuInfoDescEntity;


    /**
     * 销售属性
     */
    private List<SkuSaleAttrVo> skuSaleAttr;


    /**
     * 基本属性分组
     */
    private List<AttrGroppVo> attrGroppVos;

    private SecondKillRedisTo secondKillRedisTo;

}
