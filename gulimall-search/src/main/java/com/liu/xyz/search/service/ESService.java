package com.liu.xyz.search.service;

import com.liu.xyz.common.to.es.SkuESModel;

import java.util.List;

/**
 * create liu 2022-10-12
 */
public interface ESService {
    Boolean productUp(List<SkuESModel> skuESModels);
}
