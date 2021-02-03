package com.atguigu.common.to;

import lombok.Data;

/**
 * @ClassName StockLockedTo
 * @Description TODO
 * @Author lwq
 * @Date 2021/1/28 10:58
 * @Version 1.0
 */
@Data
public class StockLockedTo {

    /**
     * 库存工作单id
     */
    private Long id;

    /**
     * 工作单详情ids
     */
    private StockDetaikTo detaikTo;





}
