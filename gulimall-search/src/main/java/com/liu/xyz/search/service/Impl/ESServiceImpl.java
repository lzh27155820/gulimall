package com.liu.xyz.search.service.Impl;

import com.alibaba.fastjson2.JSON;
import com.liu.xyz.common.to.es.SkuESModel;
import com.liu.xyz.search.constant.EsConstant;
import com.liu.xyz.search.service.ESService;
import lombok.extern.slf4j.Slf4j;
import org.elasticsearch.action.bulk.BulkRequest;
import org.elasticsearch.action.bulk.BulkResponse;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.common.xcontent.XContentType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * create liu 2022-10-12
 */
@Slf4j
@Service
public class ESServiceImpl implements ESService {

    @Autowired
    private RestHighLevelClient restHighLevelClient;
    @Override
    public Boolean productUp(List<SkuESModel> skuESModels) {

        BulkRequest bulkRequest = new BulkRequest();

        for (SkuESModel e:skuESModels){

            IndexRequest request = new IndexRequest(EsConstant.PRODUCT_INDEX);

            request.id(e.getSkuId().toString());
            String jsonString = JSON.toJSONString(e);

            request.source(jsonString, XContentType.JSON);
            bulkRequest.add(request);
        }
        try {
            //批量增加
            BulkResponse bulk = restHighLevelClient.bulk(bulkRequest, RequestOptions.DEFAULT.toBuilder().build());

            //TODO 如果批量错误
            boolean hasFailures = bulk.hasFailures();

            List<String> collect = Arrays.asList(bulk.getItems()).stream().map(item -> {
                return item.getId();
            }).collect(Collectors.toList());

            log.info("商品上架完成：{}",collect);
            return true;
        } catch (IOException e) {
            e.printStackTrace();
        }

        return false;
    }
}
