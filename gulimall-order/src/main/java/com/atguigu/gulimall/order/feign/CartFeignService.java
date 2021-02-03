package com.atguigu.gulimall.order.feign;

import com.atguigu.gulimall.order.vo.OrderItem;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;

@FeignClient("gulimall-cart")
public interface CartFeignService {

    @GetMapping("/{memberId}/getCartItemByUserId")
    List<OrderItem> getCartItemByUserId(@PathVariable Long memberId );
}
