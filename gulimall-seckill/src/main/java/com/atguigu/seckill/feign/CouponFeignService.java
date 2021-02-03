package com.atguigu.seckill.feign;

import com.atguigu.common.utils.R;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;

/**
 * @ClassName CouponFeignService
 * @Description TODO
 * @Author lwq
 * @Date 2021/1/31 21:43
 * @Version 1.0
 */
@FeignClient("gulimall-coupon")
public interface CouponFeignService {
    @GetMapping("/coupon/seckillsession/getLast3Session")
    R getLast3Session();
}
