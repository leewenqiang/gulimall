package com.atguigu.gulimall.ware.service.impl;

import com.alibaba.fastjson.TypeReference;
import com.atguigu.common.exception.NoStockException;
import com.atguigu.common.to.OrderTo;
import com.atguigu.common.to.SkuHasStockVo;
import com.atguigu.common.to.StockDetaikTo;
import com.atguigu.common.to.StockLockedTo;
import com.atguigu.common.utils.PageUtils;
import com.atguigu.common.utils.Query;
import com.atguigu.common.utils.R;
import com.atguigu.gulimall.ware.dao.WareInfoDao;
import com.atguigu.gulimall.ware.dao.WareSkuDao;
import com.atguigu.gulimall.ware.entity.WareInfoEntity;
import com.atguigu.gulimall.ware.entity.WareOrderTaskDetailEntity;
import com.atguigu.gulimall.ware.entity.WareOrderTaskEntity;
import com.atguigu.gulimall.ware.feign.MemberFeignService;
import com.atguigu.gulimall.ware.feign.OrderFeignService;
import com.atguigu.gulimall.ware.service.WareInfoService;
import com.atguigu.gulimall.ware.service.WareOrderTaskDetailService;
import com.atguigu.gulimall.ware.service.WareOrderTaskService;
import com.atguigu.gulimall.ware.vo.*;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@Service("wareInfoService")
public class WareInfoServiceImpl extends ServiceImpl<WareInfoDao, WareInfoEntity> implements WareInfoService {


    public static final int ORDER_CANCLE = 4;
    @Autowired
    MemberFeignService memberFeignService;

    @Autowired
    private WareSkuDao wareSkuDao;

    @Autowired
    private RabbitTemplate rabbitTemplate;

    @Autowired
    WareOrderTaskService wareOrderTaskService;

    @Autowired
    WareOrderTaskDetailService wareOrderTaskDetailService;

    @Autowired
    OrderFeignService orderFeignService;





    private void unLockStock(Long skuId,Long wareId,Integer num,Long taskDetaidId){
        //解锁
        wareSkuDao.unLockStock(skuId,wareId,num);
        //调整库存工作单的状态
        WareOrderTaskDetailEntity wareOrderTaskDetailEntity = new WareOrderTaskDetailEntity();
        wareOrderTaskDetailEntity.setId(taskDetaidId);
        //已解锁
        wareOrderTaskDetailEntity.setLockStatus(2);
        wareOrderTaskDetailService.updateById(wareOrderTaskDetailEntity);
    }

    @Override
    public PageUtils queryPage(Map<String, Object> params) {


//        private String name;
//        /**
//         * 仓库地址
//         */
//        private String address;
//        /**
//         * 区域编码
//         */
//        private String areacode;

        QueryWrapper<WareInfoEntity> wrapper = new QueryWrapper<>();
        String key = (String) params.get("key");
        if(StringUtils.isNotEmpty(key)){
            wrapper.and(c->{
                c.like("name",key).or().like("address",key).or().like("areacode",key);
            });
        }

        IPage<WareInfoEntity> page = this.page(
                new Query<WareInfoEntity>().getPage(params),
                wrapper
        );



        return new PageUtils(page);
    }

    @Override
    public List<SkuHasStockVo> hasStock(List<Long> skuIds) {

        List<SkuHasStockVo> collect = skuIds.stream().map(item -> {
            SkuHasStockVo skuHasStockVo = new SkuHasStockVo();
            //查询sku存储
           Long count =  baseMapper.getSkuStcok(item);
           skuHasStockVo.setSkuId(item);
           skuHasStockVo.setHasStock(count==null?false:count>0);
            return skuHasStockVo;
        }).collect(Collectors.toList());
        return collect;

    }

