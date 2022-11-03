package com.liu.xyz.thirparty.controller;

import com.liu.xyz.common.utils.R;
import com.liu.xyz.thirparty.api.HttpUtils;
import com.liu.xyz.thirparty.config.SMSConfig;
import org.apache.http.HttpResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

/**
 * create liu 2022-10-27
 */
@RestController
public class SMSController {

    @Autowired
    private SMSConfig smsConfig;

    @GetMapping(value = "/sms/sendCode")
    public R sendCode(@RequestParam("phone") String phone, @RequestParam("code") String code){

        int send = send(phone, code);
        if(send==200){
            return R.ok();
        }else {
            return R.error();
        }
    }

    public  int  send(String phone,String code) {
        //请求的地址
        String host =smsConfig.getHost() ;
        //具体发送短信的地址
        String path = smsConfig.getPath();
        //请求方式
        String method =smsConfig.getMethod();
        //购买的短信验证的表示
        String appcode = smsConfig.getAppcode();
        Map<String, String> headers = new HashMap<String, String>();
        //最后在header中的格式(中间是英文空格)为Authorization:APPCODE 83359fd73fe94948385f570e3c139105
        headers.put("Authorization", "APPCODE " + appcode);
        //根据API的要求，定义相对应的Content-Type
        headers.put("Content-Type", "application/x-www-form-urlencoded; charset=UTF-8");
        Map<String, String> querys = new HashMap<String, String>();
        Map<String, String> bodys = new HashMap<String, String>();
        //验证码
        bodys.put("content", "code:"+code);
        //手机号
        bodys.put("phone_number", phone);
        bodys.put("template_id", "TPL_0000");


        try {
            /**
             * 重要提示如下:
             * HttpUtils请从
             * https://github.com/aliyun/api-gateway-demo-sign-java/blob/master/src/main/java/com/aliyun/api/gateway/demo/util/HttpUtils.java
             * 下载
             *
             * 相应的依赖请参照
             * https://github.com/aliyun/api-gateway-demo-sign-java/blob/master/pom.xml
             */
            HttpResponse response = HttpUtils.doPost(host, path, method, headers, querys, bodys);

            System.out.println(response.toString());

            int statusCode = response.getStatusLine().getStatusCode();

            return statusCode;
            //获取response的body
            //System.out.println(EntityUtils.toString(response.getEntity()));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return 0;
    }
}
