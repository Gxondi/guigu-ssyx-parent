package com.atguigu.ssyx.product.Api;

import com.atguigu.ssyx.model.product.Category;
import com.atguigu.ssyx.model.product.SkuAttrValue;
import com.atguigu.ssyx.model.product.SkuImage;
import com.atguigu.ssyx.model.product.SkuInfo;
import com.atguigu.ssyx.product.service.*;
import com.atguigu.ssyx.vo.product.SkuInfoVo;
import com.atguigu.ssyx.vo.product.SkuStockLockVo;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import io.swagger.annotations.ApiOperation;
import org.checkerframework.checker.units.qual.A;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("api/product")
public class ProductInnnerController {
    @Autowired
    private SkuInfoService skuInfoService;
    @Autowired
    private CategoryService categoryService;
    @Autowired
    private SkuImageService skuImageService;
    @Autowired
    private SkuAttrValueService attrValueService;
    @ApiOperation(value = "根据分类id获取分类信息")
    @GetMapping("inner/getCategory/{categoryId}")
    public Category getCategory(@PathVariable Long categoryId) {
        return categoryService.getById(categoryId);
    }
    @ApiOperation(value = "根据skuId获取sku信息")
    @GetMapping("inner/getSkuInfo/{skuId}")
    public SkuInfo getSkuInfo(@PathVariable("skuId") Long skuId) {
        return skuInfoService.getById(skuId);
    }

    @ApiOperation(value = "根据skuIds获取sku信息")
    @GetMapping("inner/findSkuListByIds")
    List<SkuInfo> findSkuListByIds(@RequestParam(value = "skuIds",required = false) List<Long> skuIds){
        if (skuIds == null || skuIds.size() == 0) {
            return null;
        }
        return skuInfoService.listByIds(skuIds);
    }
    @ApiOperation(value = "根据关键字获取sku信息")
    @GetMapping("inner/getSkuInfoByKeyWord")
    List<SkuInfo> getSkuInfoByKeyWord(@RequestParam(value = "skuIds",required = false) String keyword) {
        List<SkuInfo> skuInfoByKeyword = skuInfoService.findSkuInfoByKeyword(keyword);
        return skuInfoByKeyword;
    }
    @ApiOperation(value = "获取目录信息")
    @GetMapping("inner/findCategoryListByIds")
    List<Category> findCategoryListByIds(@RequestParam(value = "categoryIds",required = false) List<Long> categoryIds){
        if (categoryIds == null || categoryIds.size() == 0) {
            return null;
        }
        List<Category> categories = categoryService.listByIds(categoryIds);
        return categories;
    }
    @ApiOperation(value = "获取目录信息")
    @GetMapping("inner/findCategoryList")
    List<Category> findCategoryList(){
        return categoryService.list();
    }
    /**
     * 获取新人专项活动
     *
     */
    @GetMapping("inner/findNerPersonActivity")
    List<SkuInfo> findNerPersonActivity(){
        List<SkuInfo> skuInfoList = skuInfoService.findNerPersonActivity();
        return skuInfoList;
    }
    /**
     * 获取sku的信息
     */
    @GetMapping("inner/getSkuInfoVo/{skuId}")
    SkuInfoVo getSkuInfoVo(@PathVariable("skuId") Long skuId){
        SkuInfoVo skuInfoVo = new SkuInfoVo();
        SkuInfoVo skuInfo = skuInfoService.getSkuInfoById(skuId);
        BeanUtils.copyProperties(skuInfo,skuInfoVo);
        List<SkuImage> imageList = skuImageService.findBySkuId(skuId);
        skuInfoVo.setSkuImagesList(imageList);
        List<SkuAttrValue> SkuAttrList = attrValueService.findBySkuId(skuId);
        skuInfoVo.setSkuAttrValueList(SkuAttrList);
        return skuInfoVo;
    }

    @PostMapping("inner/checkAndLock/{orderNo}")
    Boolean checkAndLock(@RequestBody List<SkuStockLockVo> skuStockLockVos, @PathVariable String orderNo){
       return skuInfoService.checkAndLock(skuStockLockVos,orderNo);
    }
}
