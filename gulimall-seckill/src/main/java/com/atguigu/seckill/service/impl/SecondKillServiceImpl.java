package com.atguigu.seckill.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.atguigu.common.to.SecKIllTo;
import com.atguigu.common.utils.R;
import com.atguigu.seckill.feign.CouponFeignService;
import com.atguigu.seckill.feign.ProductFeignService;
import com.atguigu.seckill.intercepter.LoginUserIntercepter;
import com.atguigu.seckill.service.SecondKillService;
import com.atguigu.seckill.to.SecondKillRedisTo;
import com.atguigu.seckill.vo.SeckillSessionVo;
import com.atguigu.seckill.vo.SkuInfoVo;
import com.baomidou.mybatisplus.core.toolkit.IdWorker;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RSemaphore;
import org.redisson.api.RedissonClient;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.BoundHashOperations;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.time.Duration;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * @ClassName SecondKillServiceImpl
 * @Description TODO
 * @Author lwq
 * @Date 2021/1/31 21:41
 * @Version 1.0
 */
@Slf4j
@Service
public class SecondKillServiceImpl implements SecondKillService {

    @Autowired
    CouponFeignService couponFeignService;

    @Autowired
    ProductFeignService productFeignService;

    @Autowired
    StringRedisTemplate redisTemplate;

    @Autowired
    RedissonClient redissonClient;

    @Autowired
    RabbitTemplate rabbitTemplate;

    private final String SESSIONS_PREFIX_CACHE_PREFIX = "seckill:sessions:";
    private final String SKU_KILL_PREFIX_CACHE_PREFIX = "seckill:skus:";

    private final String SKU_STOCK_SEMAPHORE_PREFIX_CACHE_PREFIX = "seckill:stock:";// 加商品随机码


    private final String SECKILL_USER_UNIQUE_KEY_PREIFIX = "seckill:uniquekey:";// 占位码


    /**
     * TODO  应该设置过期时间
     *
     */
    @Override
    public void uploadSecondKillSkulast3Days() {

        //扫描需要秒杀的活动
        R last3Session = couponFeignService.getLast3Session();
        if (last3Session.getCode() == 0) {
            List<SeckillSessionVo> data = last3Session.getData(new TypeReference<List<SeckillSessionVo>>() {
            });
            //缓存redis
            //1、活动信息  2、活动商品信息
            saveSessionInfo(data);
            saveSessionSkuInfo(data);
        }


    }

    @Override
    public List<SecondKillRedisTo> getCurrentSecKill() {


        //需要返回的数据
        List<SecondKillRedisTo> currentSecondKillRedisTos = new ArrayList<>();

        //1、确定秒杀场次
        //2、获取秒杀场次的需要的商品信息

        long nowTime = System.currentTimeMillis();

        //获取到redis的场次
        Set<String> keys = redisTemplate.keys(SESSIONS_PREFIX_CACHE_PREFIX + "*");
        //遍历keys
        if (!CollectionUtils.isEmpty(keys)) {
            Set<String> collect = keys.stream().filter(key -> {
                //取到分割的时间
                String timeArr = key.substring(key.lastIndexOf(":") + 1, key.length());
                String[] s = timeArr.split("_");
                String startTime = timeArr.split("_")[0];
                String endTime = timeArr.split("_")[1];
                if (nowTime >= Long.parseLong(startTime) && nowTime <= Long.parseLong(endTime)) {
                    return true;
                } else {
                    return false;
                }
            }).collect(Collectors.toSet());

            //获取操作对象
            BoundHashOperations<String, Object, Object> operations = redisTemplate.boundHashOps(SKU_KILL_PREFIX_CACHE_PREFIX);
            if (!CollectionUtils.isEmpty(collect)) {
                //根据场次信息得到商品信息
                collect.forEach(c -> {
                    List<String> range = redisTemplate.opsForList().range(c, 0, redisTemplate.opsForList().size(c));
                    if (!CollectionUtils.isEmpty(range)) {
                        for (String s : range) {
                            //获取数据，转为对应的对象
                            String o = (String) operations.get(s);
                            SecondKillRedisTo secondKillRedisTo = JSON.parseObject(o, SecondKillRedisTo.class);
                            //随机码不返回
//                            secondKillRedisTo.setRandomCode(null);
                            currentSecondKillRedisTos.add(secondKillRedisTo);
                        }
                    }
                });

            }
        }


        return currentSecondKillRedisTos;
    }

