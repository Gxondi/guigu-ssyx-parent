package com.atguigu.ssyx.sys.controller;

import com.atguigu.ssyx.common.result.Result;
import com.atguigu.ssyx.model.sys.Region;
import com.atguigu.ssyx.sys.service.RegionService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Api(tags = "地区管理")
@RestController
@RequestMapping("/admin/sys/region")
//@CrossOrigin
public class RegionController {
    @Autowired
    private RegionService regionService;
    //findRegionByKeyword
    @ApiOperation(value = "根据关键字查询地区")
    @GetMapping("/findRegionByKeyword/{keyword}")
    public Result findRegionByKeyword(@PathVariable String keyword) {
        List<Region> region = regionService.findRegionByKeyword(keyword);
        return Result.ok(region);
    }
}
