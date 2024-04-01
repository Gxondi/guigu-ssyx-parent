package com.atguigu.ssyx.order.service.Impl;

import com.atguigu.ssyx.client.cart.CartFeignClient;
import com.atguigu.ssyx.client.order.ActivityFeignClient;
import com.atguigu.ssyx.client.order.ProductFeignClient;
import com.atguigu.ssyx.client.user.UserFeignClient;
import com.atguigu.ssyx.common.auth.AuthContextHolder;
import com.atguigu.ssyx.common.constant.MqConst;
import com.atguigu.ssyx.common.constant.RedisConst;
import com.atguigu.ssyx.common.exception.SsyxException;
import com.atguigu.ssyx.common.result.ResultCodeEnum;
import com.atguigu.ssyx.common.service.RabbitService;
import com.atguigu.ssyx.common.utils.DateUtil;
import com.atguigu.ssyx.enums.*;
import com.atguigu.ssyx.model.activity.ActivityRule;
import com.atguigu.ssyx.model.activity.CouponInfo;
import com.atguigu.ssyx.model.order.CartInfo;
import com.atguigu.ssyx.model.order.OrderInfo;
import com.atguigu.ssyx.model.order.OrderItem;
import com.atguigu.ssyx.order.mapper.OrderItemMapper;
import com.atguigu.ssyx.order.mapper.OrderMapper;
import com.atguigu.ssyx.order.service.OrderService;
import com.atguigu.ssyx.vo.order.CartInfoVo;
import com.atguigu.ssyx.vo.order.OrderConfirmVo;
import com.atguigu.ssyx.vo.order.OrderSubmitVo;
import com.atguigu.ssyx.vo.product.SkuStockLockVo;
import com.atguigu.ssyx.vo.user.LeaderAddressVo;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.BoundHashOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Service
public class OrderServiceImpl extends ServiceImpl<OrderMapper, OrderInfo> implements OrderService {
    @Autowired
    private ActivityFeignClient activityFeignClient;
    @Autowired
    private UserFeignClient userFeignClient;
    @Autowired
    private CartFeignClient cartFeignClient;
    @Autowired
    private RedisTemplate redisTemplate;
    @Autowired
    private RabbitService rabbitService;
    @Autowired
    private OrderItemMapper orderItemMapper;
    @Autowired
    private ProductFeignClient productFeignClient;

    @Override
    public OrderConfirmVo confirmOrder() {
        Long userId = AuthContextHolder.getUserId();
        LeaderAddressVo leaderAddressVo = userFeignClient.findUserById(userId);
        List<CartInfo> cartCheckedList = cartFeignClient.getCartCheckedList(userId);
        OrderConfirmVo orderConfirmVo = activityFeignClient.findCartActivityAndCoupon(userId, cartCheckedList);
        orderConfirmVo.setLeaderAddressVo(leaderAddressVo);
        String orderNo = System.currentTimeMillis() + "";
        orderConfirmVo.setOrderNo(orderNo);
        redisTemplate.opsForValue().set(RedisConst.ORDER_REPEAT + orderNo, orderNo, 24, TimeUnit.HOURS);
        return orderConfirmVo;
    }

