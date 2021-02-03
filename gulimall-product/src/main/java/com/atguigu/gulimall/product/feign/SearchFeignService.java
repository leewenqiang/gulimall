package com.atguigu.gulimall.product.feign;

import com.atguigu.common.to.es.SkuEsModel;
import com.atguigu.common.utils.R;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;

/**
 * @ClassName SearchFeignService
 * @Description TODO
 * @Author lwq
 * @Date 2021/1/2 12:40
 * @Version 1.0
 */
@FeignClient("gulimall-search")
public interface SearchFeignService {

    @PostMapping("/search/save/product")
    public R prodcuctStartUp(@RequestBody List<SkuEsModel> list)  ;
}
