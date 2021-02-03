package com.atguigu.gulimall.product.vo;

import lombok.Data;

/**
 * @ClassName AttrResVo
 * @Description TODO
 * @Author lwq
 * @Date 2020/12/21 15:17
 * @Version 1.0
 */
@Data
public class AttrResVo extends AttrVo {

    /**
     * 所属分类名字
     */
    private String catelogName;
    /**
     * 所属分组名字
     */
    private String groupName;

    /**
     *  //分类完整路径
     */
    private Long[] catelogPath;

}
