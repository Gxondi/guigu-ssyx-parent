package com.atguigu.ssyx.client.order;

import com.atguigu.ssyx.model.product.Category;
import com.atguigu.ssyx.model.product.SkuInfo;
import com.atguigu.ssyx.vo.product.SkuInfoVo;
import com.atguigu.ssyx.vo.product.SkuStockLockVo;
import io.swagger.annotations.ApiOperation;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@FeignClient(value = "service-product")//指定调用的服务
public interface ProductFeignClient {

    @GetMapping("/api/product/inner/getCategory/{categoryId}")
    public Category getCategory(@PathVariable("categoryId") Long categoryId);

    @GetMapping("/api/product/inner/getSkuInfo/{skuId}")
    public SkuInfo getSkuInfo(@PathVariable("skuId") Long skuId);

    @GetMapping("/api/product/inner/findSkuListByIds")
    public List<SkuInfo> findSkuListByIds(@RequestParam(value = "skuIds",required = false) List<Long> skuIds);

    @GetMapping("/api/product/inner/getSkuInfoByKeyWord")
    List<SkuInfo> getSkuInfoByKeyWord(@RequestParam(value = "skuIds",required = false) String keyword);

    @GetMapping("/api/product/inner/findCategoryListByIds")
    List<Category> findCategoryListByIds(@RequestParam(value = "categoryIds",required = false)  List<Long> categoryIds);

    @ApiOperation(value = "获取目录信息")
    @GetMapping("/api/product/inner/findCategoryList")
    List<Category> findCategoryList();
    @GetMapping("/api/product/inner/findNerPersonActivity")
    List<SkuInfo> findNerPersonActivity();
    @GetMapping("/api/product/inner/getSkuInfoVo/{skuId}")
    SkuInfoVo getSkuInfoVo(@PathVariable("skuId") Long skuId);

    @PostMapping("/api/product/inner/checkAndLock/{orderNo}")
    Boolean checkAndLock(@RequestBody List<SkuStockLockVo> skuStockLockVos, @PathVariable String orderNo);
}