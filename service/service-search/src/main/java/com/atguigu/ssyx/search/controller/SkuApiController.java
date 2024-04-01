package com.atguigu.ssyx.search.controller;

import com.atguigu.ssyx.common.result.Result;
import com.atguigu.ssyx.model.product.SkuInfo;
import com.atguigu.ssyx.model.search.SkuEs;
import com.atguigu.ssyx.search.service.SkuApiService;
import com.atguigu.ssyx.vo.search.SkuEsQueryVo;
import io.swagger.annotations.ApiOperation;
import org.checkerframework.checker.units.qual.A;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("api/search/sku")
public class SkuApiController {
    @Autowired
    private SkuApiService skuService;
    @Autowired
    private RedisTemplate redisTemplate;

    @ApiOperation(value = "上架商品")
    @GetMapping("inner/upperSku/{skuId}")
    public Result upperGoods(@PathVariable("skuId") Long skuId) {
        skuService.upperSku(skuId);
        return Result.ok(null);
    }

    @ApiOperation(value = "下架商品")
    @GetMapping("inner/lowerSku/{skuId}")
    public Result lowerGoods(@PathVariable("skuId") Long skuId) {
        skuService.lowerSku(skuId);
        return Result.ok(null);
    }

    /**
     * 前端搜索
     */
    @GetMapping("{pageNum}/{limitNum}")
    public Result listSku(@PathVariable("pageNum") Integer pageNum, @PathVariable("limitNum") Integer limitNum, SkuEsQueryVo skuEsQueryVo) {
        Pageable pageable = PageRequest.of(pageNum - 1, limitNum);
        Page<SkuEs> pageModel = skuService.search(pageable, skuEsQueryVo);
        return Result.ok(pageModel);
    }

    @GetMapping("inner/findHostSkuList/{skuId}")
    Boolean incrHotScore(@PathVariable("skuId") Long skuId) {
        skuService.incrHotScore(skuId);
        return true;
    }
}
