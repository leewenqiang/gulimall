package com.atguigu.common.exception;

/**
 * @ClassName NoStockException
 * @Description TODO
 * @Author lwq
 * @Date 2021/1/25 14:48
 * @Version 1.0
 */
public class NoStockException extends RuntimeException {
    private Long skudId;
    public NoStockException(Long skuId) {
        super(skuId+"没有足够的库存了...");
    }

    public NoStockException(String msg) {
        super(msg);
    }

    public Long getSkudId() {
        return skudId;
    }

    public void setSkudId(Long skudId) {
        this.skudId = skudId;
    }
}
