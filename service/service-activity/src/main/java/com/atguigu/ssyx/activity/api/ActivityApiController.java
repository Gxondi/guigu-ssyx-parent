package com.atguigu.ssyx.activity.api;

import com.atguigu.ssyx.activity.service.ActivityInfoService;
import com.atguigu.ssyx.activity.service.CouponInfoService;
import com.atguigu.ssyx.model.activity.CouponInfo;
import com.atguigu.ssyx.model.order.CartInfo;
import com.atguigu.ssyx.vo.order.CartInfoVo;
import com.atguigu.ssyx.vo.order.OrderConfirmVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("api/activity")
public class ActivityApiController {
    @Autowired
    private ActivityInfoService activityInfoService;
    @Autowired
    private CouponInfoService couponInfoService;
    @PostMapping("inner/findActivity")
    Map<Long, List<String>> findActivity(@RequestBody List<Long> skuIds){
        Map<Long, List<String>> activityMap= activityInfoService.findActivity(skuIds);
        return activityMap;
    }

    @GetMapping("inner/findCouponInfoBySkuIdAndUserId/{skuId}/{userId}")
    Map<String, Object> findCouponInfoBySkuIdAndUserId(@PathVariable("skuId") Long skuId, @PathVariable("userId") Long userId){
        Map<String, Object> couponInfo = activityInfoService.findCouponInfoBySkuIdAndUserId(skuId, userId);
        return couponInfo;
    }
    @PostMapping("inner/findCouponInfoBySkuIdAndUserId/{userId}")
    OrderConfirmVo findCartActivityAndCoupon(@PathVariable Long userId, @RequestBody List<CartInfo> cartInfoList){
        return activityInfoService.findCartActivityAndCoupon(cartInfoList,userId);
    }

    @PostMapping("inner/findCartActivityList")
    List<CartInfoVo> findCartActivityList(@RequestBody List<CartInfo> cartInfoParamList){
        return activityInfoService.findCartActivityList(cartInfoParamList);
    }

    @PostMapping("inner/findRangeSkuIdList/{couponId}")
    CouponInfo findRangeSkuIdList(@RequestBody List<CartInfo> cartInfoList, @PathVariable Long couponId){
        return couponInfoService.findRangeSkuIdList(cartInfoList, couponId);
    }

    @GetMapping("inner/updateCouponInfoStatus/{userId}/{couponId}/{id}")
    void updateCouponInfoStatus(@PathVariable Long userId,@PathVariable  Long couponId,@PathVariable  Long id){
        couponInfoService.updateCouponInfoStatus(userId, couponId, id);
    }
}
