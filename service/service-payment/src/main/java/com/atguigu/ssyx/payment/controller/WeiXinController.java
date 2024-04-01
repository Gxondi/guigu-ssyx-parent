package com.atguigu.ssyx.payment.controller;

import com.atguigu.ssyx.common.result.Result;
import com.atguigu.ssyx.payment.service.PaymentInfoService;
import com.atguigu.ssyx.payment.service.WeiXinService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api/payment/weixin")
public class WeiXinController {
    @Autowired
    private WeiXinService weiXinService;
    @Autowired
    private PaymentInfoService paymentInfoService;
    @RequestMapping("/createJsapi/{orderNo}")
    public Result createJsapi(@PathVariable String orderNo){
        Map<String,Object> map = weiXinService.createJsapi(orderNo);
        return Result.ok(map);
    }
}
