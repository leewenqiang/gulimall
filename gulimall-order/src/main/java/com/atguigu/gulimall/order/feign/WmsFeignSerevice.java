package com.atguigu.gulimall.order.feign;

import com.atguigu.common.utils.R;
import com.atguigu.gulimall.order.vo.WareSkuLockVo;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

/**
 * @ClassName WmsFeignSerevice
 * @Description TODO
 * @Author lwq
 * @Date 2021/1/22 14:55
 * @Version 1.0
 */
@FeignClient("gulimall-ware")
public interface WmsFeignSerevice {

    @PostMapping("/ware/wareinfo/hasstock")
    R getSkusStock(@RequestBody List<Long> skuIds);

    /**
     * 查询运费和收货地址信息
     * @param addrId
     * @return
     */
    @GetMapping(value = "/ware/wareinfo/fare")
    R getFare(@RequestParam("addrId") Long addrId);


    @PostMapping(value = "/ware/wareinfo/orderLockStock")
    R orderLockStock(@RequestBody WareSkuLockVo lockVo);


//    @PostMapping(value = "/ware/waresku/lock/order")
//    R orderLockStock(@RequestBody WareSkuLockVo vo);
}
