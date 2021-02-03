package com.atguigu.common.to;

import lombok.Data;

/**
 * @ClassName StockDetaikTo
 * @Description TODO
 * @Author lwq
 * @Date 2021/1/28 14:14
 * @Version 1.0
 */
@Data
public class StockDetaikTo {

    private Long id;
    /**
     * sku_id
     */
    private Long skuId;
    /**
     * sku_name
     */
    private String skuName;
    /**
     * 购买个数
     */
    private Integer skuNum;
    /**
     * 工作单id
     */
    private Long taskId;
    /**
     * 仓库id
     */
    private Long wareId;
    /**
     * 1-已锁定  2-已解锁  3-扣减
     */
    private Integer lockStatus;
}
