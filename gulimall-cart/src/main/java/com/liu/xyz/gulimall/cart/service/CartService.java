package com.liu.xyz.gulimall.cart.service;

import com.liu.xyz.gulimall.cart.vo.CartItemVo;
import com.liu.xyz.gulimall.cart.vo.CartVo;

import java.util.concurrent.ExecutionException;

/**
 * create liu 2022-11-02
 */
public interface CartService {
    /**
     * 添加到购物车
     * @param skuId 商品id
     * @param num 商品数量
     * @return
     */
    CartItemVo addToCart(Long skuId, Integer num) throws ExecutionException, InterruptedException;

    /**
     *  获取购物项
     * @param skuId
     * @return
     */
    CartItemVo getCartItem(Long skuId);

    /**
     * 获取整个购物车信息
     */
    CartVo getCart() throws ExecutionException, InterruptedException;

    /**
     * 清空购物车
     * @param cartKey
     */
    public void clearCartInfo(String cartKey);

    /**
     * 商品选中或不选中
     * @param skuId
     * @param checked
     */
    void checkItem(Long skuId, Long checked);

    /**
     * 该笔商品数量
     * @param skuId
     * @param num
     */
    void countItem(Long skuId, Integer num);

    /**
     * 购物车删除某款商品
     * @param skuId
     */
    void deleteItem(Long skuId);
}
