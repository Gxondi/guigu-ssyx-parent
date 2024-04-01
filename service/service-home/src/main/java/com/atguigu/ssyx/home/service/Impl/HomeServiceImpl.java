package com.atguigu.ssyx.home.service.Impl;

import com.atguigu.ssyx.client.order.ProductFeignClient;
import com.atguigu.ssyx.client.order.SearchFeignClient;
import com.atguigu.ssyx.client.user.UserFeignClient;
import com.atguigu.ssyx.home.service.HomeService;
import com.atguigu.ssyx.model.product.Category;
import com.atguigu.ssyx.model.product.SkuInfo;
import com.atguigu.ssyx.model.search.SkuEs;
import com.atguigu.ssyx.vo.user.LeaderAddressVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ThreadPoolExecutor;

@Service
public class HomeServiceImpl implements HomeService {
    @Autowired
    private UserFeignClient userFeignClient;
    @Autowired
    private ProductFeignClient productFeignClient;
    @Autowired
    private SearchFeignClient searchFeignClient;
    @Autowired
    private ThreadPoolExecutor threadPoolExecutor;

    @Override
    public Map<String, Object> homeData(Long userId) {
        Map<String, Object> map = new HashMap<>();
        //根据userId获取用户信息调用远程接口service-user
        //根据userId获取用户收货地址
        CompletableFuture<Void> leaderAddressVoCompletableFuture = CompletableFuture.runAsync(() -> {
            LeaderAddressVo leaderAddressVo = userFeignClient.findUserById(userId);
            map.put("leaderAddressVo", leaderAddressVo);
        }, threadPoolExecutor);

        //获取所有分类信息service-product
        CompletableFuture<Void> categoryListCompletableFuture = CompletableFuture.runAsync(() -> {
            List<Category> categoryList = productFeignClient.findCategoryList();
            map.put("categoryList", categoryList);
        }, threadPoolExecutor);

        //获取新人专项活动service-product
        CompletableFuture<Void> newPersonActivityCompletableFuture = CompletableFuture.runAsync(() -> {
            List<SkuInfo> newPersonActivity = productFeignClient.findNerPersonActivity();
            map.put("newPersonSkuInfoList", newPersonActivity);
        }, threadPoolExecutor);
        //获取热销商品service-search
        CompletableFuture<Void> hotSkuListCompletableFuture = CompletableFuture.runAsync(() -> {
            List<SkuEs> hotSkuList = searchFeignClient.findHostSkuList();
            map.put("hotSkuList", hotSkuList);
        }, threadPoolExecutor);

        CompletableFuture.allOf(leaderAddressVoCompletableFuture,
                                categoryListCompletableFuture,
                                newPersonActivityCompletableFuture,
                                hotSkuListCompletableFuture).join();
        return map;

        // LeaderAddressVo leaderAddressVo = userFeignClient.findUserById(userId);
        // map.put("leaderAddressVo", leaderAddressVo);
        // List<Category> categoryList = productFeignClient.findCategoryList();
        // map.put("categoryList", categoryList);
        // List<SkuInfo> newPersonActivity = productFeignClient.findNerPersonActivity();
        // map.put("newPersonSkuInfoList", newPersonActivity);
//        List<SkuEs> hotSkuList = searchFeignClient.findHostSkuList();
//        map.put("hotSkuList", hotSkuList);
    }
}
