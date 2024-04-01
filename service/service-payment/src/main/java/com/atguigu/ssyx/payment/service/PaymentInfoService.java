package com.atguigu.ssyx.payment.service;

import com.atguigu.ssyx.model.order.PaymentInfo;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.Map;

public interface PaymentInfoService extends IService<PaymentInfo> {
    PaymentInfo getPaymentInfo(String orderNo);

    PaymentInfo savePaymentInfo(String orderNo);
}
