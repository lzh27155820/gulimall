package com.liu.xyz.thirparty.service.impl;

import com.aliyun.oss.OSS;
import com.aliyun.oss.OSSClientBuilder;
import com.aliyun.oss.common.comm.ResponseMessage;
import com.aliyun.oss.model.PutObjectResult;
import com.liu.xyz.thirparty.config.OSScofnig;
import com.liu.xyz.thirparty.service.OSSService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.UUID;

/**
 * create liu 2022-10-03
 */
@Slf4j
@Service
public class OSSServiceImpl implements OSSService {

    @Autowired
    private OSScofnig osScofnig;
    @Override
    public String uploadBrandLogo(MultipartFile multipartFile) {

        String url = null;
        try {
            url = OSSServiceImpl.uploadFileToOss(
                    osScofnig.getEndPoint(),
                    osScofnig.getAccessKeyId(),
                    osScofnig.getAccessKeySecret(),
                    multipartFile.getInputStream(),
                    osScofnig.getBucketName(),
                    osScofnig.getBucketDomain(),
                    multipartFile.getOriginalFilename());
        } catch (IOException e) {
            e.printStackTrace();
        }
        return url;
    }
    /**
     * 专门负责上传文件到OSS服务器的工具方法
     * @param endpoint			OSS参数
     * @param accessKeyId		OSS参数
     * @param accessKeySecret	OSS参数
     * @param inputStream		要上传的文件的输入流
     * @param bucketName		OSS参数
     * @param bucketDomain		OSS参数
     * @param originalName		要上传的文件的原始文件名
     * @return	包含上传结果以及上传的文件在OSS上的访问路径
     */
    public static String uploadFileToOss(
            String endpoint,
            String accessKeyId,
            String accessKeySecret,
            InputStream inputStream,
            String bucketName,
            String bucketDomain,
            String originalName) {

        // 创建OSSClient实例。
        OSS ossClient = new OSSClientBuilder().build(endpoint, accessKeyId, accessKeySecret);

        // 生成上传文件的目录
        String folderName = new SimpleDateFormat("yyyyMMdd").format(new Date());

        // 生成上传文件在OSS服务器上保存时的文件名
        // 原始文件名：beautfulgirl.jpg
        // 生成文件名：wer234234efwer235346457dfswet346235.jpg
        // 使用UUID生成文件主体名称
        String fileMainName = UUID.randomUUID().toString().replace("-", "");

        // 从原始文件名中获取文件扩展名
        String extensionName = originalName.substring(originalName.lastIndexOf("."));

        // 使用目录、文件主体名称、文件扩展名称拼接得到对象名称
        String objectName = folderName + "/" + fileMainName + extensionName;

        try {
            // 调用OSS客户端对象的方法上传文件并获取响应结果数据
            PutObjectResult putObjectResult = ossClient.putObject(bucketName, objectName, inputStream);

            //  com.aliyun.oss.common.comm.ResponseMessage response = putObjectResult.getResponse();
            // 从响应结果中获取具体响应消息
            ResponseMessage responseMessage = putObjectResult.getResponse();

            // 根据响应状态码判断请求是否成功
            if(responseMessage == null) {

                // 拼接访问刚刚上传的文件的路径
                String ossFileAccessPath = bucketDomain + "/" + objectName;

                // 当前方法返回成功
                return ossFileAccessPath;
            } else {
                // 获取响应状态码
                int statusCode = responseMessage.getStatusCode();

                // 如果请求没有成功，获取错误消息
                String errorMessage = responseMessage.getErrorResponseAsString();

//                // 当前方法返回失败
//                return ResultEntity.failed("当前响应状态码="+statusCode+" 错误消息="+errorMessage);
                log.info("上传文件出错----------"+errorMessage);
                return errorMessage;
            }
        } catch (Exception e) {
            e.printStackTrace();

            // 当前方法返回失败
            log.info("上传文件出错----------"+e.getMessage());
            return e.getMessage();
        } finally {

            if(ossClient != null) {

                // 关闭OSSClient。
                ossClient.shutdown();
            }
        }

    }

}
