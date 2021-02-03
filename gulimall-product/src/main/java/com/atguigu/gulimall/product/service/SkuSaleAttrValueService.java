package com.atguigu.gulimall.product.service;

import com.atguigu.gulimall.product.vo.SkuSaleAttrVo;
import com.baomidou.mybatisplus.extension.service.IService;
import com.atguigu.common.utils.PageUtils;
import com.atguigu.gulimall.product.entity.SkuSaleAttrValueEntity;

import java.util.List;
import java.util.Map;

/**
 * sku销售属性&值
 *
 * @author lwq
 * @email lwqmrl@163.com
 * @date 2020-12-04 15:56:45
 */
public interface SkuSaleAttrValueService extends IService<SkuSaleAttrValueEntity> {

    PageUtils queryPage(Map<String, Object> params);

    void saveSaleAttrValue(List<SkuSaleAttrValueEntity> collect);

    List<SkuSaleAttrVo> getSaleAttrValuesBySpuId(Long spuId);

    List<String> getSkuSaleAttrValuesString(Long skuId);
}

