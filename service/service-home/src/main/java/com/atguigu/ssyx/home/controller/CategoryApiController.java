package com.atguigu.ssyx.home.controller;

import com.atguigu.ssyx.client.order.ProductFeignClient;
import com.atguigu.ssyx.common.result.Result;
import com.atguigu.ssyx.model.product.Category;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("api/home/")
public class CategoryApiController {
    @Autowired
    private ProductFeignClient productFeignClient;
    @GetMapping("category")
    public Result showCategory(){
        List<Category> categoryList = productFeignClient.findCategoryList();
        return Result.ok(categoryList);
    }

}