    @Override
    public SecondKillRedisTo getSKuSecKill(Long skuId) {

        //1、找到所有参与秒杀的商品
        //当前sku参与的秒杀 找到
        BoundHashOperations<String, String, String> operations =
                redisTemplate.boundHashOps(SKU_KILL_PREFIX_CACHE_PREFIX);
        Set<String> keys = operations.keys();
        //找到离当前时间最近的关于当前sku的秒杀活动
        if (!CollectionUtils.isEmpty(keys)) {
            List<String> values = keys.stream().filter(key -> {
                Long value = Long.parseLong(key.split("_")[1]);
                return value.equals(skuId);
            }).collect(Collectors.toList());


            if (!CollectionUtils.isEmpty(values)) {

                List<String> list = operations.multiGet(values);
                //先要去除 结束时间大于等于当前时间的数据

                List<SecondKillRedisTo> collect = list.stream().map(r -> {
                    System.out.println(r);
                    SecondKillRedisTo secondKillRedisTo = JSON.parseObject(r, SecondKillRedisTo.class);
                    return secondKillRedisTo;
                }).filter(r -> System.currentTimeMillis() <= r.getEndTime()).collect(Collectors.toList());

                System.out.println("collect======" + collect);

                System.out.println("current:" + System.currentTimeMillis());

                if (!CollectionUtils.isEmpty(collect)) {
                    SecondKillRedisTo secondKillRedisTo = collect.stream().sorted((x, y) -> x.getStartTime().compareTo(y.getStartTime())).collect(Collectors.toList()).get(0);
                    return secondKillRedisTo;
                }

                //当前满足条件的所有key
                /*long cuurentTime = System.currentTimeMillis();
                Iterator<String> iterator = values.iterator();
                //获取第一个元素
                String s1  =  iterator.next();
                SecondKillRedisTo secondKillRedisToFirst = JSON.parseObject(operations.get(s1), SecondKillRedisTo.class);
                long minDistince = Math.abs(secondKillRedisToFirst.getStartTime() - cuurentTime);
                boolean isFirstItemMin = false;
                while (iterator.hasNext()){
                    String value = iterator.next();
                    String s = operations.get(value);
                    SecondKillRedisTo secondKillRedisTo = JSON.parseObject(s, SecondKillRedisTo.class);
                    //距离绝对值
                    long abs = Math.abs(secondKillRedisTo.getStartTime() - cuurentTime);
                    if(abs> minDistince){
                        //移除比当前元素大的距离
                        iterator.remove();
                    }else{

                    }
                }*/

//                for (String value : values) {
//                    String s = operations.get(value);
//                    SecondKillRedisTo secondKillRedisTo = JSON.parseObject(s, SecondKillRedisTo.class);
//                    //距离绝对值
//                    long abs = Math.abs(secondKillRedisTo.getStartTime() - cuurentTime);
//                    if(abs< minDistince){
//
//                        minDistince=abs;
//                    }
//                }
            }


            //重新排个序
//             keys = keys.stream().sorted().collect(Collectors.toSet());
           /* for (String key : keys) {
                Long value = Long.parseLong(key.split("_")[1]);
                if(skuId.equals(value)){
                    //找到元素
                    String s = operations.get(key);
                    SecondKillRedisTo secondKillRedisTo = JSON.parseObject(s, SecondKillRedisTo.class);
                    Long startTime = secondKillRedisTo.getStartTime();
                    Long endTime = secondKillRedisTo.getEndTime();
                    if(System.currentTimeMillis()>=startTime && System.currentTimeMillis()<=endTime){

                    }else{
                        secondKillRedisTo.setRandomCode(null);
                    }
                    return secondKillRedisTo;
                }
            }*/
        }
        return null;
    }

