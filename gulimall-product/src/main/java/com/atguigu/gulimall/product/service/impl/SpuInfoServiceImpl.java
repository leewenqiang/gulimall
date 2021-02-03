package com.atguigu.gulimall.product.service.impl;

import com.alibaba.fastjson.TypeReference;
import com.atguigu.common.constant.ProductConstant;
import com.atguigu.common.to.SkuHasStockVo;
import com.atguigu.common.to.SkuReducationTo;
import com.atguigu.common.to.SpuBoundTo;
import com.atguigu.common.to.es.SkuEsModel;
import com.atguigu.common.utils.PageUtils;
import com.atguigu.common.utils.Query;
import com.atguigu.common.utils.R;
import com.atguigu.gulimall.product.dao.SpuInfoDao;
import com.atguigu.gulimall.product.entity.*;
import com.atguigu.gulimall.product.feign.CouponFeignService;
import com.atguigu.gulimall.product.feign.SearchFeignService;
import com.atguigu.gulimall.product.feign.WareFeignService;
import com.atguigu.gulimall.product.service.*;
import com.atguigu.gulimall.product.vo.*;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;


@Service("spuInfoService")
public class SpuInfoServiceImpl extends ServiceImpl<SpuInfoDao, SpuInfoEntity> implements SpuInfoService {


    @Autowired
    private SpuInfoDescService spuInfoDescService;

    @Autowired
    private SpuImagesService spuImagesService;

    @Autowired
    private ProductAttrValueService productAttrValueService;

    @Autowired
    private AttrService attrService;

    @Autowired
    private BrandService brandService;

    @Autowired
    private CategoryService categoryService;

    @Autowired
    SkuInfoService skuInfoService;

    @Autowired
    SkuImagesService skuImagesService;

    @Autowired
    SkuSaleAttrValueService skuSaleAttrValueService;

    @Autowired
    CouponFeignService couponFeignService;

    @Autowired
    WareFeignService wareFeignService;
    @Autowired
    SearchFeignService searchFeignService;



    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        IPage<SpuInfoEntity> page = this.page(
                new Query<SpuInfoEntity>().getPage(params),
                new QueryWrapper<SpuInfoEntity>()
        );

