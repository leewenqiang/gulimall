package com.atguigu.gulimall.product.service.impl;

import com.atguigu.common.utils.PageUtils;
import com.atguigu.common.utils.Query;
import com.atguigu.gulimall.product.dao.CategoryDao;
import com.atguigu.gulimall.product.entity.CategoryEntity;
import com.atguigu.gulimall.product.service.CategoryService;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;


@Service("categoryService")
public class CategoryServiceImpl extends ServiceImpl<CategoryDao, CategoryEntity> implements CategoryService {

    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        IPage<CategoryEntity> page = this.page(
                new Query<CategoryEntity>().getPage(params),
                new QueryWrapper<CategoryEntity>()
        );

        return new PageUtils(page);
    }

    @Override
    public List<CategoryEntity> queryTreeList() {


        //1.找到所有的数据
        List<CategoryEntity> categoryEntities = baseMapper.selectList(null);

        //2、组长成树形数据返回(也就是父子关系),并且排序
        List<CategoryEntity> treeList = categoryEntities
                .stream()
                .filter(r -> Objects.equals(r.getParentCid(), 0L))
                .sorted((x,y)->{
                    if(x.getSort()==null){
                        return -1;
                    }else if(y.getSort()==null){
                        return 1;
                    }else{
                        return Integer.compare(x.getSort(),y.getSort());
                    }
                })
                .map(r -> {
                    r.setChildren(getChildren(r, categoryEntities));
                    return r;
                }).collect(Collectors.toList());

        return treeList;
    }

    @Override
    public void removeMenus(List<Long> asList) {
        //TODO 处理关联的逻辑
        this.removeByIds(asList);
    }

    /**
     * 获取子元素
     * @param r
     * @param categoryEntities
     * @return
     */
    private List<CategoryEntity> getChildren(CategoryEntity parent, List<CategoryEntity> categoryEntities) {
        //
        List<CategoryEntity> treeList = categoryEntities
                .stream()
                .filter(r -> Objects.equals(r.getParentCid(), parent.getCatId()))
                .sorted((x,y)->{
                    if(x.getSort()==null){
                        return -1;
                    }else if(y.getSort()==null){
                        return 1;
                    }else{
                        return Integer.compare(x.getSort(),y.getSort());
                    }
                })
                .map(r -> {
                    r.setChildren(getChildren(r, categoryEntities));
                    return r;
                }).collect(Collectors.toList());
        return treeList;
    }

}