    @Override
    public FareVo getFare(Long addrId) {

        FareVo fareVo = new FareVo();

        //计算运费
        R r = memberFeignService.addrInfo(addrId);
        MemberReceiveAddressVo data = r.getData("memberReceiveAddress",new TypeReference<MemberReceiveAddressVo>(){});
        if(data != null){
            String phone = data.getPhone();
            //简单处理 需要调用三方接口确认费用
            String substring = phone.substring(phone.length() - 1, phone.length());

            fareVo.setMemberReceiveAddressVo(data);
            fareVo.setFare(new BigDecimal(substring));


            return fareVo;
        }
        return null;

    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public boolean orderLockStock(WareSkuLockVo vo) {

        /**
         * 解锁场景：
         *  1、下订单成功 订单过期没有支付 被系统自动取消或者手动取消  解锁库存
         *  2、下订单成功 库存成功 其他业务调用失败 导致订单回滚 之前锁定的库存就要解锁  自动解锁
         */


        /**
         * 保存库存工作单详情信息
         * 追溯
         */
        WareOrderTaskEntity wareOrderTaskEntity = new WareOrderTaskEntity();
        wareOrderTaskEntity.setOrderSn(vo.getOrderSn());
        wareOrderTaskEntity.setCreateTime(new Date());
        wareOrderTaskService.save(wareOrderTaskEntity);


        //1、按照下单的收货地址，找到一个就近仓库，锁定库存
        //2、找到每个商品在哪个仓库都有库存
        List<OrderItemVo> locks = vo.getLocks();

        List<SkuWareHasStock> collect = locks.stream().map((item) -> {
            SkuWareHasStock stock = new SkuWareHasStock();
            Long skuId = item.getSkuId();
            stock.setSkuId(skuId);
            stock.setNum(item.getCount());
            //查询这个商品在哪个仓库有库存
            List<Long> wareIdList = wareSkuDao.listWareIdHasSkuStock(skuId);
            stock.setWareId(wareIdList);

            return stock;
        }).collect(Collectors.toList());

        //2、锁定库存
        for (SkuWareHasStock hasStock : collect) {
            boolean skuStocked = false;
            Long skuId = hasStock.getSkuId();
            List<Long> wareIds = hasStock.getWareId();

            if (org.springframework.util.StringUtils.isEmpty(wareIds)) {
                //没有任何仓库有这个商品的库存
                throw new NoStockException(skuId);
            }

            //1、如果每一个商品都锁定成功,将当前商品锁定了几件的工作单记录发给MQ
            //2、锁定失败。前面保存的工作单信息都回滚了。发送出去的消息，即使要解锁库存，由于在数据库查不到指定的id，所有就不用解锁
            for (Long wareId : wareIds) {
                //锁定成功就返回1，失败就返回0
                Long count = wareSkuDao.lockSkuStock(skuId,wareId,hasStock.getNum());
                if (count == 1) {
                    log.error("库存锁定成功 =---"+skuId);
                    skuStocked = true;
//                    WareOrderTaskDetailEntity taskDetailEntity = WareOrderTaskDetailEntity.builder()
//                            .skuId(skuId)
//                            .skuName("")
//                            .skuNum(hasStock.getNum())
//                            .taskId(wareOrderTaskEntity.getId())
//                            .wareId(wareId)
//                            .lockStatus(1)
//                            .build();


                    //保存库存工作单详情
                    WareOrderTaskDetailEntity taskDetailEntity = new WareOrderTaskDetailEntity(null,skuId,null,hasStock.getNum(),wareOrderTaskEntity.getId(),wareId,1);
                    wareOrderTaskDetailService.save(taskDetailEntity);
//
//                   //TODO 告诉MQ库存锁定成功
                    StockLockedTo lockedTo = new StockLockedTo();
                    lockedTo.setId(wareOrderTaskEntity.getId());
                    StockDetaikTo detailTo = new StockDetaikTo();
                    BeanUtils.copyProperties(taskDetailEntity,detailTo);
                    lockedTo.setDetaikTo(detailTo);
//                    StockDetailTo detailTo = new StockDetailTo();
//                    BeanUtils.copyProperties(taskDetailEntity,detailTo);
//                    lockedTo.setDetailTo(detailTo);

                    rabbitTemplate.convertAndSend("stock-event-exchange","stock.locked",lockedTo);
                    log.error("发送消息成功...");
                    break;
                } else {
                    //当前仓库锁失败，重试下一个仓库
                }
            }

            if (!skuStocked ) {
                //当前商品所有仓库都没有锁住
                throw new NoStockException(skuId);
            }
        }

        //3、肯定全部都是锁定成功的
        return true;

    }

    @Override
    public void unLockStock(StockLockedTo stockLockedTo) {

        Long id = stockLockedTo.getId();
        StockDetaikTo detaikTo = stockLockedTo.getDetaikTo();
        //解锁
        // 先去数据库查询一下 关于这个订单的锁库存信息
        //1、有  需要解锁
        // 2、 没有 库存锁定失败 库存回滚  这种情况无需解锁
        WareOrderTaskDetailEntity byId = wareOrderTaskDetailService.getById(detaikTo.getId());
        if(byId != null){
            //解锁
            //说明库服务锁定 正常
            // 查看订单
            // 1、没有订单 必须解锁
            // 2、有订单
            // 判断订单状态 如果是已取消 解锁库存
            // 其他状态 不能解锁库存

            WareOrderTaskEntity wareOrderTaskEntity = wareOrderTaskService.getById(id);
            String orderSn = wareOrderTaskEntity.getOrderSn();
            R orderStatus = orderFeignService.getOrderStatus(orderSn);
            if(orderStatus.getCode()==0){
                //成功
                OrderVo data = orderStatus.getData(new TypeReference<OrderVo>() {
                });
                //订单被取消
                if(data==null || data.getStatus()== ORDER_CANCLE){
                    //解锁
                    //状态为1的时候 已锁定状态才可以解锁
                    if(byId.getLockStatus()==1){
                        unLockStock(detaikTo.getSkuId(),detaikTo.getWareId(),detaikTo.getSkuNum(),byId.getId());
                    }
                }
            }else{
                //拒绝消息 并重新入队 让别人继续解锁
                throw new RuntimeException("远程服务失败!");
            }

        }else{
            //无需解锁
        }

    }


    /**
     * 防止订单服务卡顿 库存优先到期，消费了消息，但是没有解锁库存
     * @param orderTo
     */
    @Transactional(rollbackFor = Exception.class)
    @Override
    public void unLockStock(OrderTo orderTo) {
        //解锁库存
        String orderSn = orderTo.getOrderSn();
        //查询库存解锁状态
        WareOrderTaskEntity wareOrderTaskEntity = wareOrderTaskService.getByOrderSn(orderSn);
        //找到所有没有解锁的库存
        List<WareOrderTaskDetailEntity> list = wareOrderTaskDetailService.list(new QueryWrapper<WareOrderTaskDetailEntity>()
                .eq("task_id", wareOrderTaskEntity.getId()).eq("lock_status", 1)
        );

        if(!CollectionUtils.isEmpty(list)){
            for (WareOrderTaskDetailEntity entity : list) {
                //Long skuId,Long wareId,Integer num,Long taskDetaidId
                //解锁
                unLockStock(entity.getSkuId(),entity.getWareId(),entity.getSkuNum(),entity.getId());
            }
        }

    }

    @Data
    class SkuWareHasStock {
        private Long skuId;
        private Integer num;
        private List<Long> wareId;
    }

}