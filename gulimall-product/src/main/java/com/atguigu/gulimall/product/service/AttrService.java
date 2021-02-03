package com.atguigu.gulimall.product.service;

import com.atguigu.gulimall.product.vo.AttrResVo;
import com.atguigu.gulimall.product.vo.AttrVo;
import com.baomidou.mybatisplus.extension.service.IService;
import com.atguigu.common.utils.PageUtils;
import com.atguigu.gulimall.product.entity.AttrEntity;

import java.util.List;
import java.util.Map;

/**
 * 商品属性
 *
 * @author lwq
 * @email lwqmrl@163.com
 * @date 2020-12-04 15:56:45
 */
public interface AttrService extends IService<AttrEntity> {

    PageUtils queryPage(Map<String, Object> params);

    void saveAttr(AttrVo attr);

    PageUtils queryBaseAttrPage(Map<String, Object> params, Long catelogId, String attrType);

    AttrResVo getAttrInfo(Long attrId);

    void updateAttr(AttrResVo attr);

    List<AttrEntity> getRelationAttr(Long attrgroupId);

    /**
     * 跳出检索属性
     * @param attrIds
     * @return
     */
    List<Long> searchAttrs(List<Long> attrIds);
}

