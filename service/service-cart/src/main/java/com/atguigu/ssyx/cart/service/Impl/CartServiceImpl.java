package com.atguigu.ssyx.cart.service.Impl;

import com.atguigu.ssyx.cart.service.CartService;
import com.atguigu.ssyx.client.order.ProductFeignClient;
import com.atguigu.ssyx.common.constant.RedisConst;
import com.atguigu.ssyx.common.exception.SsyxException;
import com.atguigu.ssyx.common.result.ResultCodeEnum;
import com.atguigu.ssyx.enums.SkuType;
import com.atguigu.ssyx.model.order.CartInfo;
import com.atguigu.ssyx.model.product.SkuInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.BoundHashOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Service
public class CartServiceImpl implements CartService {
    @Autowired
    private RedisTemplate redisTemplate;
    @Autowired
    private ProductFeignClient productFeignClient;

    private String getCartKey(Long userId) {
        //1.如果用户已登录，购物车的key就是用户id
        return RedisConst.USER_KEY_PREFIX + userId + RedisConst.USER_CART_KEY_SUFFIX;
        //2.如果用户未登录，购物车的key就是临时id
    }

    @Override
    public void addToCart(Long userId, Long skuId, Integer skuNum) {
        //1.查询购物车中是否有当前要添加的商品
        String cartKey = getCartKey(userId);
        BoundHashOperations<String, String, CartInfo> hashOperations = redisTemplate.boundHashOps(cartKey);
        CartInfo cartInfo = null;
        //2.如果有，数量相加
        if (hashOperations.hasKey(skuId.toString())) {
            cartInfo = hashOperations.get(skuId.toString());
            int currentSkuNum = cartInfo.getSkuNum() + skuNum;
            if (currentSkuNum <= 0) {
                return;
            }
            cartInfo.setSkuNum(currentSkuNum);
            cartInfo.setCurrentBuyNum(currentSkuNum);
            if (currentSkuNum > cartInfo.getPerLimit()) {
                throw new SsyxException(ResultCodeEnum.SKU_LIMIT_ERROR);
            }
            cartInfo.setIsChecked(1);
            cartInfo.setUpdateTime(new Date());
        } else {
            //3.如果没有，直接添加
            skuNum = 1;
            SkuInfo skuInfo = productFeignClient.getSkuInfo(skuId);
            if (skuInfo == null) {
                throw new SsyxException(ResultCodeEnum.DATA_ERROR);
            }
            cartInfo = new CartInfo();
            cartInfo.setSkuId(skuId);
            cartInfo.setCategoryId(skuInfo.getCategoryId());
            cartInfo.setSkuType(skuInfo.getSkuType());
            cartInfo.setIsNewPerson(skuInfo.getIsNewPerson());
            cartInfo.setUserId(userId);
            cartInfo.setCartPrice(skuInfo.getPrice());
            cartInfo.setSkuNum(skuNum);
            cartInfo.setCurrentBuyNum(skuNum);
            cartInfo.setSkuType(SkuType.COMMON.getCode());
            cartInfo.setPerLimit(skuInfo.getPerLimit());
            cartInfo.setImgUrl(skuInfo.getImgUrl());
            cartInfo.setSkuName(skuInfo.getSkuName());
            cartInfo.setWareId(skuInfo.getWareId());
            cartInfo.setIsChecked(1);
            cartInfo.setStatus(1);
            cartInfo.setCreateTime(new Date());
            cartInfo.setUpdateTime(new Date());
        }
        //4.更新购物车
        hashOperations.put(skuId.toString(), cartInfo);
        this.setCartKeyExpire(cartKey);
    }

    @Override
    public void deleteCart(Long userId, Long skuId) {
        BoundHashOperations<String, String, CartInfo> hashOperations = redisTemplate.boundHashOps(getCartKey(userId));
        if (hashOperations.hasKey(skuId.toString())) {
            hashOperations.delete(skuId.toString());
        }
    }

