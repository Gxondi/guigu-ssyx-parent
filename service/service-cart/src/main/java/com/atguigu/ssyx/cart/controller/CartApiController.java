package com.atguigu.ssyx.cart.controller;

import com.atguigu.ssyx.cart.service.CartService;
import com.atguigu.ssyx.client.order.ActivityFeignClient;
import com.atguigu.ssyx.common.auth.AuthContextHolder;
import com.atguigu.ssyx.common.result.Result;
import com.atguigu.ssyx.model.order.CartInfo;
import com.atguigu.ssyx.vo.order.OrderConfirmVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("api/cart/")
public class CartApiController {
    @Autowired
    private CartService cartService;
    @Autowired
    private ActivityFeignClient activityFeignClient;
    @GetMapping("activityCartList")
    public Result activityCartList() {
        Long userId = AuthContextHolder.getUserId();
        List<CartInfo> cartInfoList = cartService.getCartList(userId);
        OrderConfirmVo orderConfirmVo = activityFeignClient.findCartActivityAndCoupon(userId,cartInfoList);//远程调用activity，findCartActivityAndCoupon
        return Result.ok(orderConfirmVo);
    }
    @GetMapping("checkCart/{skuId}/{isChecked}")
    public Result changeCheckCart(@PathVariable("skuId") Long skuId,@PathVariable("isChecked") Integer isChecked) {
        Long userId = AuthContextHolder.getUserId();
        cartService.changeCheckCart(userId,skuId,isChecked);
        return Result.ok(null);
    }
    @GetMapping("checkAllCart/{isChecked}")
    public Result checkAllCart(@PathVariable("isChecked") Integer isChecked) {
        Long userId = AuthContextHolder.getUserId();
        cartService.changeAllCheckCart(userId,isChecked);
        return Result.ok(null);
    }

    @GetMapping("batchCheckCart/{isChecked}")
    public Result batchCheckCart(@RequestBody List<Long> skuIdList,@PathVariable("isChecked") Integer isChecked) {
        Long userId = AuthContextHolder.getUserId();
        cartService.batchCheckCart(userId,skuIdList,isChecked);
        return Result.ok(null);
    }
    @GetMapping("addToCart/{skuId}/{skuNum}")
    public Result addToCart( @PathVariable("skuId") Long skuId,@PathVariable("skuNum") Integer skuNum) {
        Long userId = AuthContextHolder.getUserId();
        cartService.addToCart(userId,skuId,skuNum);
        return Result.ok(null);
    }
    @DeleteMapping("deleteCart{skuId}")
    public Result deleteCart( @PathVariable("skuId") Long skuId) {
        Long userId = AuthContextHolder.getUserId();
        cartService.deleteCart(userId,skuId);
        return Result.ok(null);
    }
    @DeleteMapping("deleteAllCart")
    public Result deleteAllCart() {
        Long userId = AuthContextHolder.getUserId();
        cartService.deleteAllCart(userId);
        return Result.ok(null);
    }
    @DeleteMapping("batchDeleteCart")
    public Result batchDeleteCart(@RequestBody List<Long> skuIdList) {
        Long userId = AuthContextHolder.getUserId();
        cartService.batchDeleteCart(userId,skuIdList);
        return Result.ok(null);
    }
    @GetMapping("cartList")
    public Result cartList() {
        Long userId = AuthContextHolder.getUserId();
        List<CartInfo> cartInfoList = cartService.getCartList(userId);
        return Result.ok(cartInfoList);
    }
    /**
     * 根据用户Id 查询购物车列表
     *
     * @param userId
     * @return
     */
    @GetMapping("inner/getCartCheckedList/{userId}")
    public List<CartInfo> getCartCheckedList(@PathVariable("userId") Long userId) {
        return cartService.getCartCheckedList(userId);
    }

}
