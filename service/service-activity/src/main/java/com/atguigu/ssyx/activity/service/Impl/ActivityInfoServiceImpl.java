package com.atguigu.ssyx.activity.service.Impl;

import com.atguigu.ssyx.activity.mapper.ActivityInfoMapper;
import com.atguigu.ssyx.activity.mapper.ActivityRuleMapper;
import com.atguigu.ssyx.activity.mapper.ActivitySkuMapper;
import com.atguigu.ssyx.activity.service.ActivityInfoService;
import com.atguigu.ssyx.activity.service.CouponInfoService;
import com.atguigu.ssyx.client.order.ProductFeignClient;
import com.atguigu.ssyx.enums.ActivityType;
import com.atguigu.ssyx.model.activity.ActivityInfo;
import com.atguigu.ssyx.model.activity.ActivityRule;
import com.atguigu.ssyx.model.activity.ActivitySku;
import com.atguigu.ssyx.model.activity.CouponInfo;
import com.atguigu.ssyx.model.order.CartInfo;
import com.atguigu.ssyx.model.product.SkuInfo;
import com.atguigu.ssyx.vo.activity.ActivityRuleVo;
import com.atguigu.ssyx.vo.order.CartInfoVo;
import com.atguigu.ssyx.vo.order.OrderConfirmVo;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.checkerframework.checker.units.qual.A;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class ActivityInfoServiceImpl extends ServiceImpl<ActivityInfoMapper, ActivityInfo> implements ActivityInfoService {
    @Autowired
    private ActivityRuleMapper activityRuleMapper;
    @Autowired
    private ActivityInfoMapper activityInfoMapper;
    @Autowired
    private ActivitySkuMapper activitySkuMapper;
    @Autowired
    private ProductFeignClient productFeignClient;
    @Autowired
    private CouponInfoService couponInfoService;
    @Autowired
    private ActivityInfoService activityInfoService;

    @A
    @Override
    public IPage<ActivityInfo> selectPageList(Page<ActivityInfo> pageParam) {
        QueryWrapper<ActivityInfo> queryWrapper = new QueryWrapper<>();
        queryWrapper.orderByAsc("create_time");
        IPage<ActivityInfo> activityInfoIPage = baseMapper.selectPage(pageParam, queryWrapper);
        List<ActivityInfo> records = activityInfoIPage.getRecords();
        for (ActivityInfo record : records) {
            record.setActivityTypeString(record.getActivityType().getComment());
        }
        return activityInfoIPage;
    }

    /**
     * 根据活动id查询活动规则
     *
     * @param id
     * @return
     */
    @Override
    public Map<String, Object> findActivityRuleList(Long id) {
        Map<String, Object> map = new HashMap<>();
        ActivityRuleVo activityRuleVo = new ActivityRuleVo();
        activityRuleVo.setActivityId(id);
        List<ActivityRule> activityRules = activityRuleMapper.selectList(new LambdaQueryWrapper<ActivityRule>().eq(ActivityRule::getActivityId, id));
        if (activityRules != null && activityRules.size() != 0) {
            map.put("activityRuleList", activityRules);
        } else {
            map.put("activityRuleList", null);
        }
        List<ActivitySku> activitySkus = activitySkuMapper.selectList(new LambdaQueryWrapper<ActivitySku>().eq(ActivitySku::getActivityId, id));
        List<Long> skuIds = activitySkus.stream().map(item -> {
            return item.getSkuId();
        }).collect(Collectors.toList());
        System.out.println(skuIds);
        if (skuIds != null || skuIds.size() != 0) {
            List<SkuInfo> skuInfoList = productFeignClient.findSkuListByIds(skuIds);
            map.put("skuInfoList", skuInfoList);
        } else {
            map.put("skuInfoList", null);
        }
        return map;
    }

    @Override
    public ActivityInfo getActivityById(Long id) {
        ActivityInfo activityInfo = baseMapper.selectById(id);
        activityInfo.setActivityTypeString(activityInfo.getActivityType().getComment());
        return activityInfo;
    }

    @Override
    public void saveActivityRule(ActivityRuleVo activityRuleVo) {
        Long activityId = activityRuleVo.getActivityId();
        //删除活动规则
        activityRuleMapper.delete(new LambdaQueryWrapper<ActivityRule>().eq(ActivityRule::getActivityId, activityId));
        //保存活动规则
        List<ActivityRule> activityRuleList = activityRuleVo.getActivityRuleList();
        for (ActivityRule activityRule : activityRuleList) {
            activityRule.setActivityId(activityId);
            activityRuleMapper.insert(activityRule);
        }
        //删除活动sku
        activitySkuMapper.delete(new LambdaQueryWrapper<ActivitySku>().eq(ActivitySku::getActivityId, activityId));
        //保存活动sku
        List<ActivitySku> activitySkuList = activityRuleVo.getActivitySkuList();
        for (ActivitySku activitySku : activitySkuList) {
            activitySku.setActivityId(activityId);
            activitySkuMapper.insert(activitySku);
        }
    }

    @Override
    public List<SkuInfo> findSkuInfoByKeyword(String keyword) {
        List<SkuInfo> skuInfoList = productFeignClient.getSkuInfoByKeyWord(keyword);
        List<Long> skuIdList = skuInfoList.stream().map(SkuInfo::getId).collect(Collectors.toList());
        System.out.println("skuIdList:" + skuIdList);
        if (skuIdList == null || skuIdList.size() == 0) {
            return skuInfoList;
        }
        List<SkuInfo> notExistSkuInfoList = new ArrayList<>();
        //已经存在的skuId，一个sku只能参加一个促销活动，所以存在的得排除
        List<Long> existSkuIdList = activityInfoMapper.selectSkuListExist(skuIdList);
        System.out.println("existSkuIdList:" + existSkuIdList);
        for (SkuInfo skuInfo : skuInfoList) {
            if (!existSkuIdList.contains(skuInfo.getId())) {
                notExistSkuInfoList.add(skuInfo);
            }
        }
        return notExistSkuInfoList;
    }

    @Override
    public Map<Long, List<String>> findActivity(List<Long> skuIds) {
        Map<Long, List<String>> activityMap = new HashMap<>();
        //根据skuId查询活动规则
        skuIds.forEach(skuId -> {
            List<ActivityRule> activityRuleList = baseMapper.selectActivityRuleList(skuId);
            //封装数据
            if (!CollectionUtils.isEmpty(activityRuleList)) {
                List<String> ruleList = new ArrayList<>();
                for (ActivityRule activityRule : activityRuleList) {
                    ruleList.add(this.getRuleDesc(activityRule));
                }
                activityMap.put(skuId, ruleList);
            }
        });
        return activityMap;
    }

    @Override
    public Map<String, Object> findCouponInfoBySkuIdAndUserId(Long skuId, Long userId) {
        Map<String, Object> couponInfo = new HashMap<>();
        //根据skuId获取sku营销活动
        List<ActivityRule> activityRuleList = activityInfoService.findActivityRuleBySkuId(skuId);
        //根据skuId和userId获取优惠券
        List<CouponInfo> coupons = couponInfoService.selectCouponBySkuIdAndUserId(skuId, userId);
        couponInfo.put("activityRuleList", activityRuleList);
        couponInfo.put("couponInfoList", coupons);
        return couponInfo;
    }

    @Override
    public List<ActivityRule> findActivityRuleBySkuId(Long skuId) {
        List<ActivityRule> activityRules = baseMapper.selectActivityRuleList(skuId);
        for (ActivityRule activityRule : activityRules) {
            activityRule.setRuleDesc(this.getRuleDesc(activityRule));
        }
        return activityRules;
    }

    /**
     * 封装购物车数据
     *
     * @param cartInfoList
     * @return
     */
    @Override
    public OrderConfirmVo findCartActivityAndCoupon(List<CartInfo> cartInfoList, Long userId) {
        OrderConfirmVo orderConfirmVo = new OrderConfirmVo();
        //首先要封装cartInfoList数据
        List<CartInfoVo> carInfoVoList = this.findCartActivityList(cartInfoList);
        //计算优惠后的价格
        BigDecimal activityReduceAmount = carInfoVoList.stream()
                .filter(cartInfoVo -> cartInfoVo.getActivityRule() != null)
                .map(cartInfoVo -> cartInfoVo.getActivityRule().getReduceAmount())
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        //查找优惠劵列表
        List<CouponInfo> couponInfoList = couponInfoService.findCouponListByUserId(cartInfoList, userId);
        BigDecimal couponReduceAmount = BigDecimal.ZERO;
        //计算减去优惠劵后的价格
        if (!CollectionUtils.isEmpty(couponInfoList)) {
            couponReduceAmount = couponInfoList.stream()
                    .filter(couponInfo -> couponInfo.getIsOptimal().intValue() == 1)
                    .map(couponInfo -> couponInfo.getAmount())
                    .reduce(BigDecimal.ZERO, BigDecimal::add);
        }
        //计算总金额
        BigDecimal originalTotalAmount = cartInfoList.stream()
                .filter(cartInfo -> cartInfo.getIsChecked() == 1)
                .map(cartInfo -> cartInfo.getCartPrice().multiply(new BigDecimal(cartInfo.getSkuNum())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);


        //计算去掉优惠后的真正的金额
        BigDecimal finalTotalAmount = originalTotalAmount.subtract(activityReduceAmount).subtract(couponReduceAmount);
        orderConfirmVo.setCarInfoVoList(carInfoVoList);
        orderConfirmVo.setActivityReduceAmount(activityReduceAmount);
        orderConfirmVo.setCouponInfoList(couponInfoList);
        orderConfirmVo.setCouponReduceAmount(couponReduceAmount);
        orderConfirmVo.setOriginalTotalAmount(originalTotalAmount);
        orderConfirmVo.setTotalAmount(finalTotalAmount);
        return orderConfirmVo;
    }
    @Override
    public List<CartInfoVo> findCartActivityList(List<CartInfo> cartInfoList) {
        List<CartInfoVo> cartInfoVoList = new ArrayList<>();
        List<Long> skuIdList = cartInfoList.stream().map(cartInfo -> {
            return cartInfo.getSkuId();
        }).collect(Collectors.toList());
        //第一步：获取skuId列表对应的全部ActivitySku
        List<ActivitySku> activitySkuList = baseMapper.selectCartActivityList(skuIdList);
        //根据活动分组
        /*
            数据格式：
            {
                ActivityId->1:属于本活动的skuId->[1,2,3],
                ActivityId->2:属于本活动的skuId->[4,5,6]
            }
         */
        Map<Long, Set<Long>> groupByActivityIdMap = activitySkuList.stream()
                .collect(Collectors.groupingBy(ActivitySku::getActivityId,
                        Collectors.mapping(ActivitySku::getSkuId, Collectors.toSet())));
        //获取活动中的规则
        //Map<Long, List<ActivityRule>>
        //activityId找出来
        Set<Long> activityIdSet = activitySkuList.stream().map(ActivitySku::getActivityId).collect(Collectors.toSet());
        Map<Long, List<ActivityRule>> activityIdToActivityRuleListMap = new HashMap<>();
        //第二步：每个活动id对应的多个规则
        if (!CollectionUtils.isEmpty(activityIdSet)) {
            //使用activitySetId查询规则表
            LambdaQueryWrapper<ActivityRule> queryWrapper = new LambdaQueryWrapper<ActivityRule>();
            queryWrapper.orderByDesc(ActivityRule::getBenefitAmount, ActivityRule::getConditionNum);
            //当前活动id是否在活动规则表中的查询
            queryWrapper.in(ActivityRule::getActivityId, activityIdSet);
            List<ActivityRule> activityRuleList = activityRuleMapper.selectList(queryWrapper);
            //按活动Id分组，获取活动对应的规则
            // Map<Long, List<ActivityRule>> collect = activityRuleList.stream().collect(Collectors.groupingBy(ActivityRule::getActivityId));
            activityIdToActivityRuleListMap = activityRuleList.stream().collect(Collectors.groupingBy(ActivityRule::getActivityId));
        }
        //第三步：根据活动汇总购物项，相同活动的购物项为一组显示在页面，并且计算最优优惠金额
        Set<Long> activitySkuIdSet = new HashSet<>();
        //从activityIdToActivityRuleListMap找规则进行计算
        if (!CollectionUtils.isEmpty(groupByActivityIdMap)) {
            Iterator<Map.Entry<Long, Set<Long>>> iterator = groupByActivityIdMap.entrySet().iterator();
            /*
            数据格式：
            {
                ActivityId->1:属于本活动的skuId->[1,2,3],
                ActivityId->2:属于本活动的skuId->[4,5,6]
            }
         */
            while (iterator.hasNext()) {
                Map.Entry<Long, Set<Long>> entry = iterator.next();
                Long activityId = entry.getKey();
                Set<Long> currentActivitySkuId = entry.getValue();
                //当前活动对应的购物项列表
                List<CartInfo> currentActivityCartInfoList = cartInfoList.stream().filter(cartInfo -> currentActivitySkuId.contains(cartInfo.getSkuId())).collect(Collectors.toList());
                //当前活动的总金额
                BigDecimal activityTotalAmount = this.computeTotalAmount(currentActivityCartInfoList);
                //当前活动的购物项总个数
                Integer activityTotalNum = this.computeCartNum(currentActivityCartInfoList);
                //计算当前活动对应的最优规则
                //活动当前活动对应的规则
                List<ActivityRule> currentActivityRuleList = activityIdToActivityRuleListMap.get(activityId);
                ActivityType activityType = currentActivityRuleList.get(0).getActivityType();
                ActivityRule optimalActivityRule;
                if (ActivityType.FULL_REDUCTION == activityType) {
                    //满减
                    optimalActivityRule = this.computeFullReduction(activityTotalAmount, currentActivityRuleList);
                    //封装数据
                } else {
                    //满量打折
                    optimalActivityRule = this.computeFullDiscount(activityTotalNum, activityTotalAmount, currentActivityRuleList);
                    //封装数据
                }
                CartInfoVo cartInfoVo = new CartInfoVo();
                cartInfoVo.setCartInfoList(currentActivityCartInfoList);
                cartInfoVo.setActivityRule(optimalActivityRule);
                cartInfoVoList.add(cartInfoVo);
                activitySkuIdSet.addAll(currentActivitySkuId);
            }
        }
        //第四步：无活动的购物项，每一项一组
        skuIdList.remove(activitySkuIdSet);
        if (!CollectionUtils.isEmpty(skuIdList)) {
            Map<Long, CartInfo> skuIdToCartInfoMap = cartInfoList.stream().collect(Collectors.toMap(CartInfo::getSkuId, cartInfo -> cartInfo));
            for (Long skuId : skuIdList) {
                CartInfoVo cartInfoVo = new CartInfoVo();
                cartInfoVo.setActivityRule(null);
                List<CartInfo> currentCartInfoList = new ArrayList<>();
                currentCartInfoList.add(skuIdToCartInfoMap.get(skuId));
                cartInfoVo.setCartInfoList(currentCartInfoList);
                cartInfoVoList.add(cartInfoVo);
            }
        }
        return cartInfoVoList;
    }

    //构造规则名称的方法
    private String getRuleDesc(ActivityRule activityRule) {
        ActivityType activityType;
        activityType = activityRule.getActivityType();
        StringBuffer ruleDesc = new StringBuffer();
        if (activityType == ActivityType.FULL_REDUCTION) {
            ruleDesc
                    .append("满")
                    .append(activityRule.getConditionAmount())
                    .append("元减")
                    .append(activityRule.getBenefitAmount())
                    .append("元");
        } else {
            ruleDesc
                    .append("满")
                    .append(activityRule.getConditionNum())
                    .append("元打")
                    .append(activityRule.getBenefitDiscount())
                    .append("折");
        }
        return ruleDesc.toString();
    }

    /**
     * 计算满量打折最优规则
     *
     * @param totalNum
     * @param activityRuleList //该活动规则skuActivityRuleList数据，已经按照优惠折扣从大到小排序了
     */
    private ActivityRule computeFullDiscount(Integer totalNum, BigDecimal totalAmount, List<ActivityRule> activityRuleList) {
        ActivityRule optimalActivityRule = null;
        //该活动规则skuActivityRuleList数据，已经按照优惠金额从大到小排序了
        for (ActivityRule activityRule : activityRuleList) {
            //如果订单项购买个数大于等于满减件数，则优化打折
            if (totalNum.intValue() >= activityRule.getConditionNum()) {
                BigDecimal skuDiscountTotalAmount = totalAmount.multiply(activityRule.getBenefitDiscount().divide(new BigDecimal("10")));
                BigDecimal reduceAmount = totalAmount.subtract(skuDiscountTotalAmount);
                activityRule.setReduceAmount(reduceAmount);
                optimalActivityRule = activityRule;
                break;
            }
        }
        if (null == optimalActivityRule) {
            //如果没有满足条件的取最小满足条件的一项
            optimalActivityRule = activityRuleList.get(activityRuleList.size() - 1);
            optimalActivityRule.setReduceAmount(new BigDecimal("0"));
            optimalActivityRule.setSelectType(1);

            StringBuffer ruleDesc = new StringBuffer()
                    .append("满")
                    .append(optimalActivityRule.getConditionNum())
                    .append("元打")
                    .append(optimalActivityRule.getBenefitDiscount())
                    .append("折，还差")
                    .append(totalNum - optimalActivityRule.getConditionNum())
                    .append("件");
            optimalActivityRule.setRuleDesc(ruleDesc.toString());
        } else {
            StringBuffer ruleDesc = new StringBuffer()
                    .append("满")
                    .append(optimalActivityRule.getConditionNum())
                    .append("元打")
                    .append(optimalActivityRule.getBenefitDiscount())
                    .append("折，已减")
                    .append(optimalActivityRule.getReduceAmount())
                    .append("元");
            optimalActivityRule.setRuleDesc(ruleDesc.toString());
            optimalActivityRule.setSelectType(2);
        }
        return optimalActivityRule;
    }

    /**
     * 计算满减最优规则
     *
     * @param totalAmount      //当前活动的总金额
     * @param activityRuleList //该活动规则skuActivityRuleList数据，已经按照优惠金额从大到小排序了
     */
    private ActivityRule computeFullReduction(BigDecimal totalAmount, List<ActivityRule> activityRuleList) {
        ActivityRule optimalActivityRule = null;
        //该活动规则skuActivityRuleList数据，已经按照优惠金额从大到小排序了
        for (ActivityRule activityRule : activityRuleList) {
            //如果订单项金额大于等于满减金额，则优惠金额
            if (totalAmount.compareTo(activityRule.getConditionAmount()) > -1) {
                //优惠后减少金额
                activityRule.setReduceAmount(activityRule.getBenefitAmount());
                optimalActivityRule = activityRule;
                break;
            }
        }
        if (null == optimalActivityRule) {
            //如果没有满足条件的取最小满足条件的一项
            optimalActivityRule = activityRuleList.get(activityRuleList.size() - 1);
            optimalActivityRule.setReduceAmount(new BigDecimal("0"));
            optimalActivityRule.setSelectType(1);

            StringBuffer ruleDesc = new StringBuffer()
                    .append("满")
                    .append(optimalActivityRule.getConditionAmount())
                    .append("元减")
                    .append(optimalActivityRule.getBenefitAmount())
                    .append("元，还差")
                    .append(totalAmount.subtract(optimalActivityRule.getConditionAmount()))
                    .append("元");
            optimalActivityRule.setRuleDesc(ruleDesc.toString());
        } else {
            StringBuffer ruleDesc = new StringBuffer()
                    .append("满")
                    .append(optimalActivityRule.getConditionAmount())
                    .append("元减")
                    .append(optimalActivityRule.getBenefitAmount())
                    .append("元，已减")
                    .append(optimalActivityRule.getReduceAmount())
                    .append("元");
            optimalActivityRule.setRuleDesc(ruleDesc.toString());
            optimalActivityRule.setSelectType(2);
        }
        return optimalActivityRule;
    }

    private BigDecimal computeTotalAmount(List<CartInfo> cartInfoList) {
        BigDecimal total = new BigDecimal("0");
        for (CartInfo cartInfo : cartInfoList) {
            //是否选中
            if (cartInfo.getIsChecked().intValue() == 1) {
                BigDecimal itemTotal = cartInfo.getCartPrice().multiply(new BigDecimal(cartInfo.getSkuNum()));
                total = total.add(itemTotal);
            }
        }
        return total;
    }

    private int computeCartNum(List<CartInfo> cartInfoList) {
        int total = 0;
        for (CartInfo cartInfo : cartInfoList) {
            //是否选中
            if (cartInfo.getIsChecked().intValue() == 1) {
                total += cartInfo.getSkuNum();
            }
        }
        return total;
    }

}
