package com.atguigu.ssyx.order.service.Impl;

import com.atguigu.ssyx.model.order.OrderItem;
import com.atguigu.ssyx.order.mapper.OrderItemMapper;
import com.atguigu.ssyx.order.service.OrderItemService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

@Service
public class OrderIteImpl extends ServiceImpl<OrderItemMapper , OrderItem> implements OrderItemService {
}
