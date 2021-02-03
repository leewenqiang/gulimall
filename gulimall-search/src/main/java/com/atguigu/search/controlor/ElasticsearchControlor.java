package com.atguigu.search.controlor;

import com.atguigu.common.exception.BizCodeEnum;
import com.atguigu.common.to.es.SkuEsModel;
import com.atguigu.common.utils.R;
import com.atguigu.search.service.ProductSaveService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * @ClassName ElasticsearchControlor
 * @Description TODO
 * @Author lwq
 * @Date 2021/1/2 12:20
 * @Version 1.0
 */
@RequestMapping("/search/save")
@RestController
@Slf4j
public class ElasticsearchControlor {

    @Autowired
    private ProductSaveService productSaveService;
    //上架商品
    @PostMapping("/product")
    public R prodcuctStartUp(@RequestBody List<SkuEsModel> list)  {
        boolean flag = false;
        try{
            flag = productSaveService.prodcuctStartUp(list);
        }catch (Exception e){
            log.error("商品上架错误{}"+e);
            return R.error(BizCodeEnum.PRODUCT_UP_ERROR.getCode(),BizCodeEnum.PRODUCT_UP_ERROR.getMsg());

        }
        if(flag){
            return R.error(BizCodeEnum.PRODUCT_UP_ERROR.getCode(),BizCodeEnum.PRODUCT_UP_ERROR.getMsg());
        }else{
            return R.ok();
        }
    }

}
