package com.atguigu.gulimall.product.service.impl;

import com.atguigu.common.constant.ProductConstant;
import com.atguigu.common.utils.PageUtils;
import com.atguigu.common.utils.Query;
import com.atguigu.gulimall.product.dao.AttrAttrgroupRelationDao;
import com.atguigu.gulimall.product.dao.AttrDao;
import com.atguigu.gulimall.product.dao.AttrGroupDao;
import com.atguigu.gulimall.product.entity.AttrAttrgroupRelationEntity;
import com.atguigu.gulimall.product.entity.AttrEntity;
import com.atguigu.gulimall.product.entity.AttrGroupEntity;
import com.atguigu.gulimall.product.service.AttrAttrgroupRelationService;
import com.atguigu.gulimall.product.service.AttrGroupService;
import com.atguigu.gulimall.product.service.AttrService;
import com.atguigu.gulimall.product.vo.AttrAttrGroupRelationVo;
import com.atguigu.gulimall.product.vo.AttrGroppVo;
import com.atguigu.gulimall.product.vo.AttrGroupWithAttrsVo;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;


@Service("attrGroupService")
public class AttrGroupServiceImpl extends ServiceImpl<AttrGroupDao, AttrGroupEntity> implements AttrGroupService {

    @Autowired
    private AttrAttrgroupRelationDao attrAttrgroupRelationDao;

    @Autowired
    private AttrAttrgroupRelationService attrAttrgroupRelationService;
    @Autowired
    private AttrDao attrDao;

    @Autowired
    private AttrGroupDao attrGroupDao;

    @Autowired
    private AttrGroupService attrGroupService;

    @Autowired
    private AttrService attrService;

    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        IPage<AttrGroupEntity> page = this.page(
                new Query<AttrGroupEntity>().getPage(params),
                new QueryWrapper<AttrGroupEntity>()
        );

