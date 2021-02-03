package com.atguigu.gulimall.product.service;

import com.atguigu.common.utils.PageUtils;
import com.atguigu.gulimall.product.entity.AttrGroupEntity;
import com.atguigu.gulimall.product.vo.AttrAttrGroupRelationVo;
import com.atguigu.gulimall.product.vo.AttrGroppVo;
import com.atguigu.gulimall.product.vo.AttrGroupWithAttrsVo;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;
import java.util.Map;

/**
 * 属性分组
 *
 * @author lwq
 * @email lwqmrl@163.com
 * @date 2020-12-04 15:56:45
 */
public interface AttrGroupService extends IService<AttrGroupEntity> {

    PageUtils queryPage(Map<String, Object> params);

    PageUtils queryPage(Map<String, Object> params, Long catelogId);

    void removeRelation(List<AttrAttrGroupRelationVo> attrAttrGroupRelationVos);

    PageUtils queryNoMatterPage(Map<String, Object> params, Long attrgroupId);

    void addRelation(List<AttrAttrGroupRelationVo> attrAttrGroupRelationVo);

    List<AttrGroupWithAttrsVo> getAttrGroupsWithAttrsByCatelogId(Long catelogId);

    List<AttrGroppVo> getAttrGroupWithAttrsBySpuId(Long spuId, Long catalogId);
}

