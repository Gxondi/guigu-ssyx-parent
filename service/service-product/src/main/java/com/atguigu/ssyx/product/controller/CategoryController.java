package com.atguigu.ssyx.product.controller;

import com.atguigu.ssyx.common.result.Result;
import com.atguigu.ssyx.model.product.Category;
import com.atguigu.ssyx.product.service.CategoryService;
import com.atguigu.ssyx.vo.product.CategoryQueryVo;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 *
 * 商品分类管理
 *
 */
@Api(value = "Category管理", tags = "商品分类管理")
@RestController
@RequestMapping(value="/admin/product/category")
@SuppressWarnings({"unchecked", "rawtypes"})
//@CrossOrigin
public class CategoryController {
    @Autowired
    private CategoryService categoryService;
    @ApiOperation(value = "查找商品分类")
    @GetMapping("findAllList")
    public Result findAllList(){
        List<Category> list = categoryService.list();
        return Result.ok(list);
    }

    @ApiOperation(value = "获取商品分类分页列表")
    @GetMapping("{page}/{limit}")
    public Result index(
            @ApiParam(name = "page", value = "当前页码", required = true)
            @PathVariable Long page,
            @ApiParam(name = "limit", value = "每页记录数", required = true)
            @PathVariable Long limit,
            @ApiParam(name = "categoryQueryVo", value = "查询对象", required = false)
            CategoryQueryVo categoryQueryVo) {

        Page<Category> pageParam = new Page<>(page, limit);

        IPage<Category> pageModel = categoryService.selectPage(pageParam, categoryQueryVo);

        return Result.ok(pageModel);
    }
    @ApiOperation(value = "添加商品分类")
    @PostMapping("save")
    public Result save(@RequestBody Category category){
        categoryService.save(category);
        return Result.ok(null);

    }
    @ApiOperation(value = "根据id获取商品分类")
    @GetMapping("get/{id}")
    public Result getById(@PathVariable Long id){
        Category category = categoryService.getById(id);
        return Result.ok(category);

    }
    @ApiOperation(value = "根据id修改商品分类")
    @PutMapping("/update")
    public Result edit(@RequestBody Category category){
        categoryService.updateById(category);
        return Result.ok(null);

    }
    @ApiOperation(value = "根据id删除商品分类")
    @DeleteMapping("remove/{id}")
    public Result remove(@PathVariable Long id){
        categoryService.removeById(id);
        return Result.ok(null);
    }
    @ApiOperation(value = "根据ids批量删除商品分类")
    @DeleteMapping("batchRemove")
    public Result batchRemove(@RequestBody List<Long> ids){
        categoryService.removeByIds(ids);
        return Result.ok(null);
    }
}