    @Override
    public String kill(String killId, String code, Integer num) {

//        LocalDateTime start = LocalDateTime.now();
        Instant start = Instant.now();


        BoundHashOperations<String, String, String> operations =
                redisTemplate.boundHashOps(SKU_KILL_PREFIX_CACHE_PREFIX);
        Long userId = LoginUserIntercepter.threadLocal.get().getId();

        String res = operations.get(killId);
        if (StringUtils.isEmpty(res)) {
            return null;
        } else {
            SecondKillRedisTo secondKillRedisTo = JSON.parseObject(res, SecondKillRedisTo.class);
            //校验合法性
            // 1、秒杀时间是否过了
            Long startTime = secondKillRedisTo.getStartTime();
            Long endTime = secondKillRedisTo.getEndTime();
            Long currentTime = System.currentTimeMillis();
            Long skuId = secondKillRedisTo.getSkuId();
            String randomCode = secondKillRedisTo.getRandomCode();
            Long promotionSessionId = secondKillRedisTo.getPromotionSessionId();
            long ttl = endTime - startTime;
            String key = promotionSessionId + "_" + skuId;
            //1、校验时间合法性
            if (currentTime >= startTime && currentTime <= endTime) {
                //2、校验随机码和商品ID
                if (randomCode.equals(code) && key.equals(killId)) {
                    //3、判断购物数据是否合理
                    int i = secondKillRedisTo.getSeckillLimit().intValue();
                    if (num <= i) {
                        //4、验证是否买过了  幂等性处理  去redis占位  userId+promotionSessionId+skuId
                        String redisKey = SECKILL_USER_UNIQUE_KEY_PREIFIX+userId + "_" + key;
                        //占位  自动过期 过期时间为活动结束时间-活动开始时间
                        Boolean zhanWeiState = redisTemplate.opsForValue().setIfAbsent(redisKey, num.toString(), ttl, TimeUnit.MILLISECONDS);
                        if (zhanWeiState) {
                            //占位成功 说明之前没有买过
                            // ----------------通过核验操作--------
                            // 获取信号量
                            RSemaphore semaphore = redissonClient.getSemaphore(SKU_STOCK_SEMAPHORE_PREFIX_CACHE_PREFIX + code);
                            /*try {
                                //acquire 是阻塞的 会等待别人释放信号量 不好 应该用
                                semaphore.acquire(num);

                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }*/

                            boolean b = semaphore.tryAcquire(num);
                            if(b) {

                                //拿到信号量 秒杀成功  200-10000
                                // 快速下单 给MQ发送消息
                                //订单号
                                String timeId = IdWorker.getTimeId();

                                SecKIllTo secKIllTo = new SecKIllTo();
                                secKIllTo.setOderSn(timeId);
                                secKIllTo.setNum(num);
                                secKIllTo.setPromotionSessionId(promotionSessionId);
                                secKIllTo.setSeckillPrice(secondKillRedisTo.getSeckillPrice());
                                secKIllTo.setSkuId(skuId);
                                secKIllTo.setMemberId(userId);


                                //发送MQ消息
                                rabbitTemplate.convertAndSend("order-event-exchange",
                                        "order.seckill.order",
                                        secKIllTo);
//                                LocalDateTime end = LocalDateTime.now();
                                Instant end = Instant.now();
                                log.info("时间耗时=="+ Duration.between(start,end).toMillis());

                                return timeId;

                            }

                           /* try {
                                boolean b = semaphore.tryAcquire(num, 10, TimeUnit.MILLISECONDS);
                                if(b){
                                    //拿到信号量 秒杀成功  200-10000
                                    // 快速下单 给MQ发送消息
                                    //订单号
                                    String timeId = IdWorker.getTimeId();

                                    SecKIllTo secKIllTo = new SecKIllTo();
                                    secKIllTo.setOderSn(timeId);
                                    secKIllTo.setNum(num);
                                    secKIllTo.setPromotionSessionId(promotionSessionId);
                                    secKIllTo.setSeckillPrice(secondKillRedisTo.getSeckillPrice());
                                    secKIllTo.setSkuId(skuId);
                                    secKIllTo.setMemberId(userId);


                                    //发送MQ消息
                                    rabbitTemplate.convertAndSend("order-event-exchange",
                                            "order.seckill.order",
                                            secKIllTo);

                                    return timeId;
                                }

                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }*/

                        }

                    }
                }
            }

        }


        return null;

    }

