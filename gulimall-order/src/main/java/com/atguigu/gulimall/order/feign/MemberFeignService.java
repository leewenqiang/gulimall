package com.atguigu.gulimall.order.feign;


import com.atguigu.gulimall.order.vo.MemberReceiveAddressVo;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;

@FeignClient("gulimall-member")
public interface MemberFeignService {

    @GetMapping("/member/memberreceiveaddress/{memberId}/address")
    List<MemberReceiveAddressVo> getMemberReceiveAddressEntity(@PathVariable("memberId") Long memberId);

}
