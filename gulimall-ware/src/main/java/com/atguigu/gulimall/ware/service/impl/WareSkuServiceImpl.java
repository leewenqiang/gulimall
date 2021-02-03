package com.atguigu.gulimall.ware.service.impl;

import com.alibaba.fastjson.TypeReference;
import com.atguigu.common.to.StockDetaikTo;
import com.atguigu.common.to.StockLockedTo;
import com.atguigu.common.utils.PageUtils;
import com.atguigu.common.utils.Query;
import com.atguigu.common.utils.R;
import com.atguigu.gulimall.ware.dao.WareSkuDao;
import com.atguigu.gulimall.ware.entity.WareOrderTaskDetailEntity;
import com.atguigu.gulimall.ware.entity.WareOrderTaskEntity;
import com.atguigu.gulimall.ware.entity.WareSkuEntity;
import com.atguigu.gulimall.ware.feign.ProductFeign;
import com.atguigu.gulimall.ware.service.WareSkuService;
import com.atguigu.gulimall.ware.vo.OrderVo;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.List;
import java.util.Map;


@Service("wareSkuService")
public class WareSkuServiceImpl extends ServiceImpl<WareSkuDao, WareSkuEntity> implements WareSkuService {


    @Autowired
    ProductFeign productFeign;

    @Override
    public PageUtils queryPage(Map<String, Object> params) {
//
//        skuId: 1
//        wareId: 1
//        sku_id       bigint        null comment 'sku_id',
//                ware_id

        QueryWrapper<WareSkuEntity> wrapper = new QueryWrapper<>();




        IPage<WareSkuEntity> page = this.page(
                new Query<WareSkuEntity>().getPage(params),
                wrapper
        );

        return new PageUtils(page);
    }

    @Override
    public void addStock(Long skuId, Long wareId, Integer skuNum) {
        //先查询库存  sku_id       bigint        null comment 'sku_id',
        //    ware_id
        QueryWrapper<WareSkuEntity> wrapper = new QueryWrapper<WareSkuEntity>().eq("sku_id", skuId).eq("ware_id", wareId);
        List<WareSkuEntity> list = this.list(wrapper);
        if(CollectionUtils.isEmpty(list)){
            //新增
            WareSkuEntity wareSkuEntity = new WareSkuEntity();
            wareSkuEntity.setSkuId(skuId);
            //远程商品服务 TODO
            try{
                R info = productFeign.info(skuId);
                if(info.getCode()==0){
                    Map<String,Object> skuInfo = (Map<String, Object>) info.get("skuInfo");
                    wareSkuEntity.setSkuName((String) skuInfo.get("skuName"));
                }
            }catch (Exception e){
                log.error("调用远程接口失败!不回滚");
            }
            wareSkuEntity.setWareId(wareId);
            wareSkuEntity.setStock(skuNum);
            this.save(wareSkuEntity);
        }else{
            //修改
            WareSkuEntity entity = new WareSkuEntity();
            entity.setId(list.get(0).getId());
            entity.setStock(list.get(0).getStock()+skuNum);
            this.updateById(entity);
        }
    }

    @Override
    public void unLockStock(StockLockedTo stockLockedTo) {



    }

}