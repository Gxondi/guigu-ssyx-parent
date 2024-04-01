package com.atguigu.ssyx.activity.service.Impl;

import com.atguigu.ssyx.activity.mapper.CouponInfoMapper;
import com.atguigu.ssyx.activity.mapper.CouponRangeMapper;
import com.atguigu.ssyx.activity.mapper.CouponUseMapper;
import com.atguigu.ssyx.activity.service.CouponInfoService;
import com.atguigu.ssyx.client.order.ProductFeignClient;
import com.atguigu.ssyx.enums.CouponRangeType;
import com.atguigu.ssyx.model.activity.CouponInfo;
import com.atguigu.ssyx.model.activity.CouponRange;
import com.atguigu.ssyx.model.activity.CouponUse;
import com.atguigu.ssyx.model.order.CartInfo;
import com.atguigu.ssyx.model.product.Category;
import com.atguigu.ssyx.model.product.SkuInfo;
import com.atguigu.ssyx.vo.activity.CouponRuleVo;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class CouponInfoServiceImpl extends ServiceImpl<CouponInfoMapper, CouponInfo> implements CouponInfoService {
    @Autowired
    private CouponInfoMapper couponInfoMapper;
    @Autowired
    private CouponRangeMapper couponRangeMapper;
    @Autowired
    private ProductFeignClient productFeignClient;
    @Autowired
    private CouponUseMapper couponUseMapper;
    @Override
    public List<CouponInfo> selectCouponBySkuIdAndUserId(Long skuId, Long userId) {
        SkuInfo skuInfo = productFeignClient.getSkuInfo(skuId);
        //查询优惠券列表(skuId,userId,categoryId)
        List<CouponInfo> couponInfoList = couponInfoMapper.findCouponBySkuIdAndUserId(skuId, userId, skuInfo.getCategoryId());
        return couponInfoList;
    }

    @Override
    public IPage<CouponInfo> selectPage(Page<CouponInfo> pageParam) {
        Page<CouponInfo> couponInfoPage = baseMapper.selectPage(pageParam, null);
        List<CouponInfo> records = couponInfoPage.getRecords();
        for (CouponInfo record : records) {
            record.setCouponTypeString(record.getCouponType().getComment());
            if (record.getRangeType() != null) {
                record.setRangeTypeString(record.getRangeType().getComment());
            }
        }
        return couponInfoPage;
    }

    @Override
    public CouponInfo getCouponById(Long id) {
        CouponInfo couponInfo = baseMapper.selectById(id);
        couponInfo.setCouponTypeString(couponInfo.getCouponType().getComment());
        if (couponInfo.getRangeType() != null) {
            couponInfo.setRangeTypeString(couponInfo.getRangeType().getComment());
        }
        return couponInfo;
    }

    //    /**
//     * 获取优惠券规则列表
//     *
//     * @param id 优惠券id
//     */
//    @Override
//    public Map<String, Object> findCouponRuleList(Long id) {
//        Map<String, Object> result = new HashMap<>();
//        //查询优惠券范围
//        //查询sku
//        LambdaQueryWrapper<CouponRange> wrapper = new LambdaQueryWrapper<CouponRange>().eq(CouponRange::getCouponId, id);
//        LambdaQueryWrapper<CouponRange> sku = wrapper.eq(CouponRange::getRangeType, CouponRangeType.SKU.getCode());
//        List<CouponRange> couponRangeList = couponRangeMapper.selectList(sku);
//        List<SkuInfo> skuInfoList = productFeignClient.findSkuListByIds(couponRangeList.stream().map(CouponRange::getRangeId).collect(Collectors.toList()));
//        result.put("skuInfoList", skuInfoList);
//        //查询分类
//        LambdaQueryWrapper<CouponRange> category = new LambdaQueryWrapper<CouponRange>()
//                .eq(CouponRange::getCouponId, id)
//                .eq(CouponRange::getRangeType, CouponRangeType.CATEGORY.getCode());
//        List<CouponRange> couponRanges = couponRangeMapper.selectList(category);
//        List<Category> categoryList = productFeignClient.findCategoryListByIds(couponRanges.stream().map(CouponRange::getRangeId).collect(Collectors.toList()));
//        result.put("categoryList", categoryList);
//        return result;
//    }
    @Override
    public Map<String, Object> findCouponRuleList(Long couponId) {
        Map<String, Object> result = new HashMap<>();
        CouponInfo couponInfo = this.getById(couponId);
        //查询优惠券范围(sku)
        LambdaQueryWrapper<CouponRange> wrapper = new LambdaQueryWrapper<CouponRange>().eq(CouponRange::getCouponId, couponId);
        List<CouponRange> couponRanges = couponRangeMapper.selectList(wrapper);
        List<Long> rangeIds = couponRanges.stream().map(CouponRange::getRangeId).collect(Collectors.toList());
        if (!CollectionUtils.isEmpty(rangeIds)) {
            if (couponInfo.getRangeType() == CouponRangeType.SKU) {
                List<SkuInfo> skuInfoList = productFeignClient.findSkuListByIds(rangeIds);
                result.put("skuInfoList", skuInfoList);
            }
            if (couponInfo.getRangeType() == CouponRangeType.CATEGORY) {
                List<Category> categoryList = productFeignClient.findCategoryListByIds(rangeIds);
                result.put("categoryList", categoryList);
            }
        }
        //查询优惠券范围(category)
        return result;
    }

    @Override
    public void saveCouponRule(CouponRuleVo couponRuleVo) {
        Long couponId = couponRuleVo.getCouponId();
        //  更新数据
        CouponInfo couponInfo = this.getById(couponRuleVo.getCouponId());
        // couponInfo.setCouponType();
        couponInfo.setRangeType(couponRuleVo.getRangeType());
        couponInfo.setConditionAmount(couponRuleVo.getConditionAmount());
        couponInfo.setAmount(couponRuleVo.getAmount());
        couponInfo.setConditionAmount(couponRuleVo.getConditionAmount());
        couponInfo.setRangeDesc(couponRuleVo.getRangeDesc());
        couponInfoMapper.updateById(couponInfo);
        //更新优惠券信息
        Integer code = couponRuleVo.getRangeType().getCode();
        //删除优惠券范围列表
        couponRangeMapper.delete(new LambdaQueryWrapper<CouponRange>().eq(CouponRange::getCouponId, couponId).eq(CouponRange::getRangeType, code));
        //保存优惠券范围列表
        List<CouponRange> couponRangeList = couponRuleVo.getCouponRangeList();
        if (couponRangeList != null && couponRangeList.size() > 0) {
            for (CouponRange couponRange : couponRangeList) {
                if (couponRange.getRangeType().getCode() == CouponRangeType.SKU.getCode()) {
                    couponRange.setCouponId(couponId);
                    couponRange.setRangeType(couponRuleVo.getRangeType());
                    couponRangeMapper.insert(couponRange);
                } else if (couponRange.getRangeType().getCode() == CouponRangeType.CATEGORY.getCode()) {
                    couponRange.setCouponId(couponId);
                    couponRange.setRangeType(couponRuleVo.getRangeType());
                    couponRangeMapper.insert(couponRange);
                }
            }
        }


    }

    @Override
    public List<CouponInfo> findCouponListByUserId(List<CartInfo> cartInfoList, Long userId) {
        //根据userId 查寻当前用户的优惠劵列表 coupon_use,coupon_info
        List<CouponInfo> couponInfoList = couponInfoMapper.findCouponListByUserId(userId);
        if (CollectionUtils.isEmpty(couponInfoList)) {
            return null;
        }
        List<Long> couponIdList = couponInfoList.stream().map(couponInfo -> {
            return couponInfo.getId();
        }).collect(Collectors.toList());
        //查询优惠券范围
        LambdaQueryWrapper<CouponRange> wrapper = new LambdaQueryWrapper<CouponRange>();
        wrapper.in(CouponRange::getCouponId, couponIdList);
        List<CouponRange> couponRangeList = couponRangeMapper.selectList(wrapper);
        //根据couponId对应的skuId
        Map<Long, List<Long>> couponIdToSkuIdMap = this.findCouponIdToSkuIdMap(cartInfoList, couponRangeList);
        BigDecimal reduceAmount = new BigDecimal("0");
        CouponInfo optimalCouponInfo = null;
        //计算优惠券的优惠金额
        for (CouponInfo couponInfo : couponInfoList) {
            if (CouponRangeType.ALL == couponInfo.getRangeType()){
                //全场通用
                BigDecimal totalAmount = this.computeTotalAmount(cartInfoList);
                if(totalAmount.subtract(couponInfo.getConditionAmount()).doubleValue() >= 0){
                    couponInfo.setIsSelect(1);
                }
            }else {
                List<Long> skuIdList = couponIdToSkuIdMap.get(couponInfo.getId());
                List<CartInfo> currentCartInfoList = cartInfoList.stream().filter(cartInfo -> {
                    return skuIdList.contains(cartInfo.getSkuId());
                }).collect(Collectors.toList());
                BigDecimal totalAmount = this.computeTotalAmount(currentCartInfoList);
                if(totalAmount.subtract(couponInfo.getConditionAmount()).doubleValue() >= 0){
                    couponInfo.setIsSelect(1);
                }

            }
            if (couponInfo.getIsSelect().intValue() == 1 && couponInfo.getAmount().subtract(reduceAmount).doubleValue() > 0) {
                reduceAmount = couponInfo.getAmount();
                optimalCouponInfo = couponInfo;
            }
        }
        if(null != optimalCouponInfo) {
            optimalCouponInfo.setIsOptimal(1);
        }
        return couponInfoList;
    }
    /**
     * 获取购物车对应的优惠券
     * @param cartInfoList
     * @param couponId
     * @return
     */
    @Override
    public CouponInfo findRangeSkuIdList(List<CartInfo> cartInfoList, Long couponId) {
        //查询优惠券
        CouponInfo couponInfo = couponInfoMapper.selectById(couponId);
        //查询优惠券范围
        LambdaQueryWrapper<CouponRange> wrapper = new LambdaQueryWrapper<CouponRange>().eq(CouponRange::getCouponId, couponId);
        List<CouponRange> couponRangeList = couponRangeMapper.selectList(wrapper);
        /*
            数据结构:
            {
                couponId : skuIdList
                   1     : [1,2,3,4,5]
            }

         */
        Map<Long, List<Long>> couponIdToSkuIdMap = this.findCouponIdToSkuIdMap(cartInfoList, couponRangeList);
        List<Long> value = couponIdToSkuIdMap.entrySet().iterator().next().getValue();
        couponInfo.setSkuIdList(value);
        return couponInfo;
    }

    @Override
    public void updateCouponInfoStatus(Long userId, Long couponId, Long id) {
        LambdaQueryWrapper<CouponUse> wrapper = new LambdaQueryWrapper<CouponUse>().eq(CouponUse::getUserId, userId).eq(CouponUse::getCouponId, couponId);
        CouponUse couponUse = couponUseMapper.selectOne(wrapper);
        couponUse.setCouponId(couponId);
        couponUseMapper.updateById(couponUse);
    }

    private BigDecimal computeTotalAmount(List<CartInfo> cartInfoList) {
        BigDecimal total = new BigDecimal("0");
        for (CartInfo cartInfo : cartInfoList) {
            //是否选中
            if(cartInfo.getIsChecked().intValue() == 1) {
                BigDecimal itemTotal = cartInfo.getCartPrice().multiply(new BigDecimal(cartInfo.getSkuNum()));
                total = total.add(itemTotal);
            }
        }
        return total;
    }

    private Map<Long,List<Long>> findCouponIdToSkuIdMap(List<CartInfo> cartInfoList, List<CouponRange> couponRangeList) {
        Map<Long,List<Long>> couponIdToSkuIdMap = new HashMap<>();
        //根据CouponId分组
        Map<Long, List<CouponRange>> collect = couponRangeList.stream().collect(Collectors.groupingBy(CouponRange::getCouponId));

        Iterator<Map.Entry<Long, List<CouponRange>>> iterator = collect.entrySet().iterator();
        //封装couponId对应的skuId
        while(iterator.hasNext()){
            Map.Entry<Long, List<CouponRange>> entry = iterator.next();
            Long couponId = entry.getKey();
            List<CouponRange> couponRanges = entry.getValue();
            Set<Long> skuIdSet = new HashSet<>();
            for (CartInfo cartInfo : cartInfoList) {
                for (CouponRange couponRange : couponRanges) {
                    if(couponRange.getRangeType() == CouponRangeType.SKU && couponRange.getRangeId().equals(cartInfo.getSkuId())){
                        skuIdSet.add(cartInfo.getSkuId());
                    }else if(couponRange.getRangeType() == CouponRangeType.CATEGORY && couponRange.getRangeId().equals(cartInfo.getCategoryId())){
                        skuIdSet.add(cartInfo.getSkuId());
                    }else {
                    }
                }
            }
            couponIdToSkuIdMap.put(couponId,new ArrayList<>(skuIdSet));
        }

        return couponIdToSkuIdMap;
    }
}
