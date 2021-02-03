package com.atguigu.search.service;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.atguigu.common.to.es.SkuEsModel;
import com.atguigu.common.utils.R;
import com.atguigu.search.config.GuLIMaillElasticsearchConfig;
import com.atguigu.search.constant.EsConatnt;
import com.atguigu.search.feigin.ProductFeignService;
import com.atguigu.search.vo.AttrResponseVo;
import com.atguigu.search.vo.SearchParam;
import com.atguigu.search.vo.SearchResult;
import org.apache.commons.lang.StringUtils;
import org.apache.lucene.search.join.ScoreMode;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.NestedQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.index.query.RangeQueryBuilder;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.SearchHits;
import org.elasticsearch.search.aggregations.AggregationBuilders;
import org.elasticsearch.search.aggregations.Aggregations;
import org.elasticsearch.search.aggregations.bucket.nested.NestedAggregationBuilder;
import org.elasticsearch.search.aggregations.bucket.nested.ParsedNested;
import org.elasticsearch.search.aggregations.bucket.terms.ParsedLongTerms;
import org.elasticsearch.search.aggregations.bucket.terms.ParsedStringTerms;
import org.elasticsearch.search.aggregations.bucket.terms.Terms;
import org.elasticsearch.search.aggregations.bucket.terms.TermsAggregationBuilder;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightBuilder;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightField;
import org.elasticsearch.search.sort.SortOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @ClassName MailSearchServiceImpl
 * @Description TODO
 * @Author lwq
 * @Date 2021/1/6 16:43
 * @Version 1.0
 */
@Service
public class MailSearchServiceImpl implements MailSearchService {



    public static final String SKUPRICE_SPERETOR = "_";
    public static final int SKUPRICE_SIZE = 2;
    public static final int SKUPRICE_SIZE_ONE = 1;
    @Autowired
    private RestHighLevelClient restHighLevelClient;

    @Autowired
    private ProductFeignService productFeignService;

    @Override
    public SearchResult search(SearchParam searchParam)  {

        SearchResult searchResult = null;

        //构造DSL
        SearchRequest searchRequest = buildSearchRequuest(searchParam);


        try {
            //执行请求
            SearchResponse result = restHighLevelClient.search(searchRequest, GuLIMaillElasticsearchConfig.COMMON_OPTIONS);

            //封装响应数据
            searchResult =  buildSearchResult(result,searchParam);

        } catch (IOException e) {
            e.printStackTrace();
        }

        return searchResult;
    }

