package com.atguigu.ssyx.payment.service.Impl;

import com.atguigu.ssyx.model.order.PaymentInfo;
import com.atguigu.ssyx.payment.service.PaymentInfoService;
import com.atguigu.ssyx.payment.service.WeiXinService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
public class WeiXinServiceImpl implements WeiXinService {
    @Autowired
    private PaymentInfoService paymentInfoService;

    @Override
    public Map<String, Object> createJsapi(String orderNo) {
        PaymentInfo paymentInfo = paymentInfoService.getPaymentInfo(orderNo);
        if (paymentInfo == null) {
            paymentInfo = paymentInfoService.savePaymentInfo(orderNo);
        }
        return null;
    }


}