    @Override
    public Long submitOrder(OrderSubmitVo orderSubmitVo) {
        //设置userId
        Long userId = AuthContextHolder.getUserId();
        orderSubmitVo.setUserId(userId);
        //防止订单重复提交
        String orderNo = orderSubmitVo.getOrderNo();
        String script = "if(redis.call('get', KEYS[1]) == ARGV[1]) then return redis.call('del', KEYS[1]) else return 0 end";
        Boolean execute = (Boolean) redisTemplate.execute(new DefaultRedisScript(script, Boolean.class), Arrays.asList(RedisConst.ORDER_REPEAT + orderNo), orderNo);
        if (!execute) {
            throw new SsyxException(ResultCodeEnum.REPEAT_SUBMIT);
        }
        //验证库存锁定库存
        List<CartInfo> cartCheckedList = cartFeignClient.getCartCheckedList(userId);
        //普通商品
        List<CartInfo> commonCartList = cartCheckedList.stream().filter(cartInfo -> cartInfo.getSkuType() == SkuType.COMMON.getCode()
        ).collect(Collectors.toList());
        System.out.println("commonCartList" + commonCartList);
        //要进行锁库存的商品
        List<SkuStockLockVo> commonSkuStockLockVos = null;
        if (!CollectionUtils.isEmpty(commonCartList)) {
            commonSkuStockLockVos = commonCartList.stream().map(cartInfo -> {
                SkuStockLockVo skuStockLockVo = new SkuStockLockVo();
                skuStockLockVo.setSkuId(cartInfo.getSkuId());
                skuStockLockVo.setSkuNum(cartInfo.getSkuNum());
                return skuStockLockVo;
            }).collect(Collectors.toList());

        }
        System.out.println("commonSkuStockLockVos" + commonSkuStockLockVos);
        //分布式锁进行库存检查以及锁定库存
        //远程调用product进行库存检查锁定
        System.out.println("orderNo" + orderNo);
        Boolean lock = productFeignClient.checkAndLock(commonSkuStockLockVos, orderNo);
        System.out.println("lock" + lock);
        if (!lock) {
            throw new SsyxException(ResultCodeEnum.ORDER_STOCK_FALL);
        }
        //下单过程
        //向两张表添加数据 orderInfo orderItem
        Long orderId = this.saveOrder(orderSubmitVo, cartCheckedList);
        //删除购物车选中的商品
        rabbitService.sendMessage(MqConst.QUEUE_DELETE_CART, MqConst.ROUTING_DELETE_CART, orderSubmitVo.getUserId());
        return orderId;
    }

    @Override
    public OrderInfo getOrderInfo(Long orderId) {
        OrderInfo orderInfo = this.baseMapper.selectById(orderId);
        LambdaQueryWrapper<OrderItem> queryWrapper = new LambdaQueryWrapper();
        queryWrapper.eq(OrderItem::getOrderId, orderId);
        List<OrderItem> orderItems = orderItemMapper.selectList(queryWrapper);
        orderInfo.setOrderItemList(orderItems);
        return orderInfo;
    }

    @Override
    public OrderInfo getOrderInfoByOrderNo(String orderNo) {
        return this.baseMapper.selectOne(new LambdaQueryWrapper<OrderInfo>().eq(OrderInfo::getOrderNo, orderNo));
    }