    private SearchResult buildSearchResult(SearchResponse response,SearchParam searchParam) {
        SearchResult searchResult = new SearchResult();


        SearchHits hits = response.getHits();

        long total = hits.getTotalHits().value;
        //总记录数
        searchResult.setTotal(total);
        int totalPage = (int)total % EsConatnt.PRODUCT_PAGE_SZIE == 0 ? (int)total / EsConatnt.PRODUCT_PAGE_SZIE : ((int)total / EsConatnt.PRODUCT_PAGE_SZIE + 1);
        searchResult.setTotalPages(totalPage);


        List<Integer> pageNavs = new ArrayList<>();
        for(int i=1;i<=totalPage;i++){
            pageNavs.add(i);
        }
        searchResult.setPageNavs(pageNavs);

        //页码
       searchResult.setPageNum(searchParam.getPageNum()==null ? 0 : searchParam.getPageNum());

       List<SkuEsModel> esModels = new ArrayList<>();
        SearchHit[] hitsRecords = hits.getHits();
       if(hitsRecords != null && hitsRecords.length>0){
           //命中的记录数
           for (SearchHit hit : hitsRecords) {
               String sourceAsString = hit.getSourceAsString();
               //检索得到的对象
               SkuEsModel  skuEsModel = JSON.parseObject(sourceAsString,SkuEsModel.class);
               if(StringUtils.isNotEmpty(searchParam.getKeyword())){
                   //高亮title
                   HighlightField skuTitle = hit.getHighlightFields().get("skuTitle");
                   String string = skuTitle.fragments()[0].string();
                   skuEsModel.setSkuTitle(string);
               }
               esModels.add(skuEsModel);
           }
       }
       //设置商品值
        searchResult.setProducts(esModels);

        //products
        //attrs

        //聚合
        Aggregations aggregations = response.getAggregations();
        //分类的聚合
        ParsedLongTerms catalogAgg = aggregations.get("catalog-agg");
        List<SearchResult.CatalogVo> catalogVos = new ArrayList<>();
        List<? extends Terms.Bucket> buckets = catalogAgg.getBuckets();
        for (Terms.Bucket bucket : buckets) {
            SearchResult.CatalogVo catalogVo = new SearchResult.CatalogVo();
            //分类Id
            long catalogId = bucket.getKeyAsNumber().longValue();
            catalogVo.setCatalogId(catalogId);
            //分类名称
            ParsedStringTerms catalogNameAgg = bucket.getAggregations().get("catalogNameAgg");
            String catalogName = catalogNameAgg.getBuckets().get(0).getKeyAsString();
            catalogVo.setCatalogName(catalogName);
            catalogVos.add(catalogVo);
        }
        searchResult.setCatalogs(catalogVos);

        //品牌
        List<  SearchResult.BrandVo > brandVos = new ArrayList<>();
        ParsedLongTerms brandAgg = aggregations.get("brand-agg");
        for (Terms.Bucket bucket : brandAgg.getBuckets()) {
            SearchResult.BrandVo brandVo = new SearchResult.BrandVo();
            long brandId = bucket.getKeyAsNumber().longValue();
            brandVo.setBrandId(brandId);

            //图片
            ParsedStringTerms brandImgAgg = bucket.getAggregations().get("brandImgAgg");
            String brandImg = brandImgAgg.getBuckets().get(0).getKeyAsString();
            brandVo.setBrandImg(brandImg);

            //名称
            ParsedStringTerms brandNameAgg = bucket.getAggregations().get("brandNameAgg");
            String brandName = brandNameAgg.getBuckets().get(0).getKeyAsString();
            brandVo.setBrandName(brandName);

            brandVos.add(brandVo);

        }
        searchResult.setBrands(brandVos);


        //属性
        List<SearchResult.AttrVo> attrVos = new ArrayList<>();
        ParsedNested attrAgg = aggregations.get("attr-agg");
        ParsedLongTerms attrIdAgg = attrAgg.getAggregations().get("attr-id-agg");
        for (Terms.Bucket bucket : attrIdAgg.getBuckets()) {
            SearchResult.AttrVo attrVo = new SearchResult.AttrVo();
            long attrId = bucket.getKeyAsNumber().longValue();
            attrVo.setAttrId(attrId);

            ParsedStringTerms attrnameagg = bucket.getAggregations().get("attr-name-agg");
            String keyAsString = attrnameagg.getBuckets().get(0).getKeyAsString();
            attrVo.setAttrName(keyAsString);


            ParsedStringTerms attrvalueagg = bucket.getAggregations().get("attr-value-agg");
            List<String> collect = attrvalueagg.getBuckets().stream().map(item -> {
                return ((Terms.Bucket) item).getKeyAsString();
            }).collect(Collectors.toList());
            attrVo.setAttrValue(collect);

            attrVos.add(attrVo);

        }
        searchResult.setAttrs(attrVos);



        if(!CollectionUtils.isEmpty(searchParam.getAttrs())){
            //构建面包屑导航
            List<SearchResult.NavVo> navVos =   searchParam.getAttrs().stream().map(attr->{
                SearchResult.NavVo navVo = new SearchResult.NavVo();

                String[] s = attr.split("_");
                //值
                navVo.setNavValue(s[1]);
                R info = productFeignService.info(Long.parseLong((s[0])));
                if(info.getCode()==0){
                    AttrResponseVo attrResponseVo =   info.getData("attr",new TypeReference<AttrResponseVo>(){});
                    //名字
                    navVo.setNavName(attrResponseVo.getAttrName());
                }else{
                    navVo.setNavName(s[0]);
                }

                //取消了面包屑跳转的地方
                // 将请求的地址的url替换
                //拿到所有查询条件
                String encode = null;
                try {
                    encode = URLEncoder.encode(attr, "UTF-8");
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }
                String replace = searchParam.get_queryString().replace("&attrs=" + encode, "");
                navVo.setLink("http://search.gulimall.com/list.html?"+replace);


                return  navVo;
            }).collect(Collectors.toList());

            searchResult.setNavs(navVos);
        }



        return searchResult;
    }

