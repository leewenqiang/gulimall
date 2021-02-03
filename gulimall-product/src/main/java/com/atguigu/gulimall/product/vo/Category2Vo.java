package com.atguigu.gulimall.product.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * @ClassName Category2Vo
 * @Description 二级分类VO
 * @Author lwq
 * @Date 2021/1/4 9:51
 * @Version 1.0
 */

@AllArgsConstructor
@NoArgsConstructor
@Data
public class Category2Vo {

    /**
     * 一级父分类
     */
    private String catalog1Id;
    /**
     * 三级子分类
     */
    private List<Category3Vo> catalog3List;

    private String id;

    private String name;

    /**
     * 三级分类VO
     */
    @AllArgsConstructor
    @NoArgsConstructor
    @Data
    public static class Category3Vo{
        private String catalog2Id;
        private String id;
        private String name;
    }

}
