package com.atguigu.ssyx.search.api;

import com.atguigu.ssyx.common.result.Result;
import com.atguigu.ssyx.model.product.SkuInfo;
import com.atguigu.ssyx.model.search.SkuEs;
import com.atguigu.ssyx.search.service.SkuApiService;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("api/search")
public class SkuApiInnerController {
    @Autowired
    private SkuApiService skuApiService;

    @GetMapping("inner/findHostSkuList")
    public List<SkuEs> findHostSkuList() {
        List<SkuEs> skuInfoList = skuApiService.findHostSkuList();
        return skuInfoList;
    }
}
//    @GetMapping("inner/skuList")
//    List<SkuInfo> skuList(SkuEs skuEs, Long page, Long limit){
//        List<SkuInfo> skuInfoList = skuApiService.findSkuInfoList(page,limit, skuEs);
//        return skuInfoList;
//    }


