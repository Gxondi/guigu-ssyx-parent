package com.atguigu.ssyx.activity.mapper;

import com.atguigu.ssyx.model.activity.CouponInfo;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CouponInfoMapper extends BaseMapper<CouponInfo> {
    List<CouponInfo> findCouponBySkuIdAndUserId(@Param("skuId") Long skuId,
                                                @Param("userId") Long userId,
                                                @Param("categoryId") Long categoryId);

    List<CouponInfo> findCouponListByUserId(@Param("userId") Long userId);
}
