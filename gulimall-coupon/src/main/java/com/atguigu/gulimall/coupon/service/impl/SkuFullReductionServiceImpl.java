package com.atguigu.gulimall.coupon.service.impl;

import com.atguigu.common.to.MemberPrice;
import com.atguigu.common.to.SkuReducationTo;
import com.atguigu.common.utils.PageUtils;
import com.atguigu.common.utils.Query;
import com.atguigu.gulimall.coupon.dao.SkuFullReductionDao;
import com.atguigu.gulimall.coupon.entity.MemberPriceEntity;
import com.atguigu.gulimall.coupon.entity.SkuFullReductionEntity;
import com.atguigu.gulimall.coupon.entity.SkuLadderEntity;
import com.atguigu.gulimall.coupon.service.MemberPriceService;
import com.atguigu.gulimall.coupon.service.SkuFullReductionService;
import com.atguigu.gulimall.coupon.service.SkuLadderService;
import com.atguigu.gulimall.coupon.service.SpuBoundsService;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;


@Service("skuFullReductionService")
public class SkuFullReductionServiceImpl extends ServiceImpl<SkuFullReductionDao, SkuFullReductionEntity> implements SkuFullReductionService {


    @Autowired
    SkuLadderService skuLadderService;
    @Autowired
    SkuFullReductionService skuFullReductionService;
    @Autowired
    MemberPriceService memberPriceService;
    @Autowired
    SpuBoundsService spuBoundsService;



    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        IPage<SkuFullReductionEntity> page = this.page(
                new Query<SkuFullReductionEntity>().getPage(params),
                new QueryWrapper<SkuFullReductionEntity>()
        );

        return new PageUtils(page);
    }

    @Override
    public void saveSkuRecucation(SkuReducationTo skuReducationTo) {

        // 优惠满减信息 gulimall_sms=>sms_sku_ladder,sms_sku_full_reduction,sms_member_price,sms_spu_bounds
        SkuLadderEntity skuLadderEntity = new SkuLadderEntity();
        skuLadderEntity.setSkuId(skuReducationTo.getSkuId());
        skuLadderEntity.setFullCount(skuReducationTo.getFullCount());
        skuLadderEntity.setDiscount(skuReducationTo.getDiscount());
        skuLadderEntity.setAddOther(skuReducationTo.getCountStatus());
        if(skuLadderEntity.getFullCount()>0){
            skuLadderService.save(skuLadderEntity);
        }


        // 满减信息
        SkuFullReductionEntity skuFullReductionEntity = new SkuFullReductionEntity();
        BeanUtils.copyProperties(skuReducationTo,skuFullReductionEntity);
        if(skuFullReductionEntity.getFullPrice().compareTo(new BigDecimal("0"))==1){
            this.save(skuFullReductionEntity);
        }


        //会员价格
        List<MemberPrice> memberPrice =
                skuReducationTo.getMemberPrice();
        List<MemberPriceEntity> collect = memberPrice.stream().map(r -> {
            MemberPriceEntity memberPriceEntity = new MemberPriceEntity();
            memberPriceEntity.setSkuId(skuReducationTo.getSkuId());
            memberPriceEntity.setMemberLevelId(r.getId());
            memberPriceEntity.setMemberLevelName(r.getName());
            memberPriceEntity.setMemberPrice(r.getPrice());
            memberPriceEntity.setAddOther(1);
            return memberPriceEntity;
        }).filter(x-> x.getMemberPrice().compareTo(new BigDecimal("0"))==1).collect(Collectors.toList());
        memberPriceService.saveBatch(collect);
    }

}