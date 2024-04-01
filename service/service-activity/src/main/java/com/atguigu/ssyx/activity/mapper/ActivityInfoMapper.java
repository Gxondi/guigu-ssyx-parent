package com.atguigu.ssyx.activity.mapper;

import com.atguigu.ssyx.model.activity.ActivityInfo;
import com.atguigu.ssyx.model.activity.ActivityRule;
import com.atguigu.ssyx.model.activity.ActivitySku;
import com.atguigu.ssyx.model.activity.CouponInfo;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

@Repository
public interface ActivityInfoMapper extends BaseMapper<ActivityInfo> {
    List<Long> selectSkuListExist(@Param("skuIdList")List<Long> skuIdList);

    List<ActivityRule> selectActivityRuleList(@Param("skuId") Long skuId);

    List<CouponInfo> selectCouponBySkuIdAndUserId(Long skuId, Long userId);

    List<ActivitySku> selectCartActivityList(@Param("skuIdList")List<Long> skuIdList);
}
