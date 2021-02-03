package com.atguigu.search.controlor;

import com.atguigu.search.service.MailSearchService;
import com.atguigu.search.vo.SearchParam;
import com.atguigu.search.vo.SearchResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import javax.servlet.http.HttpServletRequest;

/**
 * @ClassName SearchControlor
 * @Description TODO
 * @Author lwq
 * @Date 2021/1/6 16:07
 * @Version 1.0
 */
@Controller
public class SearchControlor {


    @Autowired
    private MailSearchService mailSearchService;

    /**
     * 将页面提交的所有请求查询参数封装成指定的对象
     * @param searchParam
     * @return
     */
    @GetMapping("/list.html")
    public String listPage(SearchParam searchParam, Model model, HttpServletRequest servletRequest){
        //根据传递来的页面查询参数 去es中查询
        String queryString = servletRequest.getQueryString();
        searchParam.set_queryString(queryString);
        SearchResult result = mailSearchService.search(searchParam);
        //接收检索条件
        model.addAttribute("result",result);
        return "list";
    }
}
