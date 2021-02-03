package com.atguigu.search.service;

import com.alibaba.fastjson.JSON;
import com.atguigu.common.to.es.SkuEsModel;
import com.atguigu.search.config.GuLIMaillElasticsearchConfig;
import com.atguigu.search.constant.EsConatnt;
import lombok.extern.slf4j.Slf4j;
import org.elasticsearch.action.bulk.BulkRequest;
import org.elasticsearch.action.bulk.BulkResponse;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.common.xcontent.XContentType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.List;

/**
 * @ClassName ProductSaveServiceImpl
 * @Description TODO
 * @Author lwq
 * @Date 2021/1/2 12:24
 * @Version 1.0
 */
@Service
@Slf4j
public class ProductSaveServiceImpl implements ProductSaveService {

    /**
     * es
     */
    @Autowired
    private RestHighLevelClient restHighLevelClient;

    @Override
    public Boolean prodcuctStartUp(List<SkuEsModel> list) throws IOException {
        //给es建立索引
        //BulkRequest bulkRequest, RequestOptions options
        BulkRequest bulkRequest = new BulkRequest();
        for (SkuEsModel skuEsModel : list) {
            IndexRequest indexRequest = new IndexRequest(EsConatnt.PRODUCT_INDEX);
            indexRequest.id(skuEsModel.getSkuId().toString());
            indexRequest.source(JSON.toJSONString(skuEsModel), XContentType.JSON);
            bulkRequest.add(indexRequest);
        }
        BulkResponse bulk = restHighLevelClient.bulk(bulkRequest, GuLIMaillElasticsearchConfig.COMMON_OPTIONS);

        //TODO 处理错误
       return bulk.hasFailures();
    }
}
