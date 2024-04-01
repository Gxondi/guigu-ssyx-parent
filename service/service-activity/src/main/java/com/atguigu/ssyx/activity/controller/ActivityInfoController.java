package com.atguigu.ssyx.activity.controller;

import com.atguigu.ssyx.activity.service.ActivityInfoService;
import com.atguigu.ssyx.common.result.Result;
import com.atguigu.ssyx.model.activity.ActivityInfo;
import com.atguigu.ssyx.model.activity.ActivityRule;
import com.atguigu.ssyx.model.product.SkuInfo;
import com.atguigu.ssyx.vo.activity.ActivityRuleVo;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.checkerframework.checker.units.qual.A;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@Api(tags = "活动管理")
@RestController
@RequestMapping("/admin/activity/activityInfo")
//@CrossOrigin
public class ActivityInfoController {
    @Autowired
    private ActivityInfoService activityInfoService;

    @ApiOperation(value = "获取分页列表")
    @GetMapping("{page}/{limit}")
    public Result index(
            @ApiParam(name = "page", value = "当前页码", required = true)
            @PathVariable Long page,
            @ApiParam(name = "limit", value = "每页记录数", required = true)
            @PathVariable Long limit) {
        Page<ActivityInfo> pageParam = new Page<>(page, limit);
        IPage<ActivityInfo> pageModel = activityInfoService.selectPageList(pageParam);
        return Result.ok(pageModel);
    }
    @ApiOperation(value = "保存规则")
    @PostMapping("saveActivityRule")
    public Result saveActivityRule(@RequestBody ActivityRuleVo activityRuleVo){
        activityInfoService.saveActivityRule(activityRuleVo);
        return Result.ok(null);
    }
    @ApiOperation(value = "获取活动范围")
    @GetMapping("findSkuInfoByKeyword/{keyword}")
    public Result findSkuInfoByKeyword(@PathVariable String keyword){
        List<SkuInfo> result = activityInfoService.findSkuInfoByKeyword(keyword);
        return Result.ok(result);
    }
    @ApiOperation(value = "规则列表")
    @GetMapping("findActivityRuleList/{id}")
    public Result findActivityRuleList(@PathVariable Long id){
        Map<String, Object> activityRules = activityInfoService.findActivityRuleList(id);
        return Result.ok(activityRules);
    }
    @ApiOperation(value = "添加活动")
    @PostMapping("save")
    public Result save(@RequestBody ActivityInfo activityInfo){
        boolean result = activityInfoService.save(activityInfo);
        if(result){
            return Result.ok(null );
        }else{
            return Result.fail(null );
        }
    }
    @ApiOperation(value = "根据id获取活动")
    @GetMapping("get/{id}")
    public Result get(@PathVariable Long id){
        ActivityInfo activityInfo = activityInfoService.getActivityById(id);
        return Result.ok(activityInfo);
    }
    @ApiOperation(value = "更新活动")
    @PutMapping("update")
    public Result update(@RequestBody  ActivityInfo activityInfo){
        boolean b = activityInfoService.updateById(activityInfo);
        if(b) {
            return Result.ok(null);
        }else {
            return Result.fail(null);
        }
    }
    @ApiOperation(value = "删除活动")
    @DeleteMapping("remove/{id}")
    public Result removeById(@PathVariable String id){
        boolean result = activityInfoService.removeById(id);
        if(result){
            return Result.ok(null  );
        }else{
            return Result.fail(null );
        }
    }

}