    @Override
    public void deleteAllCart(Long userId) {
        BoundHashOperations<String, String, CartInfo> hashOperations = redisTemplate.boundHashOps(getCartKey(userId));
        List<CartInfo> values = hashOperations.values();
        for (CartInfo value : values) {
            hashOperations.delete(value.getSkuId().toString());
        }
    }

    @Override
    public void batchDeleteCart(Long userId, List<Long> skuIdList) {
        BoundHashOperations<String, String, CartInfo> hashOperations = redisTemplate.boundHashOps(getCartKey(userId));
        for (Long skuId : skuIdList) {
            if (hashOperations.hasKey(skuId.toString())) {
                hashOperations.delete(skuId.toString());
            }
        }
    }

    @Override
    public List<CartInfo> getCartList(Long userId) {
        List<CartInfo> cartInfoList = new ArrayList<>();
        BoundHashOperations<String, String, CartInfo> hashOperations = redisTemplate.boundHashOps(getCartKey(userId));
        if (StringUtils.isEmpty(userId)) {
            return cartInfoList;
        }
        cartInfoList = hashOperations.values();
        if(!CollectionUtils.isEmpty(cartInfoList)){
            cartInfoList.sort((o1, o2) -> {
                return o1.getUpdateTime().compareTo(o2.getUpdateTime());
            });
        }
        return cartInfoList;
    }

    @Override
    public void changeCheckCart(Long userId,Long skuId, Integer isChecked) {
        String cartKey = this.getCartKey(userId);
        BoundHashOperations<String,String,CartInfo> boundHashOperations = redisTemplate.boundHashOps(cartKey);
        CartInfo cartInfo = boundHashOperations.get(skuId.toString());
        if (cartInfo!=null){
            cartInfo.setIsChecked(isChecked);
            boundHashOperations.put(skuId.toString(),cartInfo);
            //设置key的时间
            setCartKeyExpire(cartKey);
        }
    }

    @Override
    public void changeAllCheckCart(Long userId, Integer isChecked) {
        String cartKey = this.getCartKey(userId);
        BoundHashOperations<String,String,CartInfo> boundHashOperations = redisTemplate.boundHashOps(cartKey);
        List<CartInfo> cartInfoList = boundHashOperations.values();
        if(!CollectionUtils.isEmpty(cartInfoList)){
            for (CartInfo cartInfo : cartInfoList) {
                cartInfo.setIsChecked(isChecked);
                setCartKeyExpire(cartKey);
            }
        }
    }

    @Override
    public void batchCheckCart(Long userId, List<Long> skuIdList, Integer isChecked) {
        String cartKey = this.getCartKey(userId);
        BoundHashOperations<String,String,CartInfo> boundHashOperations = redisTemplate.boundHashOps(cartKey);
        for (Long skuId : skuIdList) {
            CartInfo cartInfo = boundHashOperations.get(skuId.toString());
            cartInfo.setIsChecked(isChecked);
            setCartKeyExpire(cartKey);
        }
    }

    @Override
    public List<CartInfo> getCartCheckedList(Long userId) {
        String cartKey = getCartKey(userId);
        BoundHashOperations<String,String,CartInfo> boundHashOperations = redisTemplate.boundHashOps(cartKey);
        List<CartInfo> cartInfoList = boundHashOperations.values();
        List<CartInfo> collect = cartInfoList.stream().filter(cartInfo -> {
            return cartInfo.getIsChecked() == 1;
        }).collect(Collectors.toList());
        return collect;
    }

    @Override
    public void deleteCartItem(Long userId) {
        List<CartInfo> cartList = this.getCartList(userId);
        List<Long> skuIdList = cartList.stream().map(item -> {
            return item.getSkuId();
        }).collect(Collectors.toList());
        BoundHashOperations<String,String,CartInfo> boundHashOperations = redisTemplate.boundHashOps(getCartKey(userId));

        for (Long skuId : skuIdList) {
            boundHashOperations.delete(skuId.toString());
        }
    }

    /**
         * 设置购物车的过期时间
         * @param cartKey
         */
        private void setCartKeyExpire (String cartKey){
            redisTemplate.expire(cartKey, RedisConst.USER_CART_EXPIRE, TimeUnit.SECONDS);
        }
    }
