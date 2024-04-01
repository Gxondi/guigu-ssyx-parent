package com.atguigu.ssyx.product.controller;

import com.atguigu.ssyx.common.result.Result;
import com.atguigu.ssyx.common.service.RabbitService;
import com.atguigu.ssyx.model.product.Attr;
import com.atguigu.ssyx.model.product.AttrGroup;
import com.atguigu.ssyx.model.product.SkuAttrValue;
import com.atguigu.ssyx.model.product.SkuInfo;
import com.atguigu.ssyx.product.service.AttrService;
import com.atguigu.ssyx.product.service.SkuInfoService;
import com.atguigu.ssyx.vo.product.AttrGroupQueryVo;
import com.atguigu.ssyx.vo.product.SkuInfoQueryVo;
import com.atguigu.ssyx.vo.product.SkuInfoVo;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.checkerframework.checker.units.qual.A;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Api(value = "SkuInfo管理", tags = "sku信息管理")
@RestController
@RequestMapping("/admin/product/skuInfo")
//@CrossOrigin
public class SkuInfoController {
    @Autowired
    private SkuInfoService skuInfoService;

    @ApiOperation(value = "分页查询sku信息")
    @GetMapping("/{page}/{limit}")
    public Result pageList(@PathVariable Long page, @PathVariable Long limit, SkuInfoQueryVo skuInfoQueryVo) {
        Page pageParam = new Page<>(page, limit);
        IPage<SkuInfo> result = skuInfoService.selectPage(pageParam, skuInfoQueryVo);
        return Result.ok(result);
    }

    @ApiOperation(value = "根据id删除sku信息")
    @DeleteMapping("remove/{id}")
    public Result remove(@PathVariable Long id) {
        skuInfoService.removeById(id);
        return Result.ok(null);
    }

    @ApiOperation(value = "根据ids批量删除sku信息")
    @DeleteMapping("batchRemove")
    public Result batchRemove(@RequestBody List<Long> ids) {
        skuInfoService.removeByIds(ids);
        return Result.ok(null);
    }

    @ApiOperation(value = "添加sku信息")
    @PostMapping("save")
    public Result save(@RequestBody SkuInfoVo skuInfoVo) {
        skuInfoService.saveSkuInfo(skuInfoVo);
        return Result.ok(null);
    }
    @ApiOperation(value = "根据id获取sku信息")
    @GetMapping("get/{skuId}")
    public Result getById(@PathVariable Long skuId) {
        SkuInfoVo skuInfo = skuInfoService.getSkuInfoById(skuId);
        return Result.ok(skuInfo);
    }
    @ApiOperation(value = "保存sku信息")
    @PutMapping("update")
    public Result update(@RequestBody SkuInfoVo skuInfoVo) {
        skuInfoService.updateSkuInfo(skuInfoVo);
        return Result.ok(null);
    }
    @ApiOperation(value = "是否是新人专享")
    @GetMapping("isNewPerson/{skuId}/{status}")
    public Result updateNewPerson(@PathVariable Long skuId, @PathVariable Integer status){
        skuInfoService.updateNewPerson(skuId, status);
        return Result.ok(null);

    }
    @ApiOperation(value = "上架/下架")
    @GetMapping("publish/{skuId}/{status}")
    public Result publish(@PathVariable Long skuId, @PathVariable Integer status){
        skuInfoService.updatePublish(skuId, status);
        return Result.ok(null);

    }
    @ApiOperation(value = "审核状态")
    @GetMapping("check/{skuId}/{status}")
    public Result check(@PathVariable Long skuId, @PathVariable Integer status){
        skuInfoService.updateCheck(skuId, status);
        return Result.ok(null);

    }
}
