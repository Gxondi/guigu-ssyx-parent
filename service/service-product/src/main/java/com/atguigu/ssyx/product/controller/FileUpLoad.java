package com.atguigu.ssyx.product.controller;

import com.atguigu.ssyx.common.result.Result;
import com.atguigu.ssyx.product.service.FileUploadService;
import io.swagger.annotations.Api;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
/**
 * 文件上传
 AccessKey ID
 LTAI5tFuVCfvFgZiGg9iya7y
 AccessKey Secret
 V7lFYQwgh9JawcJcDdYDI5ySWvQFxa
 */
@Api(value = "文件上传", tags = "文件上传")
@RestController
@RequestMapping("admin/product")
//@CrossOrigin
public class FileUpLoad {
    @Autowired
    private FileUploadService fileUploadService;
    @PostMapping("fileUpload")
    public Result fileUpload(MultipartFile file) {
        String url = fileUploadService.upload(file);
        return Result.ok(url);
    }
}
