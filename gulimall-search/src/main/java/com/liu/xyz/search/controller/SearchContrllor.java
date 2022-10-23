package com.liu.xyz.search.controller;

import com.liu.xyz.common.to.es.SkuESModel;
import com.liu.xyz.common.utils.R;
import com.liu.xyz.search.service.ESService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * create liu 2022-10-12
 */
@RestController
@RequestMapping("search")
public class SearchContrllor {

    @Autowired
    private ESService esService;

    @RequestMapping("/product")
    public R get(@RequestBody List<SkuESModel> skuESModels){

       Boolean s=esService.productUp(skuESModels);

       if(s){
           return R.ok();
       }else {
           return R.error();
       }

    }
}
