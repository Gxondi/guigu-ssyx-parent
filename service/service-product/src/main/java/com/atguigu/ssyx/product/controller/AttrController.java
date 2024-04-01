package com.atguigu.ssyx.product.controller;

import com.atguigu.ssyx.common.result.Result;
import com.atguigu.ssyx.model.product.Attr;
import com.atguigu.ssyx.model.product.AttrGroup;
import com.atguigu.ssyx.product.service.AttrService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Api(value = "Attr管理", tags = "平台属性管理")
@RestController
@RequestMapping("/admin/product/attr")
//@CrossOrigin
public class AttrController {
    @Autowired
    private AttrService attrService;
    @ApiOperation(value = "根据分类id获取对应的属性列表")
    @GetMapping("/{GroupId}")
    public Result getAttrList(@PathVariable Long GroupId){
        List<Attr> list = attrService.getAttrList(GroupId);
        return Result.ok(list);
    }
    @ApiOperation(value = "添加属性")
    @PostMapping("save")
    public Result save(@RequestBody Attr attr){
        attrService.save(attr);
        return Result.ok(null);
    }
    @ApiOperation(value = "根据id获取属性")
    @GetMapping("get/{id}")
    public Result getById(@PathVariable Long id){
        Attr attr = attrService.getById(id);
        return Result.ok(attr);
    }
    @ApiOperation(value = "根据id修改属性")
    @PutMapping("/update")
    public Result edit(@RequestBody Attr attr){
        attrService.updateById(attr);
        return Result.ok(null);
    }
    @ApiOperation(value = "根据id删除属性")
    @DeleteMapping("remove/{id}")
    public Result remove(@PathVariable Long id){
        attrService.removeById(id);
        return Result.ok(null);
    }
    @ApiOperation(value = "根据ids批量删除属性")
    @DeleteMapping("batchRemove")
    public Result batchRemove(@RequestBody List<Long> ids){
        attrService.removeByIds(ids);
        return Result.ok(null);
    }
}
