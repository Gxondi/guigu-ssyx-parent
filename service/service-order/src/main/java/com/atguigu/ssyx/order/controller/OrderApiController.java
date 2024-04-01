package com.atguigu.ssyx.order.controller;

import com.atguigu.ssyx.common.result.Result;
import com.atguigu.ssyx.model.order.OrderInfo;
import com.atguigu.ssyx.order.service.OrderService;
import com.atguigu.ssyx.vo.order.OrderSubmitVo;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;

@RestController
@RequestMapping("api/order")
public class OrderApiController {
    @Autowired
    private OrderService orderService;

    @ApiOperation("确认订单")
    @GetMapping("auth/confirmOrder")
    public Result confirm() {
        return Result.ok(orderService.confirmOrder());
    }

    @PostMapping("auth/submitOrder")
    public Result submitOrder(@RequestBody OrderSubmitVo orderSubmitVo){
        Long orderNo = orderService.submitOrder(orderSubmitVo);
        return Result.ok(orderNo);
    }
    @GetMapping("auth/getOrderInfoById/{orderId}")
    public Result getOrderInfoById(@PathVariable Long orderId){
        return Result.ok(orderService.getOrderInfo(orderId));
    }

    @GetMapping("inner/getOrderInfoById/{orderNo}")
    OrderInfo getOrderInfo(@PathVariable("orderNo") String orderNo){
        OrderInfo orderInfo = orderService.getOrderInfoByOrderNo(orderNo);
        return orderInfo;
    }
}
