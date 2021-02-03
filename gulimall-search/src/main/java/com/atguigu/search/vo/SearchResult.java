package com.atguigu.search.vo;

import com.atguigu.common.to.es.SkuEsModel;
import lombok.Data;

import java.util.List;

/**
 * @ClassName SearchResponse
 * @Description TODO
 * @Author lwq
 * @Date 2021/1/6 17:10
 * @Version 1.0
 */
@Data
public class SearchResult {
    /**
     * 商品信息
     */
    private List<SkuEsModel> products;


   // ======================分页信息=======================
    /**
     * 当前页面
     */
    private Integer pageNum;

    /**
     * 总记录数
     */
    private Long total;

    /**
     * 总页码
     */
    private Integer totalPages;

    private List<Integer> pageNavs;


    /**
     * 面包屑导航
     *
     */
    private List<NavVo> navs;

    // ======================分页信息=======================


    /**
     * 查询到的结果涉及到的品牌
     */
    private List<BrandVo> brands;

    /**
     * 当前查询到的结果涉及到的所有属性
     */
    private List<AttrVo> attrs;

    /**
     * 当前查询到的结果涉及到的所有分类
     *
     */
    private List<CatalogVo> catalogs;


    @Data
    public static class NavVo{
        String navName;
        String navValue;
        String link;
    }

    @Data
    public static class BrandVo{
        private Long brandId;
        private String brandName;
        private String brandImg;
    }

    @Data
    public static class CatalogVo{
        private Long catalogId;
        private String catalogName;
    }


    @Data
    public static class AttrVo{
        private Long attrId;
        private String attrName;
        private List<String> attrValue;
    }




}
