package com.liu.xyz.gulimall.product.web;

import com.liu.xyz.gulimall.product.service.SkuInfoService;
import com.liu.xyz.gulimall.product.web.vo.SkuItemVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.concurrent.ExecutionException;

/**
 * create liu 2022-10-25
 */
@Controller
public class ItemController {

    @Autowired
    private SkuInfoService skuInfoService;
    //@RequestMapping("/{skuId}.html")
    public String item(@PathVariable("skuId") Long skuId, Model model){


        SkuItemVo skuItemVo=skuInfoService.searchToskuId(skuId);
       model.addAttribute("item",skuItemVo);
        return "item";
    }
    /**
     * 展示当前sku的详情
     * @param skuId
     * @return
     */
    @GetMapping("/{skuId}.html")
    public String skuItem(@PathVariable("skuId") Long skuId, Model model) throws ExecutionException, InterruptedException {

        System.out.println("准备查询" + skuId + "详情");

        SkuItemVo vos = skuInfoService.searchToskuId(skuId);

        model.addAttribute("item",vos);

        return "item";
    }
}
