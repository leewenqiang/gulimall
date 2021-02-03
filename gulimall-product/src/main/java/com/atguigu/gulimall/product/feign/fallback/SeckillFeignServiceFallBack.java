package com.atguigu.gulimall.product.feign.fallback;

import com.atguigu.common.exception.BizCodeEnum;
import com.atguigu.common.utils.R;
import com.atguigu.gulimall.product.feign.SecKillFeignService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * @ClassName SeckillFeignServiceFallBack
 * @Description TODO
 * @Author lwq
 * @Date 2021/2/3 9:34
 * @Version 1.0
 */
@Slf4j
@Component
public class SeckillFeignServiceFallBack implements SecKillFeignService {
    @Override
    public R getSKuSecKill(Long skuId) {

        log.info("熔断方法调用");
        return R.error(BizCodeEnum.TOO_MANY_RQUEST.getCode(),BizCodeEnum.TOO_MANY_RQUEST.getMsg());
    }
}