        return new PageUtils(page);
    }

    @Override
    public PageUtils queryPage(Map<String, Object> params, Long catelogId) {


        //构造一个QueryWrapper
        QueryWrapper<AttrGroupEntity> wrapper = new QueryWrapper<AttrGroupEntity>();
        String key = (String) params.get("key");
        if(!StringUtils.isBlank(key)){
            wrapper.and((obj)->{
                obj.eq("attr_group_id",key).or().like("attr_group_name",key);
            });
        }
       if(catelogId==0){
           return new PageUtils(this.page(new Query<AttrGroupEntity>().getPage(params), wrapper));
       }else{
           wrapper.eq("catelog_id",catelogId);
           IPage<AttrGroupEntity> page = this.page(new Query<AttrGroupEntity>().getPage(params), wrapper);
           return new PageUtils(page);
       }

    }

    @Override
    public void removeRelation(List<AttrAttrGroupRelationVo> attrAttrGroupRelationVos) {

        List<AttrAttrgroupRelationEntity> collect = attrAttrGroupRelationVos.stream().map(
                r -> {
                    AttrAttrgroupRelationEntity attrAttrgroupRelationEntity = new AttrAttrgroupRelationEntity();
                    BeanUtils.copyProperties(r, attrAttrgroupRelationEntity);
                    return attrAttrgroupRelationEntity;
                }
        ).collect(Collectors.toList());

        //移除关系
        attrAttrgroupRelationDao.deleteBatch(collect);

    }

    /**
     * 逻辑：
     * 查询在当前分类下的 没有关联属性分组的属性
     * @param params
     * @param attrgroupId
     * @return
     */
    @Override
    public PageUtils queryNoMatterPage(Map<String, Object> params, Long attrgroupId) {

        //1、找到所有当前分类的所有属性
        //当前分类
        AttrGroupEntity attrGroupEntity = this.getById(attrgroupId);
        //当前分类ID
        Long catelogId = attrGroupEntity.getCatelogId();


        //当前分类下的所有属性分组
        List<AttrGroupEntity> allRelatedGroups =
                attrGroupDao.selectList(new QueryWrapper<AttrGroupEntity>().eq("catelog_id",catelogId));
        List<Long> collect = allRelatedGroups.stream().map(r -> r.getAttrGroupId()).collect(Collectors.toList());
        //查询被关联的属性
        List<AttrAttrgroupRelationEntity> relatedAttrs = attrAttrgroupRelationDao.
                selectList(new QueryWrapper<AttrAttrgroupRelationEntity>().in("attr_group_id", collect));
        List<Long> attrIds = new ArrayList<>();
        if(!CollectionUtils.isEmpty(relatedAttrs)){
            //关联的属性ID集合
            attrIds = relatedAttrs.stream().map(r -> r.getAttrId()).collect(Collectors.toList());
        }
        //数据
        QueryWrapper<AttrEntity> wrapper = new QueryWrapper<>();
        if(!CollectionUtils.isEmpty(attrIds)){
            //数据
            wrapper = new QueryWrapper<AttrEntity>().eq("catelog_id", catelogId).notIn("attr_id", attrIds);
        }else{
            //数据
            wrapper = new QueryWrapper<AttrEntity>().eq("catelog_id", catelogId);
        }

        log.error("ProductConstant.AttrEnum.ATTR_TYPE_BASE===="+ProductConstant.AttrEnum.ATTR_TYPE_BASE);
        log.error("ProductConstant.AttrEnum.ATTR_TYPE_BASE===="+ProductConstant.AttrEnum.ATTR_TYPE_BASE.getCode());


        wrapper.and(c->{
            c.eq("attr_type", ProductConstant.AttrEnum.ATTR_TYPE_BASE.getCode());
        });

        String key = (String) params.get("key");
        if(StringUtils.isNotEmpty(key)){
            wrapper.and(c->{
                c.eq("attr_id",key).or().like("attr_name",key);
            });
        }
        IPage<AttrEntity> attrEntityIPage = attrDao.selectPage(new Query<AttrEntity>().getPage(params), wrapper);

        return new PageUtils(attrEntityIPage);

    }

    @Override
    public void addRelation(List<AttrAttrGroupRelationVo> attrAttrGroupRelationVo) {


        List<AttrAttrgroupRelationEntity> collect = attrAttrGroupRelationVo.stream().map(r -> {
            AttrAttrgroupRelationEntity attrAttrgroupRelationEntity = new AttrAttrgroupRelationEntity();
            BeanUtils.copyProperties(r, attrAttrgroupRelationEntity);
            return attrAttrgroupRelationEntity;
        }).collect(Collectors.toList());

        attrAttrgroupRelationService.saveBatch(collect);


    }

    @Override
    public List<AttrGroupWithAttrsVo> getAttrGroupsWithAttrsByCatelogId(Long catelogId) {


        List<AttrGroupWithAttrsVo> attrGroupWithAttrsVos = new ArrayList<>();

        //先查找分类下所有的属性分组
        List<AttrGroupEntity> attrGroupEntities
                = attrGroupService.getBaseMapper().selectList(new QueryWrapper<AttrGroupEntity>().eq("catelog_id", catelogId));

        if(!CollectionUtils.isEmpty(attrGroupEntities)){
            attrGroupEntities.stream().forEach(item->{
                AttrGroupWithAttrsVo attrGroupWithAttrsVo = new AttrGroupWithAttrsVo();
                BeanUtils.copyProperties(item,attrGroupWithAttrsVo,"attrs");
                //查询属性分组关联的所有属性
                List<AttrAttrgroupRelationEntity> attrGroups = attrAttrgroupRelationService.getBaseMapper().
                        selectList(new QueryWrapper<AttrAttrgroupRelationEntity>().eq("attr_group_id", item.getAttrGroupId()));
                if(!CollectionUtils.isEmpty(attrGroups)){
                    List<Long> collect = attrGroups.stream().map(r -> r.getAttrId()).collect(Collectors.toList());
                    List<AttrEntity> currentAttrEntities = attrService.getBaseMapper()
                            .selectList(new QueryWrapper<AttrEntity>().in("attr_id", collect));
                    if(!CollectionUtils.isEmpty(currentAttrEntities)){
                        attrGroupWithAttrsVo.setAttrs(currentAttrEntities);
                    }
                }

                attrGroupWithAttrsVos.add(attrGroupWithAttrsVo);

            });
        }


        return attrGroupWithAttrsVos;

    }

    @Override
    public List<AttrGroppVo> getAttrGroupWithAttrsBySpuId(Long spuId, Long catalogId) {

        List<AttrGroppVo> attrGroppVos =   baseMapper.getAttrGroupWithAttrsBySpuId(spuId,catalogId);

        return attrGroppVos;

    }


}