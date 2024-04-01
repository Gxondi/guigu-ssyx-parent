package com.atguigu.ssyx.payment.service;

import java.util.Map;

public interface WeiXinService {
    Map<String, Object> createJsapi(String orderNo);
}
