package com.liu.xyz.gulimall.cart.service.impl;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.TypeReference;
import com.liu.xyz.common.productUtils.CartConstant;
import com.liu.xyz.common.utils.R;
import com.liu.xyz.gulimall.cart.feign.ProductFeignService;
import com.liu.xyz.gulimall.cart.interceptor.CartInterceptor;
import com.liu.xyz.gulimall.cart.service.CartService;
import com.liu.xyz.gulimall.cart.to.UserInfoTo;
import com.liu.xyz.gulimall.cart.vo.CartItemVo;
import com.liu.xyz.gulimall.cart.vo.CartVo;
import com.liu.xyz.gulimall.cart.vo.SkuInfoVo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.BoundHashOperations;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.stream.Collectors;

/**
 * create liu 2022-11-02
 */
@Slf4j
@Service
public class CartServiceImpl implements CartService {
    @Autowired
    private StringRedisTemplate stringRedisTemplate;
    @Autowired
    private ProductFeignService productFeignService;
    @Autowired
    private ThreadPoolExecutor executor;

    @Override
    public CartItemVo addToCart(Long skuId, Integer num) throws ExecutionException, InterruptedException {

        BoundHashOperations<String, Object, Object> ops = getOps();

        String o = (String)ops.get(skuId.toString());
        if(StringUtils.isEmpty(o)){

            CartItemVo cartItemVo = new CartItemVo();
            CompletableFuture<Void> future = CompletableFuture.runAsync(() -> {
                R info = productFeignService.info(skuId);
                String jsonString = JSON.toJSONString(info.get("skuInfo"));
                SkuInfoVo skuInfoVo = JSON.parseObject(jsonString, new TypeReference<SkuInfoVo>() {
                });
                cartItemVo.setTitle(skuInfoVo.getSkuTitle());
                cartItemVo.setImage(skuInfoVo.getSkuDefaultImg());
                cartItemVo.setCount(num);
                cartItemVo.setPrice(skuInfoVo.getPrice());
                cartItemVo.setSkuId(skuInfoVo.getSkuId());
                cartItemVo.setCheck(true);
            }, executor);
            CompletableFuture<Void> future1 = CompletableFuture.runAsync(() -> {
                List<String> strings = productFeignService.stringList(skuId);
                cartItemVo.setSkuAttrValues(strings);
            });
            //等待所有的异步任务全部完成
            CompletableFuture.allOf(future, future1).get();
            String s = JSON.toJSONString(cartItemVo);
            ops.put(skuId.toString(),s);
            return cartItemVo;
        }else {
            CartItemVo cartItemVo = JSON.parseObject(o, CartItemVo.class);

            cartItemVo.setCount(cartItemVo.getCount()+num);
            ops.put(skuId.toString(),JSON.toJSONString(cartItemVo));
            return cartItemVo;
        }




    }

    @Override
    public CartItemVo getCartItem(Long skuId) {

        BoundHashOperations<String, Object, Object> ops = getOps();

        String o =(String) ops.get(skuId.toString());

        CartItemVo cartItemVo = JSON.parseObject(o, CartItemVo.class);
        return cartItemVo;
    }

    @Override
    public CartVo getCart() throws ExecutionException, InterruptedException {

        CartVo cartVo = new CartVo();

        UserInfoTo userInfoTo = CartInterceptor.threadLocal.get();
        if(userInfoTo.getUserId()!=null){

            String tepm= CartConstant.CART_PREFIX + userInfoTo.getUserKey();
            List<CartItemVo> tepmVos = getCartItemVoList(tepm);

            if(tepmVos!=null&&tepmVos.size()>0){
                for (CartItemVo tepmVo : tepmVos) {
                    addToCart(tepmVo.getSkuId(),tepmVo.getCount());
                }
                clearCartInfo(tepm);
            }

            String user = CartConstant.CART_PREFIX + userInfoTo.getUserId();
            List<CartItemVo> list = getCartItemVoList(user);
            cartVo.setItems(list);

        }else{
            String tepm = CartConstant.CART_PREFIX + userInfoTo.getUserKey();
            List<CartItemVo> list = getCartItemVoList(tepm);
            cartVo.setItems(list);

        }


        return cartVo;
    }

    @Override
    public void clearCartInfo(String cartKey) {
        stringRedisTemplate.delete(cartKey);
    }

    @Override
    public void checkItem(Long skuId, Long checked) {
        BoundHashOperations<String, Object, Object> ops = getOps();

        String o =(String) ops.get(skuId.toString());
        CartItemVo cartItemVo = JSON.parseObject(o, CartItemVo.class);
        cartItemVo.setCheck(checked==0?false:true);
        ops.put(skuId.toString(),JSON.toJSONString(cartItemVo));
    }

    @Override
    public void countItem(Long skuId, Integer num) {
        BoundHashOperations<String, Object, Object> ops = getOps();

        String o =(String) ops.get(skuId.toString());
        CartItemVo cartItemVo = JSON.parseObject(o, CartItemVo.class);

        cartItemVo.setCount(num);
        ops.put(skuId.toString(),JSON.toJSONString(cartItemVo));
    }

    @Override
    public void deleteItem(Long skuId) {
        BoundHashOperations<String, Object, Object> ops = getOps();
        ops.delete(skuId.toString());
    }

    private List<CartItemVo> getCartItemVoList(String keys){

        BoundHashOperations<String, Object, Object> ops = stringRedisTemplate.boundHashOps(keys);

        List<Object> values = ops.values();
        if(values!=null&&values.size()>0){
            List<CartItemVo> collect = values.stream().map((obj) -> {
                String str=(String) obj;
                CartItemVo cartItemVo = JSON.parseObject(str, CartItemVo.class);
                return cartItemVo;
            }).collect(Collectors.toList());
            return collect;
        }

       return null;

    }
    /**
     *  区分零时用户和登入用户
     * @return 返回的是在redis帮
     */
    private BoundHashOperations<String, Object, Object> getOps(){
        UserInfoTo userInfoTo = CartInterceptor.threadLocal.get();

        String key="";
        //用户
        if(userInfoTo.getUserId()!=null){
            key= CartConstant.CART_PREFIX+userInfoTo.getUserId();
        }else {
            key=CartConstant.CART_PREFIX+userInfoTo.getUserKey();
        }

        BoundHashOperations<String, Object, Object> ops = stringRedisTemplate.boundHashOps(key);

        return ops;
    }
}
