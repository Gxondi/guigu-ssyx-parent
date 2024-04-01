package com.atguigu.ssyx.sys.controller;

import com.atguigu.ssyx.common.result.Result;
import com.atguigu.ssyx.model.sys.RegionWare;
import com.atguigu.ssyx.model.sys.Ware;
import com.atguigu.ssyx.sys.service.RegionWareService;
import com.atguigu.ssyx.sys.service.WareService;
import com.atguigu.ssyx.vo.sys.RegionWareQueryVo;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@Api(tags = "区域仓库管理")
@RestController
@RequestMapping("/admin/sys/regionWare")
//@CrossOrigin
public class RegionWareController {
    @Autowired
    private RegionWareService regionWareService;
    @ApiOperation(value = "分页查询区域仓库")
    @GetMapping("/{page}/{limit}")
    public Result getPageList(@PathVariable Long page, @PathVariable Long limit, RegionWareQueryVo regionWareQueryVo) {
        Page pageParam = new Page(page, limit);
        IPage<RegionWare> result =  regionWareService.selectPage(pageParam, regionWareQueryVo);
        return Result.ok(result);
    }
    @ApiOperation(value = "删除区域仓库")
    @DeleteMapping("remove/{id}")
    public Result remove(@PathVariable Long id) {
        regionWareService.removeById(id);
        return Result.ok(null);
    }
    @ApiOperation(value = "取消开通区域仓库")
    @PostMapping("updateStatus/{id}/{status}")
    public Result updateStatus(@PathVariable Long id, @PathVariable Integer status) {
        regionWareService.updateStatus(id, status);
        return Result.ok(null);
    }
    @ApiOperation(value = "添加开通区域仓库")
    @PostMapping("save")
    public Result save(@RequestBody RegionWare regionWare) {
        regionWareService.saveRegionWare(regionWare);
        return Result.ok(null);
    }
}
