package com.atguigu.gulimall.cart.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.atguigu.common.utils.R;
import com.atguigu.gulimall.cart.feigin.ProductFeignService;
import com.atguigu.gulimall.cart.interceptor.CartInterceptor;
import com.atguigu.gulimall.cart.service.CartService;
import com.atguigu.gulimall.cart.to.UserInfoTo;
import com.atguigu.gulimall.cart.vo.Cart;
import com.atguigu.gulimall.cart.vo.CartItem;
import com.atguigu.gulimall.cart.vo.SkuInfoVO;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.BoundHashOperations;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.math.BigDecimal;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.stream.Collectors;

/**
 * @ClassName CartServiceImpl
 * @Description TODO
 * @Author lwq
 * @Date 2021/1/18 15:39
 * @Version 1.0
 */
@Slf4j
@Service
public class CartServiceImpl implements CartService {

    private  final String CART_PREIFX="gulimall:cart:";

    @Autowired
    private StringRedisTemplate redisTemplate;

    @Autowired
    ProductFeignService productFeignService;

    @Autowired
    ThreadPoolExecutor executor;


    @Override
    public CartItem addToCart(Long skuId, Integer num) throws ExecutionException, InterruptedException {


        BoundHashOperations cartOps = getCartOps();
        String res = (String) cartOps.get(skuId.toString());
        if(StringUtils.isEmpty(res)){
            //购物车无此商品
            CartItem cartItem = new CartItem();
            CompletableFuture<Void> getSkuInfoTask = CompletableFuture.runAsync(() -> {

                //1、远程查询商品信息
                R r = productFeignService.skuInfo(skuId);
                SkuInfoVO skuInfo = r.getData("skuInfo", new TypeReference<SkuInfoVO>() {
                });
                //添加
                cartItem.setCheck(true);
                cartItem.setCount(num);
                cartItem.setImage(skuInfo.getSkuDefaultImg());
                cartItem.setTitle(skuInfo.getSkuTitle());
                cartItem.setPrice(skuInfo.getPrice());
                cartItem.setSkuId(skuId);
            },executor);


            CompletableFuture<Void> getSkuSaleAttrValues = CompletableFuture.runAsync(() -> {
                //远程查询sku组合信息
                List<String> skuSaleAttrValue = productFeignService.getSkuSaleAttrValue(skuId);
                cartItem.setSkuAttr(skuSaleAttrValue);
            }, executor);


            CompletableFuture.allOf(getSkuInfoTask,getSkuSaleAttrValues).get();

            String jsonString = JSON.toJSONString(cartItem);
            cartOps.put(skuId.toString(),jsonString);
            return cartItem;
        }else{
            //修改数量
            CartItem cartItem = JSON.parseObject(res, CartItem.class);
            cartItem.setCount(cartItem.getCount()+num);
            cartOps.put(skuId.toString(),JSON.toJSONString(cartItem));
            return cartItem;
        }



    }

    @Override
    public CartItem getCartItem(Long skuId) {

        BoundHashOperations cartOps = getCartOps();
        String values = (String) cartOps.get(skuId.toString());

        return JSON.parseObject(values,CartItem.class);
    }

    @Override
    public Cart getCart() throws ExecutionException, InterruptedException {
        Cart cart = new Cart();
        UserInfoTo userInfoTo = CartInterceptor.toThreadLocal.get();
        //决定用那个key
        String cartKey = "";
        String temCartKey= "";
        if(userInfoTo.getUserId() != null){
            cartKey=CART_PREIFX+userInfoTo.getUserId();
            temCartKey = CART_PREIFX+userInfoTo.getUserKey();
            List<CartItem> cartByCartKey = getCartByCartKey(temCartKey);
            if(!CollectionUtils.isEmpty(cartByCartKey)){
                for (CartItem cartItem : cartByCartKey) {
                    addToCart(cartItem.getSkuId(),cartItem.getCount());
                }
                //删除购物车
                deleteCart(temCartKey);
            }

            List<CartItem> byCartKey = getCartByCartKey(cartKey);
            cart.setItems(byCartKey);

        }else{
            cartKey = CART_PREIFX+userInfoTo.getUserKey();
            List<CartItem> cartByCartKey = getCartByCartKey(cartKey);
            cart.setItems(cartByCartKey);
        }

        return cart;

    }

    @Override
    public void deleteCart(String cartKey) {
        redisTemplate.delete(cartKey);
    }

    @Override
    public void checkItem(Long skuId, Integer check) {

        CartItem cartItem = getCartItem(skuId);

        cartItem.setCheck(check==1?true:false);

        String jsonString = JSON.toJSONString(cartItem);

        getCartOps().put(skuId.toString(),jsonString);

    }

    @Override
    public void countItem(Long skuId, Integer num) {

        CartItem cartItem = getCartItem(skuId);

        cartItem.setCount(num);

        String jsonString = JSON.toJSONString(cartItem);

        getCartOps().put(skuId.toString(),jsonString);


    }

    @Override
    public void deleteItem(Long skuId) {

        getCartOps().delete(skuId.toString());

    }

    @Override
    public List<CartItem> getCartItemByUserId(Long memberId) {

        String cartKey = CART_PREIFX+memberId;

        List<CartItem> cartItems = getCartByCartKey(cartKey);

        if(!CollectionUtils.isEmpty(cartItems)){
           return cartItems.stream().
                   filter(item->item.getCheck())
                   .map(item->{
                       //更新最新价格
                       BigDecimal priceBySkuId = productFeignService.getPriceBySkuId(item.getSkuId());
                       item.setPrice(priceBySkuId);
                     return item;
                   }).collect(Collectors.toList());
        }else{
            return null;
        }

    }

    @Override
    public List<CartItem> getCartByCartKey(String cartKey) {

        BoundHashOperations<String, Object, Object> operations
                = redisTemplate.boundHashOps(cartKey);
        List<Object> values = operations.values();
        if(!CollectionUtils.isEmpty(values)){
            List<CartItem> collect = values.stream().map(v -> {
                String s = (String) v;
                CartItem cartItem = JSON.parseObject(s, CartItem.class);
                return cartItem;
            }).collect(Collectors.toList());
           return collect;
        }
        return null;
    }


    /**
     * 获取操作的购物车
     * @return
     */
    private BoundHashOperations getCartOps() {
        UserInfoTo userInfoTo = CartInterceptor.toThreadLocal.get();
        //决定用那个key
        String cartKey = "";
        if(userInfoTo.getUserId() != null){
            cartKey=CART_PREIFX+userInfoTo.getUserId();
        }else{
            cartKey = CART_PREIFX+userInfoTo.getUserKey();
        }

        //操作购物车
        BoundHashOperations<String, Object, Object> operations
                = redisTemplate.boundHashOps(cartKey);

        return operations;
    }
}
