package com.atguigu.ssyx.activity.controller;

import com.atguigu.ssyx.activity.service.CouponInfoService;
import com.atguigu.ssyx.common.result.Result;
import com.atguigu.ssyx.model.activity.CouponInfo;
import com.atguigu.ssyx.model.activity.CouponRange;
import com.atguigu.ssyx.vo.activity.CouponRuleVo;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.checkerframework.checker.units.qual.A;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Api(tags = "优惠券管理")
@RestController
@RequestMapping("/admin/activity/couponInfo")
//@CrossOrigin
public class CouponController {
    @Autowired
    private CouponInfoService couponInfoService;
    @ApiOperation(value = "获取分页列表")
    @GetMapping("{page}/{limit}")
    public Result index(@PathVariable Long page, @PathVariable Long limit){
        Page<CouponInfo> pageParam = new Page<>(page, limit);
        IPage<CouponInfo> result =  couponInfoService.selectPage(pageParam);
        return Result.ok(result);
    }
    @ApiOperation(value = "添加优惠券")
    @PostMapping("save")
    public Result save(@RequestBody CouponInfo couponInfo){
        couponInfoService.save(couponInfo);
        return Result.ok(null);
    }
    @ApiOperation(value = "获取优惠券")
    @GetMapping("get/{id}")
    public Result get(@PathVariable Long id){
        CouponInfo result = couponInfoService.getCouponById(id);
        return Result.ok(result);
    }
    @ApiOperation(value = "修改优惠券")
    @PutMapping("update")
    public Result update(@RequestBody CouponInfo couponInfo){
        couponInfoService.updateById(couponInfo);
        return Result.ok(null);
    }
    @ApiOperation(value = "删除优惠券")
    @DeleteMapping("remove/{id}")
    public Result delete(@PathVariable Long id){
        boolean result = couponInfoService.removeById(id);
        return Result.ok(null);
    }

    @ApiOperation(value = "获取优惠券规则列表")
    @GetMapping("findCouponRuleList/{id}")
    public Result findCouponRuleList(@PathVariable Long id){
        //rangeId和couponId
        return Result.ok(couponInfoService.findCouponRuleList(id));
    }

    @ApiOperation(value = "保存优惠券规则列表")
    @PostMapping("saveCouponRule")
    public Result saveCouponRule(@RequestBody CouponRuleVo couponRuleVo){
        couponInfoService.saveCouponRule(couponRuleVo);
        return Result.ok(null);
    }
}
