package com.atguigu.gulimall.cart.controlor;

import com.atguigu.gulimall.cart.service.CartService;
import com.atguigu.gulimall.cart.vo.Cart;
import com.atguigu.gulimall.cart.vo.CartItem;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.servlet.http.HttpSession;
import java.util.List;
import java.util.concurrent.ExecutionException;

/**
 * @ClassName CartControlor
 * @Description TODO
 * @Author lwq
 * @Date 2021/1/18 15:42
 * @Version 1.0
 */
@Controller
public class CartControlor {

    @Autowired
    private CartService cartService;


    @GetMapping("/cart.html")
    public String cartListPage(HttpSession session,Model model) throws ExecutionException, InterruptedException {
        //快速得到用户信息
       Cart cart =  cartService.getCart();

       model.addAttribute("cart",cart);

        return "cartList";
    }

    @ResponseBody
    @GetMapping("/{memberId}/getCartItemByUserId")
    public List<CartItem> getCartItemByUserId(@PathVariable Long memberId ){
        return cartService.getCartItemByUserId(memberId);
    }



    @GetMapping("/checkItem")
    public String checkItem(@RequestParam("skuId") Long skuId,@RequestParam("check") Integer check)  {

        cartService.checkItem(skuId,check);

        return "redirect:http://cart.gulimall.com/cart.html";

    }

    @GetMapping("/deleteItem")
    public String deleteItem(@RequestParam("skuId") Long skuId)  {

        cartService.deleteItem(skuId);

        return "redirect:http://cart.gulimall.com/cart.html";

    }



    @GetMapping("/countItem")
    public String countItem(@RequestParam("skuId") Long skuId,@RequestParam("num") Integer num)  {

        cartService.countItem(skuId,num);

        return "redirect:http://cart.gulimall.com/cart.html";

    }




    @GetMapping("/addToCart")
    public String addToCart(@RequestParam("skuId") Long skuId,
                            @RequestParam("num")Integer num,
                            Model model, RedirectAttributes redirectAttributes) throws ExecutionException, InterruptedException {
       CartItem cartItem =  cartService.addToCart(skuId,num);
        model.addAttribute("item",cartItem);
//        model.addAttribute("skuId",skuId);
        redirectAttributes.addAttribute("skuId",skuId);
        return "redirect:http://cart.gulimall.com/addToCartSuccess.html";
    }



    @GetMapping("/addToCartSuccess.html")
    public String addToCartSuccessPage(@RequestParam("skuId") Long skuId,Model model){
        //再次查询购物车
        CartItem cartItem = cartService.getCartItem(skuId);
        model.addAttribute("item",cartItem);
        return "success";
    }

}
