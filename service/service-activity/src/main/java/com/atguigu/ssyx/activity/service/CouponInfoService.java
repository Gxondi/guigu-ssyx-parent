package com.atguigu.ssyx.activity.service;

import com.atguigu.ssyx.model.activity.CouponInfo;
import com.atguigu.ssyx.model.activity.CouponRange;
import com.atguigu.ssyx.model.order.CartInfo;
import com.atguigu.ssyx.model.product.SkuInfo;
import com.atguigu.ssyx.vo.activity.CouponRuleVo;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;
import java.util.Map;

public interface CouponInfoService extends IService<CouponInfo> {
     List<CouponInfo> selectCouponBySkuIdAndUserId(Long skuId, Long userId);

    IPage<CouponInfo> selectPage(Page<CouponInfo> pageParam);

    CouponInfo getCouponById(Long id);

    Map<String, Object> findCouponRuleList(Long id);

    void saveCouponRule(CouponRuleVo couponRuleVo);

    List<CouponInfo> findCouponListByUserId(List<CartInfo> cartInfoList, Long userId);

    CouponInfo findRangeSkuIdList(List<CartInfo> cartInfoList, Long couponId);

    void updateCouponInfoStatus(Long userId, Long couponId, Long id);
}