    /**
     * 保存商品信息到redis
     *
     * @param data
     */
    private void saveSessionSkuInfo(List<SeckillSessionVo> data) {

        BoundHashOperations<String, Object, Object> ops = redisTemplate.boundHashOps(SKU_KILL_PREFIX_CACHE_PREFIX);


        data.stream().forEach(sessoion -> {

            sessoion.getRelationSkus().stream().forEach(sku -> {

                String token = UUID.randomUUID().toString().replace("-", "");

                if (!ops.hasKey(sku.getPromotionSessionId().toString() + "_" + sku.getSkuId().toString())) {
                    //缓存商品
                    SecondKillRedisTo secondKillRedisTo = new SecondKillRedisTo();
                    //1、sku基本信息
                    R r = productFeignService.skuInfo(sku.getSkuId());
                    if (r.getCode() == 0) {
                        SkuInfoVo skuInfo = r.getData("skuInfo", new TypeReference<SkuInfoVo>() {
                        });
                        secondKillRedisTo.setSkuInfoVo(skuInfo);
                    }

                    // 2、秒杀信息
                    BeanUtils.copyProperties(sku, secondKillRedisTo);

                    //3、设置秒杀商品的开始时间和结束时间
                    secondKillRedisTo.setStartTime(sessoion.getStartTime().getTime());
                    secondKillRedisTo.setEndTime(sessoion.getEndTime().getTime());

                    //4、随机码 secondkill?skuid=1&key=sadas
                    secondKillRedisTo.setRandomCode(token);


                    String jsonString = JSON.toJSONString(secondKillRedisTo);
                    // key 为场次id+skuId
                    ops.put(sku.getPromotionSessionId().toString() + "_" + sku.getSkuId().toString(), jsonString);

                    //获取信号量
                    RSemaphore semaphore = redissonClient
                            .getSemaphore(SKU_STOCK_SEMAPHORE_PREFIX_CACHE_PREFIX + token);
                    //商品可以秒杀的数量作为信号量 限流
                    //引入分布式的信号量
                    semaphore.trySetPermits(sku.getSeckillCount().intValue());

                }


                // 移到上面处理
              /*  if(!redisTemplate.hasKey(SKU_STOCK_SEMAPHORE_PREFIX_CACHE_PREFIX + token)){
                    //获取信号量
                    RSemaphore semaphore = redissonClient
                            .getSemaphore(SKU_STOCK_SEMAPHORE_PREFIX_CACHE_PREFIX + token);
                    //商品可以秒杀的数量作为信号量 限流
                    //引入分布式的信号量
                    semaphore.trySetPermits(sku.getSeckillCount().intValue());
                }*/


            });

        });
    }

    /**
     * 保存活动信息
     *
     * @param data
     */
    private void saveSessionInfo(List<SeckillSessionVo> data) {
        data.stream().forEach(sessoion -> {
            Long startTime = sessoion.getStartTime().getTime();
            Long endTime = sessoion.getEndTime().getTime();

            //key
            String key = SESSIONS_PREFIX_CACHE_PREFIX + startTime + "_" + endTime;
            Boolean aBoolean = redisTemplate.hasKey(key);
            if (!aBoolean) {
                //value
                List<String> collect = sessoion.getRelationSkus().stream().map(item -> item.getId() + "_" + item.getSkuId().toString()).collect(Collectors.toList());
                //存入数据
                redisTemplate.opsForList().leftPushAll(key, collect);
            }
        });
    }
}
