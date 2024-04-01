package com.atguigu.ssyx.order.service;

import com.atguigu.ssyx.model.order.OrderInfo;
import com.atguigu.ssyx.vo.order.OrderConfirmVo;
import com.atguigu.ssyx.vo.order.OrderSubmitVo;
import com.baomidou.mybatisplus.extension.service.IService;

public interface OrderService extends IService<OrderInfo> {
    OrderConfirmVo confirmOrder();

    Long submitOrder(OrderSubmitVo orderSubmitVo);

    OrderInfo getOrderInfo(Long orderId);

    OrderInfo getOrderInfoByOrderNo(String orderNo);
}
