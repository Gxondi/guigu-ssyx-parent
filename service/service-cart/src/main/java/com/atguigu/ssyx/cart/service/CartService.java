package com.atguigu.ssyx.cart.service;

import com.atguigu.ssyx.model.order.CartInfo;

import java.util.List;

public interface CartService {
    void addToCart(Long userId, Long skuId, Integer skuNum);

    void deleteCart(Long userId, Long skuId);

    void deleteAllCart(Long userId);

    void batchDeleteCart(Long userId, List<Long> skuIdList);

    List<CartInfo> getCartList(Long userId);

    void changeCheckCart(Long userId, Long skuId, Integer isChecked);

    void changeAllCheckCart(Long userId, Integer isChecked);

    void batchCheckCart(Long userId, List<Long> skuIdList, Integer isChecked);

    List<CartInfo> getCartCheckedList(Long userId);

    void deleteCartItem(Long userId);
}
