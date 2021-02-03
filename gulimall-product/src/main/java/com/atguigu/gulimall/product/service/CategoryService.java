package com.atguigu.gulimall.product.service;

import com.atguigu.common.utils.PageUtils;
import com.atguigu.gulimall.product.entity.CategoryEntity;
import com.atguigu.gulimall.product.vo.Category2Vo;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;
import java.util.Map;

/**
 * 商品三级分类
 *
 * @author lwq
 * @email lwqmrl@163.com
 * @date 2020-12-04 15:56:45
 */
public interface CategoryService extends IService<CategoryEntity> {

    PageUtils queryPage(Map<String, Object> params);

    /**
     * 按照父子分类找到数据
     * @return
     */
    List<CategoryEntity> queryTreeList();


    void removeMenus(List<Long> asList);

    Long[] getPath(Long catelogId);

    void updateCascade(CategoryEntity category);

    /**
     * 查询所有的一级分类
     * @return
     */
    List<CategoryEntity> getAllLevel1Categoryies();


    /**
     * 查所有分类
     * @return
     */
    Map<String,List<Category2Vo>> getCatelogJson();


}

