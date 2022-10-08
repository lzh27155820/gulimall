package com.liu.xyz.thirparty.controller;

import com.aliyun.oss.OSSClient;
import com.aliyun.oss.common.utils.BinaryUtil;
import com.aliyun.oss.model.MatchMode;
import com.aliyun.oss.model.PolicyConditions;
import com.liu.xyz.common.utils.R;
import com.liu.xyz.thirparty.config.OSScofnig;
import com.liu.xyz.thirparty.service.OSSService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * create liu 2022-10-03
 */
@RestController
@RequestMapping("thirdparty/oss")
public class OSSController {

    @Autowired
    private OSSService ossService;

    @Autowired
    private OSScofnig osScofnig;
    @Autowired
    private OSSClient ossClient;
    @RequestMapping("/upload")
    public String upload(MultipartFile multipartFile)  {

        if(multipartFile==null){
            throw new RuntimeException("上传有误");
        }

        String url= ossService.uploadBrandLogo(multipartFile);
        return url;
    }

   // @RequestMapping("/policy")
    public R upload(){

        // 阿里云账号AccessKey拥有所有API的访问权限，风险很高。强烈建议您创建并使用RAM用户进行API访问或日常运维，请登录RAM控制台创建RAM用户。
        String accessId = osScofnig.getAccessKeyId();
        String accessKey = osScofnig.getAccessKeySecret();
        // Endpoint以华东1（杭州）为例，其它Region请按实际情况填写。
        String endpoint = osScofnig.getEndPoint();
        // 填写Bucket名称，例如examplebucket。
        String bucket = osScofnig.getBucketName();
        // 填写Host地址，格式为https://bucketname.endpoint。
        String host = "https://"+bucket+".oss-cn-hangzhou.aliyuncs.com";
        // 设置上传回调URL，即回调服务器地址，用于处理应用服务器与OSS之间的通信。OSS会在文件上传完成后，把文件上传信息通过此回调URL发送给应用服务器。
        //String callbackUrl = "https://192.168.0.0:8888";
        // 设置上传到OSS文件的前缀，可置空此项。置空后，文件将上传至Bucket的根目录下。

        String format = new SimpleDateFormat("yyyy-MM-dd").format(new Date());
        String dir = format+"/";

//        // 创建ossClient实例。
//        OSS ossClient = new OSSClientBuilder().build(endpoint, accessId, accessKey);
        Map<String, String> respMap=null;
        try {
            long expireTime = 30;
            long expireEndTime = System.currentTimeMillis() + expireTime * 1000;
            Date expiration = new Date(expireEndTime);
            PolicyConditions policyConds = new PolicyConditions();
            policyConds.addConditionItem(PolicyConditions.COND_CONTENT_LENGTH_RANGE, 0, 1048576000);
            policyConds.addConditionItem(MatchMode.StartWith, PolicyConditions.COND_KEY, dir);

            String postPolicy = ossClient.generatePostPolicy(expiration, policyConds);
            byte[] binaryData = postPolicy.getBytes("utf-8");
            String encodedPolicy = BinaryUtil.toBase64String(binaryData);
            String postSignature = ossClient.calculatePostSignature(postPolicy);

             respMap = new LinkedHashMap<String, String>();
            respMap.put("accessId", accessId);
            respMap.put("policy", encodedPolicy);
            respMap.put("signature", postSignature);
            respMap.put("dir", dir);
            respMap.put("host", host);
            respMap.put("expire", String.valueOf(expireEndTime / 1000));
            // respMap.put("expire", formatISO8601Date(expiration));


         return R.ok().put("data",respMap);
        } catch (Exception e) {
            // Assert.fail(e.getMessage());
            System.out.println(e.getMessage());
        }
        return null;
    }



        @RequestMapping("/policy")
        public R policy() {



            //https://gulimall-hello.oss-cn-beijing.aliyuncs.com/hahaha.jpg

            String host = "https://" + osScofnig.getBucketName() + "." + osScofnig.getEndPoint(); // host的格式为 bucketname.endpoint
            // callbackUrl为 上传回调服务器的URL，请将下面的IP和Port配置为您自己的真实信息。
//        String callbackUrl = "http://88.88.88.88:8888";
            String format = new SimpleDateFormat("yyyy-MM-dd").format(new Date());
            String dir = format + "/"; // 用户上传文件时指定的前缀。

            Map<String, String> respMap = null;
            try {
                long expireTime = 30;
                long expireEndTime = System.currentTimeMillis() + expireTime * 1000;
                Date expiration = new Date(expireEndTime);
                PolicyConditions policyConds = new PolicyConditions();
                policyConds.addConditionItem(PolicyConditions.COND_CONTENT_LENGTH_RANGE, 0, 1048576000);
                policyConds.addConditionItem(MatchMode.StartWith, PolicyConditions.COND_KEY, dir);

                String postPolicy = ossClient.generatePostPolicy(expiration, policyConds);
                byte[] binaryData = postPolicy.getBytes("utf-8");
                String encodedPolicy = BinaryUtil.toBase64String(binaryData);
                String postSignature = ossClient.calculatePostSignature(postPolicy);

                respMap = new LinkedHashMap<String, String>();
                respMap.put("accessid",osScofnig.getAccessKeyId());
                respMap.put("policy", encodedPolicy);
                respMap.put("signature", postSignature);
                respMap.put("dir", dir);
                respMap.put("host", host);
                respMap.put("expire", String.valueOf(expireEndTime / 1000));
                // respMap.put("expire", formatISO8601Date(expiration));


            } catch (Exception e) {
                // Assert.fail(e.getMessage());
                System.out.println(e.getMessage());
            }

            return R.ok().put("data",respMap);

    }
}
