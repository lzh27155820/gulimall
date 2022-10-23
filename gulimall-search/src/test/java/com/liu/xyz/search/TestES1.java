package com.liu.xyz.search;


import com.alibaba.fastjson2.JSON;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.common.xcontent.XContentType;
import org.elasticsearch.rest.RestStatus;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.io.IOException;

/**
 * create liu 2022-10-09
 */
@SpringBootTest
public class TestES1 {

    @Autowired
    private RestHighLevelClient restHighLevelClient;
    @Test
    public void test(){

        System.out.println(restHighLevelClient);
    }


    @Test
    public void test1() throws IOException {

        Users users = new Users("张三",18,true);

        IndexRequest request = new IndexRequest();
        /**
         * 1.参数 索引 2. id
         */
        request.index("users").id("1");
        /**
         * 1. 参数 要传入的资源 json 2. 数据传输方式
         */
        String usersJSON = JSON.toJSONString(users);
        request.source(usersJSON, XContentType.JSON);
        /**
         * 1 参数 IndexRequest 行为
         * 2 返回值
         */
        IndexResponse response = restHighLevelClient.index(request, RequestOptions.DEFAULT);

        RestStatus status = response.status();
        System.out.println(status.getStatus());
    }
}
