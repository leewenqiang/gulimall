package com.atguigu.search.vo;

import lombok.Data;

import java.util.List;

/**
 * @ClassName SearchParam
 * @Description 封装页面可能传递过来的查询条件
 * @Author lwq
 * @Date 2021/1/6 16:41
 * @Version 1.0
 *
 * catalog3Id=250&keyword=小米&sort=saleCount_desc&hasStock=0/1&brandId=111&brandId=112
 *
 */
@Data
public class SearchParam {

    /**
     * 页面传递的检索参数
     * 全文匹配关键字
     */
    private String keyword;
    /**
     * 三级分类id
     */
    private Long catalog3Id;

    /**
     * 排序条件
     * sort=saleCount_desc/asc
     * sort=skuPrice_asc/desc
     * sort=hotScore_asc/desc
     */
    private String sort;


    /**
     * 过滤条件
     * hasStock(是否有货)
     * skuPrice(价格区间)skuPrice=1_500/_500/500_
     *  brandId=111
     *  属性：
     *  attrs=1_其他:安卓&attrs=2_5存:6寸
     *
     */
    /**
     * 是否只显示有货
     */
    private Integer hasStock;

    /**
     * 价格区间
     */
    private String skuPrice;


    /**
     * 品牌id
     */
    private List<Long> brandId;

    /**
     * 属性
     */
    private List<String> attrs;

    /**
     * 页码
     */
    private Integer pageNum;

    /**
     * 查询条件
     */
    private String _queryString;


}
