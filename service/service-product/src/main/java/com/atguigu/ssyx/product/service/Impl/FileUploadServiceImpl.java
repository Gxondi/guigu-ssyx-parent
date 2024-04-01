package com.atguigu.ssyx.product.service.Impl;

import com.aliyun.oss.ClientException;
import com.aliyun.oss.OSS;
import com.aliyun.oss.OSSClientBuilder;
import com.aliyun.oss.OSSException;
import com.aliyun.oss.common.auth.CredentialsProviderFactory;
import com.aliyun.oss.common.auth.EnvironmentVariableCredentialsProvider;
import com.aliyun.oss.model.PutObjectRequest;
import com.aliyun.oss.model.PutObjectResult;
import com.atguigu.ssyx.product.service.FileUploadService;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.UUID;

@Service
public class FileUploadServiceImpl implements FileUploadService {
    @Value("${aliyun.endpoint}")
    private String endPoint;
    @Value("${aliyun.keyid}")
    private String accessKey;
    @Value("${aliyun.keysecret}")
    private String secreKey;
    @Value("${aliyun.bucketname}")
    private String bucketName;

    @Override
    public String upload(MultipartFile file) {
        // 创建OSSClient实例。
        OSS ossClient = new OSSClientBuilder().build(endPoint, accessKey, secreKey);
        try {
            InputStream inputStream = file.getInputStream();
            // 创建PutObjectRequest对象。
            String objectName = file.getOriginalFilename();
            //防止文件名重复
            String uuid = UUID.randomUUID().toString().replace("-", "");
            objectName = uuid + objectName;
            //根据日期创建文件夹
            String currentTime = new DateTime().toString("yyyy/MM/dd");
            objectName = currentTime + "/" + objectName;
            // 上传文件流。
            PutObjectRequest putObjectRequest = new PutObjectRequest(bucketName, objectName, inputStream);
            //返回Response
            putObjectRequest.setProcess("true");
            // 创建PutObject请求。
            PutObjectResult result = ossClient.putObject(putObjectRequest);

            //返回url地址
            String uri = result.getResponse().getUri();
            if (uri == null) {
                uri = "https://" + bucketName + "." + endPoint + "/" + objectName;
            }
            return uri;
        } catch (IOException e) {
            throw new RuntimeException(e);
        } finally {
            if (ossClient != null) {
                ossClient.shutdown();
            }
        }
    }
}