    private SearchRequest buildSearchRequuest(SearchParam searchParam) {
        String keyword = searchParam.getKeyword();
        Long catalog3Id = searchParam.getCatalog3Id();
        List<Long> brandId = searchParam.getBrandId();
        Integer hasStock = searchParam.getHasStock();
        String skuPrice = searchParam.getSkuPrice();
        List<String> attrs = searchParam.getAttrs();
        String sort = searchParam.getSort();
        Integer pageNum = searchParam.getPageNum();

        SearchSourceBuilder sourceBuilder = new SearchSourceBuilder();
        //商品名称 是否有库存 价格区间 分类  排序 品牌 属性 分页
        SearchRequest searchRequest = new SearchRequest(new String[]{EsConatnt.PRODUCT_INDEX},sourceBuilder);

        /**
         * 构造查询条件
         */
        BoolQueryBuilder boolQueryBuilder = QueryBuilders.boolQuery();
        if(StringUtils.isNotEmpty(keyword)){
            boolQueryBuilder.must(QueryBuilders.matchQuery("skuTitle", keyword));
        }

        // filter
        if(catalog3Id  != null){
            boolQueryBuilder.filter(QueryBuilders.termQuery("catalogId",catalog3Id));
        }
        if(!CollectionUtils.isEmpty(brandId)){
            boolQueryBuilder.filter(QueryBuilders.termsQuery("brandId",brandId));
        }
        if(hasStock!=null){
            boolQueryBuilder.filter(QueryBuilders.termQuery("hasStock",hasStock==1));
        }

        if(StringUtils.isNotEmpty(skuPrice)){
            RangeQueryBuilder rangeQueryBuilder = QueryBuilders.rangeQuery("skuPrice");
            skuPrice = skuPrice.trim();
            String[] s = skuPrice.split(SKUPRICE_SPERETOR);
            if(s.length== SKUPRICE_SIZE){
                if(StringUtils.isNotEmpty(s[0])){
                    rangeQueryBuilder.gte(s[0]);
                }
                if(StringUtils.isNotEmpty(s[1])){
                    rangeQueryBuilder.lte(s[1]);
                }
            }else if(s.length== SKUPRICE_SIZE_ONE){
               if(skuPrice.startsWith(SKUPRICE_SPERETOR)){
                   rangeQueryBuilder.lte(s[0]);
               }else if(skuPrice.endsWith(SKUPRICE_SPERETOR)){
                   rangeQueryBuilder.gte(s[0]);
               }
            }
            boolQueryBuilder.filter(rangeQueryBuilder);
        }

        //构造 多查询条件
        if(!CollectionUtils.isEmpty(attrs)){

            for (String attr : attrs) {

                BoolQueryBuilder nestedBollQuery = QueryBuilders.boolQuery();

                String[] split = attr.split(SKUPRICE_SPERETOR);
                //属性ID
                String attrId = split[0];
                //属性值
                String[] attrValues = split[1].split(":");

                nestedBollQuery.must(QueryBuilders.termQuery("attrs.attrId",attrId));
                nestedBollQuery.must(QueryBuilders.termsQuery("attrs.attrValue",attrValues));

                //每一个都必须生成一个嵌入式的nested query
                NestedQueryBuilder nesatedQuery = QueryBuilders.
                        nestedQuery("attrs", nestedBollQuery, ScoreMode.None);

                boolQueryBuilder.filter(nesatedQuery);
            }

        }

        //排序
        if(StringUtils.isNotEmpty(sort)){
            String[] split = sort.split(SKUPRICE_SPERETOR);
            String order = split[1];
            sourceBuilder.sort(split[0],order.equalsIgnoreCase("asc")? SortOrder.ASC:SortOrder.DESC);
        }
        //分页
        // pageNum 0-5  from=(pageNum-1)*pageSiz
        if(pageNum==null){
            pageNum=1;
        }
        sourceBuilder.from((pageNum-1)*EsConatnt.PRODUCT_PAGE_SZIE);
        sourceBuilder.size(EsConatnt.PRODUCT_PAGE_SZIE);


        //高亮
        if(StringUtils.isNotEmpty(keyword)){
            HighlightBuilder highlightBuilder = new HighlightBuilder();
            highlightBuilder.field("skuTitle");
            highlightBuilder.preTags("<b style='color:red'/>");
            highlightBuilder.postTags("</b>");
            sourceBuilder.highlighter(highlightBuilder);
        }

        sourceBuilder.query(boolQueryBuilder);

//        System.out.println("DSL"+sourceBuilder.toString());


        //品牌
        TermsAggregationBuilder brandagg = AggregationBuilders.terms("brand-agg").field("brandId").size(50);
        //子聚合
        brandagg.subAggregation( AggregationBuilders.terms("brandNameAgg").field("brandName").size(1));
        brandagg.subAggregation(AggregationBuilders.terms("brandImgAgg").field("brandImg").size(1));
        sourceBuilder.aggregation(brandagg);


        //分类聚合
        TermsAggregationBuilder catalogAgg = AggregationBuilders.terms("catalog-agg").field("catalogId").size(50);
        catalogAgg.subAggregation(AggregationBuilders.terms("catalogNameAgg").field("catalogName").size(1));
        sourceBuilder.aggregation(catalogAgg);

        //属性聚合
        NestedAggregationBuilder nestedAggBuild = AggregationBuilders.nested("attr-agg", "attrs");
        TermsAggregationBuilder attridagg = AggregationBuilders.terms("attr-id-agg").field("attrs.attrId").size(50);
        attridagg.subAggregation(AggregationBuilders.terms("attr-name-agg").field("attrs.attrName").size(1));
        attridagg.subAggregation(AggregationBuilders.terms("attr-value-agg").field("attrs.attrValue").size(50));
        nestedAggBuild.subAggregation(attridagg);


        sourceBuilder.aggregation(nestedAggBuild);
        System.out.println("DSL"+sourceBuilder.toString());

        return searchRequest;

    }
}
