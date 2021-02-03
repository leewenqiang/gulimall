package com.atguigu.search.service;

import com.atguigu.search.vo.SearchParam;
import com.atguigu.search.vo.SearchResult;

import java.io.IOException;

/**
 * @ClassName MailSearchService
 * @Description TODO
 * @Author lwq
 * @Date 2021/1/6 16:43
 * @Version 1.0
 */
public interface MailSearchService {

    /**
     *
     * @param searchParam 检索的所有参数
     * @return 返回检索结果
     */
    SearchResult search(SearchParam searchParam) ;
}