        return new PageUtils(page);
    }


    //TODO seata 的AT模式
    @Transactional(rollbackFor = Exception.class)
    @Override
    public void saveSpuInfo(SpuSaveVo spuInfo) {

        //1、保存 基本信息 pms_spu_info
        SpuInfoEntity spuInfoEntity = new SpuInfoEntity();
        BeanUtils.copyProperties(spuInfo,spuInfoEntity);
        spuInfoEntity.setCreateTime(new Date());
        spuInfoEntity.setUpdateTime(new Date());
        this.saveBaseSpuInfo(spuInfoEntity);
        //2、保存描述信息  pms_spu_info_desc
        List<String> decript = spuInfo.getDecript();
        SpuInfoDescEntity descEntity = new SpuInfoDescEntity();
        descEntity.setSpuId(spuInfoEntity.getId());
        descEntity.setDecript(String.join(",",decript));
        spuInfoDescService.saveSpuDesrpit(descEntity);
        //3、保存图片信息 pms_spu_images
        List<String> images = spuInfo.getImages();
        spuImagesService.saveImages(spuInfoEntity.getId(),images);

        //4、保存spu规格参数 pms_product_attr_value
        List<BaseAttrs> baseAttrs = spuInfo.getBaseAttrs();
        if(!CollectionUtils.isEmpty(baseAttrs)){
            List<ProductAttrValueEntity> collect = baseAttrs.stream().map(r -> {
                ProductAttrValueEntity productAttrValueEntity = new ProductAttrValueEntity();
                productAttrValueEntity.setAttrId(r.getAttrId());
                AttrEntity attrEntity = attrService.getById(r.getAttrId());
                if(attrEntity != null){
                    productAttrValueEntity.setAttrName(attrEntity.getAttrName());
                }
                productAttrValueEntity.setAttrValue(r.getAttrValues());
                productAttrValueEntity.setQuickShow(r.getShowDesc());
                productAttrValueEntity.setSpuId(spuInfoEntity.getId());
                return productAttrValueEntity;
            }).collect(Collectors.toList());
            productAttrValueService.saveProductAttr(collect);
        }

        //积分信息
        Bounds bounds = spuInfo.getBounds();
        SpuBoundTo spuBoundTo = new SpuBoundTo();
        BeanUtils.copyProperties(bounds,spuBoundTo);
        spuBoundTo.setSpuId(spuInfoEntity.getId());
        R r = couponFeignService.saveSpuBounds(spuBoundTo);
        if(r.getCode()==0){

        }else{
            log.error("远程服务失败!");
        }

        //5、spu对应的所有sku
        List<Skus> skus = spuInfo.getSkus();
        String defaultImg = "";
        if(!CollectionUtils.isEmpty(skus)){

            for (Skus sku : skus) {

                List<Images> skuImages = sku.getImages();
                if(!CollectionUtils.isEmpty(skuImages)){
                    for (Images skuImage : skuImages) {
                        if(skuImage.getDefaultImg()==1){
                            defaultImg=skuImage.getImgUrl();
                        }
                    }
                }
                SkuInfoEntity skuInfoEntity = new SkuInfoEntity();
                BeanUtils.copyProperties(sku,skuInfoEntity);
                skuInfoEntity.setBrandId(spuInfoEntity.getBrandId());
                skuInfoEntity.setCatalogId(spuInfoEntity.getCatalogId());
                skuInfoEntity.setSaleCount(0L);
                skuInfoEntity.setSpuId(spuInfoEntity.getId());
                skuInfoEntity.setSkuDefaultImg(defaultImg);
                //          基本信息 pms_sku_info
                skuInfoService.saveSkuInfo(skuInfoEntity);

                Long skuId = skuInfoEntity.getSkuId();

                if(!CollectionUtils.isEmpty(skuImages)){
                    List<SkuImagesEntity> collect = skuImages.stream().map(img -> {
                        SkuImagesEntity skuImagesEntity = new SkuImagesEntity();
                        skuImagesEntity.setSkuId(skuId);
                        skuImagesEntity.setDefaultImg(img.getDefaultImg());
                        skuImagesEntity.setImgUrl(img.getImgUrl());
                        return skuImagesEntity;
                    }).filter(x-> !StringUtils.isEmpty(x.getImgUrl())).collect(Collectors.toList());
                    //  图片  pms_sku_images
                    skuImagesService.saveSkuImages(collect);
                }


                //  //  销售属性  pms_sku_sale_attr_value
                List<Attr> attr = sku.getAttr();
                if(!CollectionUtils.isEmpty(attr)){
                    List<SkuSaleAttrValueEntity> collect = attr.stream().map(item -> {
                        SkuSaleAttrValueEntity skuSaleAttrValueEntity = new SkuSaleAttrValueEntity();
                        BeanUtils.copyProperties(item, skuSaleAttrValueEntity);
                        skuSaleAttrValueEntity.setSkuId(skuId);

                        return skuSaleAttrValueEntity;
                    }).collect(Collectors.toList());
                    skuSaleAttrValueService.saveSaleAttrValue(collect);
                }


                // 优惠满减信息 gulimall_sms=>sms_sku_ladder,sms_sku_full_reduction,sms_member_price,sms_spu_bounds
                SkuReducationTo skuReducationTo = new SkuReducationTo();
                BeanUtils.copyProperties(sku,skuReducationTo);
                skuReducationTo.setSkuId(skuId);
                if(skuReducationTo.getFullCount()>0 || skuReducationTo.getFullPrice().compareTo(new BigDecimal("0"))==1){
                    R r1 = couponFeignService.saveSkuReducation(skuReducationTo);
                    if(r1.getCode()==0){

                    }else{
                        log.error("远程保存SKU优惠服务失败!");
                    }
                }




            }
        }



    }

    @Override
    public void saveBaseSpuInfo(SpuInfoEntity spuInfoEntity) {
        this.baseMapper.insert(spuInfoEntity);
    }

    @Override
    public PageUtils queryPageByCondition(Map<String, Object> params) {

        QueryWrapper<SpuInfoEntity> wrapper = new QueryWrapper<>();

        String key = (String) params.get("key");
        if(!StringUtils.isEmpty(key)){
            wrapper.and(c->{
                c.eq("id",key).or().like("spu_name",key);
            });
        }


        String status = (String) params.get("status");
        if(!StringUtils.isEmpty(status)){
            wrapper.eq("publish_status",status);
        }


        String brandId = (String) params.get("brandId");
        if(!StringUtils.isEmpty(brandId)  && !"0".equalsIgnoreCase(brandId)){
            wrapper.eq("brand_id",brandId);
        }
        String catelogId = (String) params.get("catelogId");
        if(!StringUtils.isEmpty(catelogId)  && !"0".equalsIgnoreCase(catelogId)){
            wrapper.eq("catalog_id",catelogId);
        }


        IPage<SpuInfoEntity> page = this.page(
                new Query<SpuInfoEntity>().getPage(params),
               wrapper
        );

        return new PageUtils(page);

    }


    @Transactional(rollbackFor = Exception.class)
    @Override
    public void up(Long spuId) {

        //找到所有的sku
        List<SkuInfoEntity> skuInfoList = skuInfoService.getSkusBySpuId(spuId);

        Map<Long, Boolean> skuStockMap = null;
        try {
            //库存
            R skusStock = wareFeignService.
                    getSkusStock(skuInfoList.stream().map(SkuInfoEntity::getSkuId).collect(Collectors.toList()));
    //        skusStock.getd
            TypeReference<List<SkuHasStockVo>> typeReference = new TypeReference<List<SkuHasStockVo>>() {
            };
            List<SkuHasStockVo> data = skusStock.getData(typeReference);
            //是否有库存的map
            skuStockMap = data.stream().collect(Collectors.toMap(SkuHasStockVo::getSkuId, item -> item.getHasStock()));
        }catch (Exception e){
            log.error("库存查询失败:"+e);
        }

        //查询当前sku的所有可以被检索的规格属性
        List<ProductAttrValueEntity> productAttrValueEntitys = productAttrValueService.baseAttrListForSpu(spuId);
        List<Long> attrIds = productAttrValueEntitys.stream().map(attr -> {
            return attr.getAttrId();
        }).collect(Collectors.toList());
        List<Long> searchAttrIds = attrService.searchAttrs(attrIds);

        Set<Long> idSet = new HashSet<>(searchAttrIds);


        List<SkuEsModel.Attr> attrs = productAttrValueEntitys.stream().filter(item -> {
            return idSet.contains(item.getAttrId());
        }).map(item -> {
            SkuEsModel.Attr attr = new SkuEsModel.Attr();
            BeanUtils.copyProperties(item, attr);
            return attr;
        }).collect(Collectors.toList());



        //封装每个sku信息
        Map<Long, Boolean> finalSkuStockMap = skuStockMap;
        List<SkuEsModel> esModels = skuInfoList.stream().map(sku -> {
            //商品上架
            SkuEsModel skuEsModel = new SkuEsModel();
            BeanUtils.copyProperties(sku,skuEsModel);
            skuEsModel.setSkuPrice(sku.getPrice());
            skuEsModel.setSkuImg(sku.getSkuDefaultImg());

            //热度评分
            skuEsModel.setHotScore(0L);
            //品牌信息
            BrandEntity brandEntity = brandService.getById(skuEsModel.getBrandId());
            skuEsModel.setBrandName(brandEntity.getName());
            skuEsModel.setBrandImg(brandEntity.getLogo());

            //分类信息
            CategoryEntity categoryEntity = categoryService.getById(skuEsModel.getCatalogId());
            skuEsModel.setCatalogName(categoryEntity.getName());


            skuEsModel.setAttrs(attrs);
            if(finalSkuStockMap ==null){
                skuEsModel.setHasStock(true);
            }else{
                skuEsModel.setHasStock(finalSkuStockMap.get(sku.getSkuId()));
            }
            return skuEsModel;
        }).collect(Collectors.toList());

        //TODO 发送es，远程调用检索服务发送
        R r = searchFeignService.prodcuctStartUp(esModels);
        if(r.getCode()==0){
            //成功
            //改掉spu发布状态
            this.getBaseMapper().updateSpuStatus(spuId, ProductConstant.StatusEnum.SPU_UP.getCode());
        }else{
            //失败
            //TODO 重复调用  接口幂等性
        }


    }

    @Override
    public SpuInfoEntity getSpuInfoBySkuId(Long skuId) {

//先查询sku表里的数据
        SkuInfoEntity skuInfoEntity = skuInfoService.getById(skuId);

        //获得spuId
        Long spuId = skuInfoEntity.getSpuId();

        //再通过spuId查询spuInfo信息表里的数据
        SpuInfoEntity spuInfoEntity = this.baseMapper.selectById(spuId);

        //查询品牌表的数据获取品牌名
        BrandEntity brandEntity = brandService.getById(spuInfoEntity.getBrandId());
//        spuInfoEntity.setBrandName(brandEntity.getName());
        spuInfoEntity.setBrandId(brandEntity.getBrandId());

        return spuInfoEntity;


    }

}