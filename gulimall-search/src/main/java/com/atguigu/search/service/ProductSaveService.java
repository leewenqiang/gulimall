package com.atguigu.search.service;

import com.atguigu.common.to.es.SkuEsModel;

import java.io.IOException;
import java.util.List;

/**
 * @ClassName ProductSaveService
 * @Description TODO
 * @Author lwq
 * @Date 2021/1/2 12:23
 * @Version 1.0
 */
public interface ProductSaveService {
    Boolean prodcuctStartUp(List<SkuEsModel> list) throws IOException;
}
