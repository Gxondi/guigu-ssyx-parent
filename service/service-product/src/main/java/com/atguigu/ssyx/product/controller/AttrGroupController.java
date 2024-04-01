package com.atguigu.ssyx.product.controller;

import com.atguigu.ssyx.common.result.Result;
import com.atguigu.ssyx.model.product.AttrGroup;
import com.atguigu.ssyx.model.product.Category;
import com.atguigu.ssyx.product.service.AttrGroupService;
import com.atguigu.ssyx.vo.product.AttrGroupQueryVo;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Api(value = "AttrGroup管理", tags = "属性分组管理")
@RestController
@RequestMapping("/admin/product/attrGroup")
//@CrossOrigin
public class AttrGroupController {
    @Autowired
    private AttrGroupService attrGroupService;
    @ApiOperation(value = "分页查询属性分组")
    @GetMapping("/{page}/{limit}")
    public Result pageList(@PathVariable Long page, @PathVariable Long limit, AttrGroupQueryVo  attrGroupQueryVo){
        Page pageParam = new Page<>(page,limit);
        IPage<AttrGroup> result = attrGroupService.selectPage(pageParam,attrGroupQueryVo);
        return Result.ok(result );
    }
    @ApiOperation(value = "添加属性分组")
    @PostMapping("save")
    public Result save(@RequestBody AttrGroup attrGroup){
        attrGroupService.save(attrGroup);
        return Result.ok(null);
    }
    @ApiOperation(value = "根据id获取属性分组")
    @GetMapping("get/{id}")
    public Result getById(@PathVariable Long id){
        AttrGroup attrGroup = attrGroupService.getById(id);
        return Result.ok(attrGroup);
    }
    @ApiOperation(value = "根据id修改属性分组")
    @PutMapping("/update")
    public Result edit(@RequestBody AttrGroup attrGroup){
        attrGroupService.updateById(attrGroup);
        return Result.ok(null);
    }
    @ApiOperation(value = "根据id删除属性分组")
    @DeleteMapping("remove/{id}")
    public Result remove(@PathVariable Long id){
        attrGroupService.removeById(id);
        return Result.ok(null);
    }
    @ApiOperation(value = "根据ids批量删除属性分组")
    @DeleteMapping("batchRemove")
    public Result batchRemove(@RequestBody List<Long> ids){
        attrGroupService.removeByIds(ids);
        return Result.ok(null);
    }
    @ApiOperation(value = "查找商属性分组")
    @GetMapping("findAllList")
    public Result findAllList(){
        List<AttrGroup> list = attrGroupService.list();
        return Result.ok(list);
    }
}
