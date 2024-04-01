package com.atguigu.ssyx.payment.service.Impl;


import com.atguigu.ssyx.client.order.OrderFeignClient;
import com.atguigu.ssyx.enums.PaymentStatus;
import com.atguigu.ssyx.enums.PaymentType;
import com.atguigu.ssyx.model.order.OrderInfo;
import com.atguigu.ssyx.model.order.PaymentInfo;
import com.atguigu.ssyx.payment.mapper.PaymentInfoMapper;
import com.atguigu.ssyx.payment.service.PaymentInfoService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.checkerframework.checker.units.qual.A;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Date;
import java.util.Map;

@Service
public class PaymentInfoServiceImpl extends ServiceImpl<PaymentInfoMapper, PaymentInfo> implements PaymentInfoService {
    @Autowired
    private OrderFeignClient orderFeignClient;
    @Override
    public PaymentInfo getPaymentInfo(String orderNo) {
        PaymentInfo paymentInfo = baseMapper.selectOne(new LambdaQueryWrapper<PaymentInfo>().eq(PaymentInfo::getOrderNo, orderNo));
        return paymentInfo;
    }

    @Override
    public PaymentInfo savePaymentInfo(String orderNo) {
        OrderInfo orderInfo = orderFeignClient.getOrderInfo(orderNo);
        PaymentInfo paymentInfo = new PaymentInfo();
        paymentInfo.setCreateTime(new Date());
        paymentInfo.setOrderId(orderInfo.getId());
        paymentInfo.setPaymentType(PaymentType.WEIXIN);
        paymentInfo.setUserId(orderInfo.getUserId());
        paymentInfo.setOrderNo(orderInfo.getOrderNo());
        paymentInfo.setPaymentStatus(PaymentStatus.UNPAID);
        String subject = "test";
        paymentInfo.setSubject(subject);
        //paymentInfo.setTotalAmount(order.getTotalAmount());
        paymentInfo.setTotalAmount(new BigDecimal("0.01"));
        baseMapper.insert(paymentInfo);
        return paymentInfo;
    }
}
