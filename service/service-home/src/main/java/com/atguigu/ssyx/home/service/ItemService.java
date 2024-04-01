package com.atguigu.ssyx.home.service;

import com.atguigu.ssyx.model.activity.CouponInfo;
import com.baomidou.mybatisplus.extension.service.IService;
import org.springframework.stereotype.Repository;

import java.util.Map;
import java.util.concurrent.ExecutionException;

@Repository
public interface ItemService  {
    Map<String, Object> findItem(Long skuId , Long userId) throws ExecutionException, InterruptedException;
}
