package com.atguigu.ssyx.client.order;

import com.atguigu.ssyx.model.activity.CouponInfo;
import com.atguigu.ssyx.model.order.CartInfo;
import com.atguigu.ssyx.vo.order.CartInfoVo;
import com.atguigu.ssyx.vo.order.OrderConfirmVo;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@FeignClient(value = "service-activity")//指定调用的服务
public interface ActivityFeignClient {

    @PostMapping("api/activity/inner/findActivity")
    Map<Long, List<String>> findActivity(@RequestBody List<Long> skuIds);

    @GetMapping("api/activity/inner/findCouponInfoBySkuIdAndUserId/{skuId}/{userId}")
    Map<String, Object> findCouponInfoBySkuIdAndUserId(@PathVariable("skuId") Long skuId, @PathVariable("userId") Long userId);

    @PostMapping("api/activity/inner/findCouponInfoBySkuIdAndUserId/{userId}")
    OrderConfirmVo findCartActivityAndCoupon(@PathVariable Long userId, @RequestBody List<CartInfo> cartInfoList);

    @PostMapping("api/activity/inner/findCartActivityList")
    List<CartInfoVo> findCartActivityList(@RequestBody List<CartInfo> cartInfoParamList);
    @PostMapping("api/activity/inner/findRangeSkuIdList/{couponId}")
    CouponInfo findRangeSkuIdList(@RequestBody List<CartInfo> cartInfoList,@PathVariable Long couponId);
    @GetMapping("api/activity/inner/updateCouponInfoStatus/{userId}/{couponId}/{id}")
    void updateCouponInfoStatus(@PathVariable Long userId,@PathVariable  Long couponId,@PathVariable  Long id);
}