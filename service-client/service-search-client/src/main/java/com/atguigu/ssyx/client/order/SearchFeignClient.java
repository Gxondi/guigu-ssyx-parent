package com.atguigu.ssyx.client.order;

import com.atguigu.ssyx.model.search.SkuEs;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;

@FeignClient(value = "service-search")//指定调用的服务
public interface SearchFeignClient {
    @GetMapping("api/search/inner/findHostSkuList")
    List<SkuEs> findHostSkuList();
    @GetMapping("api/search/sku/inner/findHostSkuList/{skuId}")
    Boolean incrHotScore(@PathVariable("skuId") Long skuId);
}