    @Transactional
    public Long saveOrder(OrderSubmitVo orderSubmitVo, List<CartInfo> cartCheckedList) {
        if (CollectionUtils.isEmpty(cartCheckedList)) {
            throw new SsyxException(ResultCodeEnum.DATA_ERROR);
        }
        Long userId = AuthContextHolder.getUserId();
        LeaderAddressVo leaderAddressVo = userFeignClient.findUserById(userId);
        if (leaderAddressVo == null) {
            throw new SsyxException(ResultCodeEnum.DATA_ERROR);
        }
        //计算金额
        //营销活动，优惠劵价格
        Map<String, BigDecimal> activitySplitAmount = this.computeActivitySplitAmount(cartCheckedList);
        Map<String, BigDecimal> couponInfoSplitAmount = this.computeCouponInfoSplitAmount(cartCheckedList, orderSubmitVo.getCouponId());
        List<OrderItem> orderItemList = new ArrayList<>();
        //订单总金额
        for (CartInfo cartInfo : cartCheckedList) {
            OrderItem orderItem = new OrderItem();
            orderItem.setId(null);
            orderItem.setCategoryId(cartInfo.getCategoryId());
            if (cartInfo.getSkuType() == SkuType.COMMON.getCode()) {
                orderItem.setSkuType(SkuType.COMMON);
            } else {
                orderItem.setSkuType(SkuType.SECKILL);
            }
            orderItem.setSkuId(cartInfo.getSkuId());
            orderItem.setSkuName(cartInfo.getSkuName());
            orderItem.setSkuPrice(cartInfo.getCartPrice());
            orderItem.setImgUrl(cartInfo.getImgUrl());
            orderItem.setSkuNum(cartInfo.getSkuNum());
            orderItem.setLeaderId(orderSubmitVo.getLeaderId());
            //营销活动
            BigDecimal activityReduceAmount = activitySplitAmount.get("activity:" + cartInfo.getSkuId());
            if (activityReduceAmount == null) {
                activityReduceAmount = new BigDecimal(0);
            }
            orderItem.setSplitActivityAmount(activityReduceAmount);
            //优惠劵价格
            BigDecimal couponReduceAmount = couponInfoSplitAmount.get("coupon:" + cartInfo.getSkuId());
            if (couponReduceAmount == null) {
                couponReduceAmount = new BigDecimal(0);
            }
            orderItem.setSplitCouponAmount(couponReduceAmount);
            //总金额
            BigDecimal totalAmount = cartInfo.getCartPrice().multiply(new BigDecimal(cartInfo.getSkuNum()));
            //优惠后金额
            BigDecimal finalAmount = totalAmount.subtract(activityReduceAmount).subtract(couponReduceAmount);
            orderItemList.add(orderItem);
        }
        //封装订单信息
        OrderInfo order = new OrderInfo();
        order.setUserId(userId);
//		private String nickName;
        order.setOrderNo(orderSubmitVo.getOrderNo());
        order.setOrderStatus(OrderStatus.UNPAID);
        order.setProcessStatus(ProcessStatus.UNPAID);
        order.setCouponId(orderSubmitVo.getCouponId());
        order.setLeaderId(orderSubmitVo.getLeaderId());
        order.setLeaderName(leaderAddressVo.getLeaderName());
        order.setLeaderPhone(leaderAddressVo.getLeaderPhone());
        order.setTakeName(leaderAddressVo.getTakeName());
        order.setReceiverName(orderSubmitVo.getReceiverName());
        order.setReceiverPhone(orderSubmitVo.getReceiverPhone());
        order.setReceiverProvince(leaderAddressVo.getProvince());
        order.setReceiverCity(leaderAddressVo.getCity());
        order.setReceiverDistrict(leaderAddressVo.getDistrict());
        order.setReceiverAddress(leaderAddressVo.getDetailAddress());
        order.setWareId(cartCheckedList.get(0).getWareId());

        //计算订单金额
        BigDecimal originalTotalAmount = this.computeTotalAmount(cartCheckedList);
        BigDecimal activityAmount = activitySplitAmount.get("activity:total");
        if (null == activityAmount) activityAmount = new BigDecimal(0);
        BigDecimal couponAmount = couponInfoSplitAmount.get("coupon:total");
        if (null == couponAmount) couponAmount = new BigDecimal(0);
        BigDecimal totalAmount = originalTotalAmount.subtract(activityAmount).subtract(couponAmount);
        //计算订单金额
        order.setOriginalTotalAmount(originalTotalAmount);
        order.setActivityAmount(activityAmount);
        order.setCouponAmount(couponAmount);
        order.setTotalAmount(totalAmount);
        baseMapper.insert(order);
        //保存订单项
        orderItemList.forEach(orderItem -> {
            orderItem.setOrderId(order.getId());
            orderItemMapper.insert(orderItem);
        });
        //如果当前订单使用了优惠券，更新优惠券使用状态
        if (order.getCouponId() != null) {
            activityFeignClient.updateCouponInfoStatus(userId, order.getCouponId(), order.getId());
        }
        //下单成功后，记录用户购物商品数量 redis
        String orderSkuKey = RedisConst.ORDER_SKU_MAP + orderSubmitVo.getUserId();
        BoundHashOperations<String, String, Integer> hashOperations = redisTemplate.boundHashOps(orderSkuKey);
        cartCheckedList.forEach(cartInfo -> {
            if (hashOperations.hasKey(cartInfo.getSkuId().toString())) {
                Integer orderSkuNum = hashOperations.get(cartInfo.getSkuId().toString()) + cartInfo.getSkuNum();
                hashOperations.put(cartInfo.getSkuId().toString(), orderSkuNum);
            }
        });
        redisTemplate.expire(orderSkuKey, DateUtil.getCurrentExpireTimes(), TimeUnit.SECONDS);
        //发送消息
        return order.getId();
    }

