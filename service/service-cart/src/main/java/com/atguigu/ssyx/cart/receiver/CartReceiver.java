package com.atguigu.ssyx.cart.receiver;

import com.atguigu.ssyx.cart.service.CartService;
import com.atguigu.ssyx.common.constant.MqConst;
import com.rabbitmq.client.Channel;
import io.lettuce.core.dynamic.annotation.Key;
import org.springframework.amqp.rabbit.annotation.Exchange;
import org.springframework.amqp.rabbit.annotation.Queue;
import org.springframework.amqp.rabbit.annotation.QueueBinding;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.integration.annotation.Router;
import org.springframework.stereotype.Component;
import org.springframework.amqp.core.Message;

import java.io.IOException;

@Component
public class CartReceiver {
    @Autowired
    private CartService cartService;

    @RabbitListener(bindings = @QueueBinding(
            value = @Queue(value = MqConst.QUEUE_DELETE_CART, durable = "true"),
            exchange = @Exchange(value = MqConst.EXCHANGE_ORDER_DIRECT),
            key = {MqConst.ROUTING_DELETE_CART}
    )
    )
    public void deleteCartItem(Long userId, Message message, Channel channel) throws IOException {
        if(userId!=null){
            cartService.deleteCartItem(userId);
        }
        channel.basicAck(message.getMessageProperties().getDeliveryTag(), false);
    }
}
