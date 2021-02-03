package com.atguigu.gulimall.ware.vo;

import lombok.Data;

import java.util.List;

/**
 * @ClassName MergeVo
 * @Description TODO
 * @Author lwq
 * @Date 2020/12/29 10:32
 * @Version 1.0
 */
@Data
public class MergeVo {

   private Long purchaseId;
   private List<Long> items;

}