    /**
     * 计算订单总金额
     *
     * @param cartInfoList
     * @return
     */
    private BigDecimal computeTotalAmount(List<CartInfo> cartInfoList) {
        BigDecimal total = new BigDecimal(0);
        for (CartInfo cartInfo : cartInfoList) {
            BigDecimal itemTotal = cartInfo.getCartPrice().multiply(new BigDecimal(cartInfo.getSkuNum()));
            total = total.add(itemTotal);
        }
        return total;
    }

    /**
     * 计算购物项分摊的优惠减少金额
     * 打折：按折扣分担
     * 现金：按比例分摊
     *
     * @param cartInfoParamList
     * @return
     */
    private Map<String, BigDecimal> computeActivitySplitAmount(List<CartInfo> cartInfoParamList) {
        Map<String, BigDecimal> activitySplitAmountMap = new HashMap<>();

        //促销活动相关信息
        List<CartInfoVo> cartInfoVoList = activityFeignClient.findCartActivityList(cartInfoParamList);
        //活动总金额
        BigDecimal activityReduceAmount = new BigDecimal(0);
        if (!CollectionUtils.isEmpty(cartInfoVoList)) {
            for (CartInfoVo cartInfoVo : cartInfoVoList) {
                ActivityRule activityRule = cartInfoVo.getActivityRule();
                //购物项
                List<CartInfo> cartInfoList = cartInfoVo.getCartInfoList();
                if (null != activityRule) {
                    //优惠金额， 按比例分摊 优惠后减少金额
                    BigDecimal reduceAmount = activityRule.getReduceAmount();
                    //优惠后减少金额
                    activityReduceAmount = activityReduceAmount.add(reduceAmount);
                    //只有一个购物项
                    if (cartInfoList.size() == 1) {
                        activitySplitAmountMap.put("activity:" + cartInfoList.get(0).getSkuId(), reduceAmount);
                    } else {
                        //多个购物项
                        //计算总金额
                        BigDecimal originalTotalAmount = new BigDecimal(0);
                        for (CartInfo cartInfo : cartInfoList) {
                            BigDecimal skuTotalAmount = cartInfo.getCartPrice().multiply(new BigDecimal(cartInfo.getSkuNum()));
                            originalTotalAmount = originalTotalAmount.add(skuTotalAmount);
                        }
                        //记录除最后一项是所有分摊金额， 最后一项=总的 - skuPartReduceAmount
                        BigDecimal skuPartReduceAmount = new BigDecimal(0);
                        //活动类型（满减、折扣）
                        if (activityRule.getActivityType() == ActivityType.FULL_REDUCTION) {
                            for (int i = 0, len = cartInfoList.size(); i < len; i++) {
                                CartInfo cartInfo = cartInfoList.get(i);
                                if (i < len - 1) {
                                    //满减活动下，每个购物项的总金额
                                    BigDecimal skuTotalAmount = cartInfo.getCartPrice().multiply(new BigDecimal(cartInfo.getSkuNum()));
                                    //当前sku在总金额中的占比
                                    BigDecimal skuReduceAmount = skuTotalAmount.divide(originalTotalAmount, 2, RoundingMode.HALF_UP).multiply(reduceAmount);
                                    activitySplitAmountMap.put("activity:" + cartInfo.getSkuId(), skuReduceAmount);
                                    skuPartReduceAmount = skuPartReduceAmount.add(skuReduceAmount);
                                } else {
                                    BigDecimal skuReduceAmount = reduceAmount.subtract(skuPartReduceAmount);
                                    activitySplitAmountMap.put("activity:" + cartInfo.getSkuId(), skuReduceAmount);
                                }
                            }
                        } else {
                            //折扣
                            for (int i = 0, len = cartInfoList.size(); i < len; i++) {
                                CartInfo cartInfo = cartInfoList.get(i);
                                if (i < len - 1) {
                                    BigDecimal skuTotalAmount = cartInfo.getCartPrice().multiply(new BigDecimal(cartInfo.getSkuNum()));
                                    //sku分摊金额
                                    BigDecimal skuDiscountTotalAmount = skuTotalAmount.multiply(activityRule.getBenefitDiscount().divide(new BigDecimal("10")));
                                    BigDecimal skuReduceAmount = skuTotalAmount.subtract(skuDiscountTotalAmount);
                                    activitySplitAmountMap.put("activity:" + cartInfo.getSkuId(), skuReduceAmount);

                                    skuPartReduceAmount = skuPartReduceAmount.add(skuReduceAmount);
                                } else {
                                    BigDecimal skuReduceAmount = reduceAmount.subtract(skuPartReduceAmount);
                                    activitySplitAmountMap.put("activity:" + cartInfo.getSkuId(), skuReduceAmount);
                                }
                            }
                        }
                    }
                }
            }
        }
        activitySplitAmountMap.put("activity:total", activityReduceAmount);
        return activitySplitAmountMap;
    }

