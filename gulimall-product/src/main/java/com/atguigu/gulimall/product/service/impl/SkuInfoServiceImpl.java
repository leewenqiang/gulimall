package com.atguigu.gulimall.product.service.impl;

import com.alibaba.fastjson.TypeReference;
import com.atguigu.common.utils.PageUtils;
import com.atguigu.common.utils.Query;
import com.atguigu.common.utils.R;
import com.atguigu.gulimall.product.dao.SkuInfoDao;
import com.atguigu.gulimall.product.entity.SkuImagesEntity;
import com.atguigu.gulimall.product.entity.SkuInfoEntity;
import com.atguigu.gulimall.product.entity.SpuInfoDescEntity;
import com.atguigu.gulimall.product.feign.SecKillFeignService;
import com.atguigu.gulimall.product.service.*;
import com.atguigu.gulimall.product.vo.AttrGroppVo;
import com.atguigu.gulimall.product.vo.SecondKillRedisTo;
import com.atguigu.gulimall.product.vo.SkuItemVo;
import com.atguigu.gulimall.product.vo.SkuSaleAttrVo;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ThreadPoolExecutor;


@Service("skuInfoService")
public class SkuInfoServiceImpl extends ServiceImpl<SkuInfoDao, SkuInfoEntity> implements SkuInfoService {


    @Autowired
    private SkuImagesService skuImagesService;

    @Autowired
    private SpuInfoDescService spuInfoDescService;
    @Autowired
    private AttrGroupService attrGroupService;

    @Autowired
    SkuSaleAttrValueService skuSaleAttrValueService;

    @Autowired
    ThreadPoolExecutor threadPoolExecutor;

    @Autowired
    SecKillFeignService secKillFeignService;

    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        IPage<SkuInfoEntity> page = this.page(
                new Query<SkuInfoEntity>().getPage(params),
                new QueryWrapper<SkuInfoEntity>()
        );

        return new PageUtils(page);
    }

    @Override
    public void saveSkuInfo(SkuInfoEntity skuInfoEntity) {

        this.baseMapper.insert(skuInfoEntity);
    }

    @Override
    public PageUtils queryPageByCondition(Map<String, Object> params) {
        QueryWrapper<SkuInfoEntity> wrapper = new QueryWrapper<>();


        //t: 1609134794630
        //page: 1
        //limit: 10
        //key:
        //catelogId: 0
        //brandId: 0
        //min: 0
        //max: 0

        String key = (String) params.get("key");
        if (!StringUtils.isEmpty(key)) {
            wrapper.and(c -> {
                c.eq("id", key).or().like("sku_name", key);
            });
        }

        String brandId = (String) params.get("brandId");
        if (!StringUtils.isEmpty(brandId) && !"0".equalsIgnoreCase(brandId)) {
            wrapper.eq("brand_id", brandId);
        }
        String catelogId = (String) params.get("catelogId");
        if (!StringUtils.isEmpty(catelogId) && !"0".equalsIgnoreCase(catelogId)) {
            wrapper.eq("catalog_id", catelogId);
        }

        String min = (String) params.get("min");
        if (!StringUtils.isEmpty(min)) {
            wrapper.ge("price", min);
        }

        String max = (String) params.get("max");
        if (!StringUtils.isEmpty(max) && new BigDecimal(max).compareTo(new BigDecimal("0")) == 1) {
            wrapper.le("price", max);
        }
        IPage<SkuInfoEntity> page = this.page(
                new Query<SkuInfoEntity>().getPage(params),
                wrapper
        );

        return new PageUtils(page);
    }

    @Override
    public List<SkuInfoEntity> getSkusBySpuId(Long spuId) {
        return this.list(new QueryWrapper<SkuInfoEntity>().eq("spu_id", spuId));
    }

    @Override
    public SkuItemVo skuItem(Long skuId) throws ExecutionException, InterruptedException {

        SkuItemVo skuItemVo = new SkuItemVo();

        CompletableFuture<SkuInfoEntity> skuInfoEntityCompletableFuture = CompletableFuture.supplyAsync(() -> {
            //sku基本信息
            SkuInfoEntity infoEntity = getById(skuId);
            skuItemVo.setSkuInfoEntity(infoEntity);
            return infoEntity;

        }, threadPoolExecutor);

        CompletableFuture<Void> saleFuture = skuInfoEntityCompletableFuture.thenAcceptAsync((res) -> {
            //spu销售属性组合
            List<SkuSaleAttrVo> skuSaleAttrVos = skuSaleAttrValueService.getSaleAttrValuesBySpuId(res.getSpuId());
            skuItemVo.setSkuSaleAttr(skuSaleAttrVos);
        }, threadPoolExecutor);


        CompletableFuture<Void> descFuture = skuInfoEntityCompletableFuture.thenAcceptAsync(res -> {
            //spu介绍
            SpuInfoDescEntity spuInfoDescEntity = spuInfoDescService.getById(res.getSpuId());
            skuItemVo.setSpuInfoDescEntity(spuInfoDescEntity);
        }, threadPoolExecutor);


        CompletableFuture<Void> attrGroupFuture = skuInfoEntityCompletableFuture.thenAcceptAsync(res -> {
            //规格参数信息
            List<AttrGroppVo> attrGroppVos = attrGroupService.getAttrGroupWithAttrsBySpuId(res.getSpuId(), res.getCatalogId());
            skuItemVo.setAttrGroppVos(attrGroppVos);
        }, threadPoolExecutor);


        //单独运行
        CompletableFuture<Void> imagesFuture = CompletableFuture.runAsync(() -> {
            //sku图片信息
            List<SkuImagesEntity> skuImagesEntities = skuImagesService.getImagesBySkuId(skuId);
            skuItemVo.setSkuImagesEntitys(skuImagesEntities);
        }, threadPoolExecutor);


        //查询当前sku是否参与秒杀优惠
        CompletableFuture<Void> getSkuSecKillTask = CompletableFuture.runAsync(() -> {
            R sKuSecKill = secKillFeignService.getSKuSecKill(skuId);
            if (sKuSecKill.getCode() == 0) {
                SecondKillRedisTo data = sKuSecKill.getData(new TypeReference<SecondKillRedisTo>() {
                });
                skuItemVo.setSecondKillRedisTo(data);
            }
        }, threadPoolExecutor);

        //等待所有任务都完成，返回
        CompletableFuture.allOf(saleFuture,descFuture,attrGroupFuture,imagesFuture,getSkuSecKillTask).get();


        return skuItemVo;

    }



}