package com.atguigu.ssyx.home.service.Impl;

import com.atguigu.ssyx.client.order.ActivityFeignClient;
import com.atguigu.ssyx.client.order.ProductFeignClient;
import com.atguigu.ssyx.client.order.SearchFeignClient;
import com.atguigu.ssyx.home.service.ItemService;
import com.atguigu.ssyx.vo.product.SkuInfoVo;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ThreadPoolExecutor;

@Service
public class ItemServiceImpl implements ItemService {
    @Autowired
    private ThreadPoolExecutor threadPoolExecutor;
    @Autowired
    private ProductFeignClient productFeignClient;
    @Autowired
    private SearchFeignClient searchFeignClient;
    @Autowired
    private ActivityFeignClient activityFeignClient;
    @Override
    public Map<String, Object> findItem(Long skuId , Long userId) throws ExecutionException, InterruptedException {
        Map<String, Object> result = new HashMap<>();

        //查询sku基本信息
        CompletableFuture<SkuInfoVo> skuInfoVoCompletableFuture = CompletableFuture.supplyAsync(() -> {
            //查询sku
            SkuInfoVo skuInfoVo = productFeignClient.getSkuInfoVo(skuId);
            result.put("skuInfoVo", skuInfoVo);
            return skuInfoVo;
        }, threadPoolExecutor);

        CompletableFuture<Void> couponInfoCompletableFuture = CompletableFuture.runAsync(() -> {
            //查询优惠价基本信息
            Map<String, Object> activityAndCouponMap = activityFeignClient.findCouponInfoBySkuIdAndUserId(skuId,userId);
            result.putAll(activityAndCouponMap);
        }, threadPoolExecutor);

        CompletableFuture<Void> hotCompletableFuture = CompletableFuture.runAsync(() -> {
            //更新search中的热度
            Boolean b = searchFeignClient.incrHotScore(skuId);
        }, threadPoolExecutor);

        //等待所有任务完成
        CompletableFuture<Void> voidCompletableFuture = CompletableFuture.allOf(skuInfoVoCompletableFuture, couponInfoCompletableFuture, hotCompletableFuture);
        voidCompletableFuture.get();
        return result;
    }
}