    private Map<String, BigDecimal> computeCouponInfoSplitAmount(List<CartInfo> cartInfoList, Long couponId) {
        Map<String, BigDecimal> couponInfoSplitAmountMap = new HashMap<>();

        if (null == couponId) return couponInfoSplitAmountMap;
        CouponInfo couponInfo = activityFeignClient.findRangeSkuIdList(cartInfoList, couponId);

        if (null != couponInfo) {
            //sku对应的订单明细
            Map<Long, CartInfo> skuIdToCartInfoMap = new HashMap<>();
            for (CartInfo cartInfo : cartInfoList) {
                skuIdToCartInfoMap.put(cartInfo.getSkuId(), cartInfo);
            }
            //优惠券对应的skuId列表
            List<Long> skuIdList = couponInfo.getSkuIdList();
            if (CollectionUtils.isEmpty(skuIdList)) {
                return couponInfoSplitAmountMap;
            }
            //优惠券优化总金额
            BigDecimal reduceAmount = couponInfo.getAmount();
            if (skuIdList.size() == 1) {
                //sku的优化金额
                couponInfoSplitAmountMap.put("coupon:" + skuIdToCartInfoMap.get(skuIdList.get(0)).getSkuId(), reduceAmount);
            } else {
                //总金额
                BigDecimal originalTotalAmount = new BigDecimal(0);
                for (Long skuId : skuIdList) {
                    CartInfo cartInfo = skuIdToCartInfoMap.get(skuId);
                    BigDecimal skuTotalAmount = cartInfo.getCartPrice().multiply(new BigDecimal(cartInfo.getSkuNum()));
                    originalTotalAmount = originalTotalAmount.add(skuTotalAmount);
                }
                //记录除最后一项是所有分摊金额， 最后一项=总的 - skuPartReduceAmount
                BigDecimal skuPartReduceAmount = new BigDecimal(0);
                if (couponInfo.getCouponType() == CouponType.CASH || couponInfo.getCouponType() == CouponType.FULL_REDUCTION) {
                    for (int i = 0, len = skuIdList.size(); i < len; i++) {
                        CartInfo cartInfo = skuIdToCartInfoMap.get(skuIdList.get(i));
                        if (i < len - 1) {
                            BigDecimal skuTotalAmount = cartInfo.getCartPrice().multiply(new BigDecimal(cartInfo.getSkuNum()));
                            //sku分摊金额
                            BigDecimal skuReduceAmount = skuTotalAmount.divide(originalTotalAmount, 2, RoundingMode.HALF_UP).multiply(reduceAmount);
                            couponInfoSplitAmountMap.put("coupon:" + cartInfo.getSkuId(), skuReduceAmount);

                            skuPartReduceAmount = skuPartReduceAmount.add(skuReduceAmount);
                        } else {
                            BigDecimal skuReduceAmount = reduceAmount.subtract(skuPartReduceAmount);
                            couponInfoSplitAmountMap.put("coupon:" + cartInfo.getSkuId(), skuReduceAmount);
                        }
                    }
                }
            }
            couponInfoSplitAmountMap.put("coupon:total", couponInfo.getAmount());
        }
        return couponInfoSplitAmountMap;
    }
}
