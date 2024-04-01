package com.atguigu.ssyx.activity.service;

import com.atguigu.ssyx.model.activity.ActivityInfo;
import com.atguigu.ssyx.model.activity.ActivityRule;
import com.atguigu.ssyx.model.order.CartInfo;
import com.atguigu.ssyx.model.product.SkuInfo;
import com.atguigu.ssyx.vo.activity.ActivityRuleVo;
import com.atguigu.ssyx.vo.order.CartInfoVo;
import com.atguigu.ssyx.vo.order.OrderConfirmVo;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;
import java.util.Map;

public interface ActivityInfoService extends IService<ActivityInfo> {

    IPage<ActivityInfo> selectPageList(Page<ActivityInfo> pageParam);

    Map<String, Object> findActivityRuleList(Long id);

    ActivityInfo getActivityById(Long id);

    void saveActivityRule(ActivityRuleVo activityRuleVo);

    List<SkuInfo> findSkuInfoByKeyword(String keyword);
    /**
     * 远程调用接口
     * 根据skuId获取活动规则
     * @param skuIds
     * @return
     */
    Map<Long, List<String>> findActivity(List<Long> skuIds);

    Map<String, Object> findCouponInfoBySkuIdAndUserId(Long skuId, Long userId);

    List<ActivityRule> findActivityRuleBySkuId(Long skuId);

    OrderConfirmVo findCartActivityAndCoupon(List<CartInfo> cartInfoList, Long userId);
    List<CartInfoVo> findCartActivityList(List<CartInfo> cartInfoList);
}
