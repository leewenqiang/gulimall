//package com.atguigu.search;
//
//
//import com.alibaba.fastjson.JSON;
//import com.atguigu.search.config.GuLIMaillElasticsearchConfig;
//import lombok.Data;
//import lombok.ToString;
//import org.elasticsearch.action.index.IndexRequest;
//import org.elasticsearch.action.index.IndexResponse;
//import org.elasticsearch.action.search.SearchRequest;
//import org.elasticsearch.action.search.SearchResponse;
//import org.elasticsearch.client.RestHighLevelClient;
//import org.elasticsearch.common.xcontent.XContentType;
//import org.elasticsearch.index.query.MatchQueryBuilder;
//import org.elasticsearch.index.query.QueryBuilders;
//import org.elasticsearch.search.SearchHit;
//import org.elasticsearch.search.aggregations.Aggregation;
//import org.elasticsearch.search.aggregations.AggregationBuilders;
//import org.elasticsearch.search.aggregations.Aggregations;
//import org.elasticsearch.search.aggregations.bucket.terms.Terms;
//import org.elasticsearch.search.aggregations.bucket.terms.TermsAggregationBuilder;
//import org.elasticsearch.search.aggregations.metrics.Avg;
//import org.elasticsearch.search.aggregations.metrics.AvgAggregationBuilder;
//import org.elasticsearch.search.builder.SearchSourceBuilder;
//import org.junit.Test;
//import org.junit.runner.RunWith;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.test.context.SpringBootTest;
//import org.springframework.test.context.junit4.SpringRunner;
//
//import java.io.IOException;
//import java.util.List;
//
//@RunWith(SpringRunner.class)
//@SpringBootTest
//public class GulimallSearchApplicationTests {
//
//
//    @Autowired
//    RestHighLevelClient restHighLevelClient;
//
//    @Test
//    public void contextLoads() {
//
//    }
//
//
//    @Test
//    public void test1(){
////        System.out.println(restHighLevelClient);
//        String[] s = "_1111".split("_");
//        for (String s1 : s) {
//            System.out.println(s1);
//        }
//    }
//
//    @Test
//    public void testSerach() throws IOException {
//        SearchRequest searchRequest = new SearchRequest();
//        searchRequest.indices("bank");
//        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
//
//        MatchQueryBuilder matchQueryBuilder = QueryBuilders.matchQuery("address", "mill");
//        searchSourceBuilder.query(matchQueryBuilder);
//
//        TermsAggregationBuilder ageAgg = AggregationBuilders.terms("ageAgg").field("age").size(10);
//        searchSourceBuilder.aggregation(ageAgg);
//
//
//        AvgAggregationBuilder balanceAgg = AggregationBuilders.avg("balanceAgg").field("balance");
//        searchSourceBuilder.aggregation(balanceAgg);
//
//
//
//        System.out.println(searchSourceBuilder.toString());
//
//
//        searchRequest.source(searchSourceBuilder);
//
//        SearchResponse searchResponse = restHighLevelClient
//                .search(searchRequest, GuLIMaillElasticsearchConfig.COMMON_OPTIONS);
//
//
//        System.out.println(searchResponse);
//
//        for (SearchHit hit : searchResponse.getHits().getHits()) {
//            String sourceAsString = hit.getSourceAsString();
//            Account account = JSON.parseObject(sourceAsString, Account.class);
//            System.out.println(account);
//        }
//
//        Aggregations aggregations = searchResponse.getAggregations();
//        List<Aggregation> aggregations1 = aggregations.asList();
//
////        for (Aggregation aggregation : aggregations1) {
////            System.out.println("聚合名称："+aggregation.getName());
////
////        }
//
//        Terms ageAgg1 = aggregations.get("ageAgg");
//        for (Terms.Bucket bucket : ageAgg1.getBuckets()) {
//            String keyAsString = bucket.getKeyAsString();
//            System.out.println(bucket.getDocCount());
//            System.out.println("keyAsString"+keyAsString);
//        }
//
//        Avg balanceAgg1 = aggregations.get("balanceAgg");
//        double value = balanceAgg1.getValue();
//        System.out.println(value);
//
//    }
//
//    @ToString
//    @Data
//    static class Account{
//        private int account_number;
//        private int balance;
//        private String firstname;
//        private String lastname;
//        private int age;
//        private String gender;
//        private String address;
//        private String employer;
//        private String email;
//        private String city;
//        private String state;
//    }
//
//    @Test
//    public void testIndex() throws IOException {
//        IndexRequest indexRequest = new IndexRequest("users");
//        indexRequest.id("1");
//        User user = new User();
//        user.setAge(20);
//        user.setGender("男");
//        user.setUserName("测试");
//        String jsonString = JSON.toJSONString(user);
//        indexRequest.source(jsonString, XContentType.JSON);
//
//
//        IndexResponse indexResponse = restHighLevelClient.
//                index(indexRequest, GuLIMaillElasticsearchConfig.COMMON_OPTIONS);
//
//        System.out.println(indexRequest);
//    }
//
//    @Data
//    class User{
//        private String userName;
//        private String gender;
//        private Integer age;
//    }
//
//
//}
