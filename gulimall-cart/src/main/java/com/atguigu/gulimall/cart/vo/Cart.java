package com.atguigu.gulimall.cart.vo;

import org.springframework.util.CollectionUtils;

import java.math.BigDecimal;
import java.util.List;

/**
 * @ClassName Cart
 * @Description TODO
 * @Author lwq
 * @Date 2021/1/18 15:25
 * @Version 1.0
 */
public class Cart {


    private List<CartItem> items;

    private Integer countNum;

    /**
     * 类型数量
     */
    private Integer countType;


    /**
     * 总价
     */
    private BigDecimal totalAmount;

    /**
     * 减免
     */
    private BigDecimal reduce = new BigDecimal("0");


    public List<CartItem> getItems() {
        return items;
    }

    public void setItems(List<CartItem> items) {
        this.items = items;
    }

    public Integer getCountNum() {

        int countNum = 0;
        if(!CollectionUtils.isEmpty(this.items)){
            for (CartItem item : this.items) {
                countNum+=item.getCount();
            }
        }
        return countNum;
    }


    public Integer getCountType() {
        int countType = 0;
        if(!CollectionUtils.isEmpty(this.items)){
            for (CartItem item : this.items) {
                countType+=1;
            }
        }
        return countType;
    }


    public BigDecimal getTotalAmount() {
        BigDecimal amount = new BigDecimal("0");
        if(!CollectionUtils.isEmpty(this.items)){
            for (CartItem item : this.items) {
                if(item.getCheck()){
                    BigDecimal totalPrice = item.getTotalPrice();
                    amount =  amount.add(totalPrice);
                }
            }
        }
        BigDecimal subtract = amount.subtract(reduce);
        return subtract;
    }



    public BigDecimal getReduce() {
        return reduce;
    }

    public void setReduce(BigDecimal reduce) {
        this.reduce = reduce;
    }
}

