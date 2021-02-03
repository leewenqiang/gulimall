package com.atguigu.gulimall.product.service.impl;

import com.atguigu.common.utils.PageUtils;
import com.atguigu.common.utils.Query;
import com.atguigu.gulimall.product.dao.BrandDao;
import com.atguigu.gulimall.product.entity.BrandEntity;
import com.atguigu.gulimall.product.entity.CategoryBrandRelationEntity;
import com.atguigu.gulimall.product.service.BrandService;
import com.atguigu.gulimall.product.service.CategoryBrandRelationService;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;


@Service("brandService")
public class BrandServiceImpl extends ServiceImpl<BrandDao, BrandEntity> implements BrandService {


    @Autowired
   private CategoryBrandRelationService categoryBrandRelationService;
    @Autowired
    private  BrandService brandService;

    @Override
    public PageUtils queryPage(Map<String, Object> params) {

        String key = (String) params.get("key");
        QueryWrapper<BrandEntity> wrapper = new QueryWrapper<>();
        if(StringUtils.isNotEmpty(key)){
            wrapper.eq("brand_id",key).or().like("name",key);
        }
        wrapper.orderByDesc("sort");

        IPage<BrandEntity> page = this.page(
                new Query<BrandEntity>().getPage(params),
                wrapper
        );

        return new PageUtils(page);
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public void updateDetail(BrandEntity brand) {
        //保证冗余字段一致
        this.updateById(brand);
        //品牌名称
        if(StringUtils.isNotEmpty(brand.getName())){
            //同步更新其他关联表的数据
            categoryBrandRelationService.updateBrand(brand.getBrandId(),brand.getName());

            //TODO 更新其他关联

        }
    }

    @Override
    public List<BrandEntity> getBrandListByCatId(Long catId) {
        List<CategoryBrandRelationEntity> list =
                categoryBrandRelationService.getBaseMapper().selectList(new QueryWrapper<CategoryBrandRelationEntity>().eq("catelog_id", catId));

        if(!CollectionUtils.isEmpty(list)){
            List<BrandEntity> collect = list.stream().map(r -> {
                BrandEntity byId = brandService.getById(r.getBrandId());
                return byId;
            }).collect(Collectors.toList());
            return collect;
        }else{
            return new ArrayList<>();
        }
    }

}