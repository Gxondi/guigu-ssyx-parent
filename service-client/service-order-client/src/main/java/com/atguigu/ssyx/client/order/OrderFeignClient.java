package com.atguigu.ssyx.client.order;
import com.atguigu.ssyx.model.order.OrderInfo;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;
import java.util.Map;

@FeignClient(value = "service-order")//指定调用的服务
public interface OrderFeignClient {

    @GetMapping("api/order/inner/getOrderInfoById/{orderNo}")
    OrderInfo getOrderInfo(@PathVariable("orderNo") String orderNo);
}