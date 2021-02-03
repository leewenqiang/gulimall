package com.atguigu.gulimall.order.vo;

import lombok.Getter;
import lombok.Setter;
import org.springframework.util.CollectionUtils;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

/**
 * @ClassName OrderConfirmVo
 * @Description TODO
 * @Author lwq
 * @Date 2021/1/21 10:16
 * @Version 1.0
 */

public class OrderConfirmVo {

    /**
     * 收货地址
     */
    List<MemberReceiveAddressVo> addressList;

    /**
     * 订单项列表
     */
    List<OrderItem> orderItems;

    public Integer getCount(){
        Integer i = 0;
        if(!CollectionUtils.isEmpty(orderItems)){
            for (OrderItem orderItem : orderItems) {
                i += orderItem.getCount();
            }
        }
        return i;
    }


    /**
     * 会员积分
     */

     Integer integration;

    /**
     * 总金额
     */
//    BigDecimal total;

    /**
     * 唯一令牌 防重复提交令牌
     */
    String orderToken;

    /**
     * 应付金额
     */
    BigDecimal payAccount;

    @Getter @Setter
    Map<Long,Boolean> stocks;

    public List<MemberReceiveAddressVo> getAddressList() {
        return addressList;
    }

    public void setAddressList(List<MemberReceiveAddressVo> addressList) {
        this.addressList = addressList;
    }

    public List<OrderItem> getOrderItems() {
        return orderItems;
    }

    public void setOrderItems(List<OrderItem> orderItems) {
        this.orderItems = orderItems;
    }

    public Integer getIntegration() {
        return integration;
    }

    public void setIntegration(Integer integration) {
        this.integration = integration;
    }

    public BigDecimal getTotal() {
        BigDecimal total = new BigDecimal("0");
        if(!CollectionUtils.isEmpty(orderItems)){
            for (OrderItem orderItem : orderItems) {
                BigDecimal multiply = orderItem.getPrice().multiply(new BigDecimal(orderItem.getCount().toString()));
                total =  total.add(multiply);
            }
        }
        return total;
    }



    public BigDecimal getPayAccount() {
       return getTotal();
    }

    public String getOrderToken() {
        return orderToken;
    }

    public void setOrderToken(String orderToken) {
        this.orderToken = orderToken;
    }
}
