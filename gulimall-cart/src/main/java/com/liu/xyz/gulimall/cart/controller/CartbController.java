package com.liu.xyz.gulimall.cart.controller;

import com.liu.xyz.gulimall.cart.service.CartService;
import com.liu.xyz.gulimall.cart.vo.CartItemVo;
import com.liu.xyz.gulimall.cart.vo.CartVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.concurrent.ExecutionException;

/**
 * create liu 2022-11-02
 */
@Controller
public class CartbController {

    @Autowired
    private CartService cartService;





    /**
     *购物车首页
     */
    @GetMapping("cart.html")
    public String cart(Model model) throws ExecutionException, InterruptedException {


      CartVo cartVo= cartService.getCart();

      model.addAttribute("cart",cartVo);
      return "cartList";
    }

    /**
     *  RedirectAttributes.addAttribute 在请求路径上添加key value
     *  attributes.addFlashAttribute    把数据保存到session中并只能使用一次
     */
    @GetMapping(value = "/addCartItem")
    public String addCartItem(@RequestParam("skuId") Long skuId,
                              @RequestParam("num") Integer num,
                              RedirectAttributes attributes) throws ExecutionException, InterruptedException {

        cartService.addToCart(skuId,num);

        attributes.addAttribute("skuId",skuId);
        return "redirect:http://cart.gulimall.com/addToCartSuccessPage.html";
    }
    /**
     * 跳转到添加购物车成功页面
     * @param skuId
     * @param model
     * @return
     */
    @GetMapping(value = "/addToCartSuccessPage.html")
    public String addToCartSuccessPage(@RequestParam("skuId") Long skuId,
                                       Model model) {
        //重定向到成功页面。再次查询购物车数据即可
        CartItemVo cartItemVo = cartService.getCartItem(skuId);
        model.addAttribute("cartItem",cartItemVo);
        return "success";
    }

    /**
     *商品是否被选中
     */
    @GetMapping("/checkItem")
    public String checkItem(@RequestParam("skuId") Long skuId,Long checked){


        cartService.checkItem(skuId,checked);

        return "redirect:http://cart.gulimall.com/cart.html";//只能重定向到本服务器url地址
    }

    @GetMapping("/countItem")
    public String countItem(@RequestParam("skuId") Long skuId,@RequestParam("num") Integer num){

        cartService.countItem(skuId,num);

        return "redirect:http://cart.gulimall.com/cart.html";
    }
    @GetMapping("/deleteItem")
    public String deleteItem(@RequestParam("skuId")Long skuId){

        cartService.deleteItem(skuId);
        return "redirect:http://cart.gulimall.com/cart.html";
    }


}
