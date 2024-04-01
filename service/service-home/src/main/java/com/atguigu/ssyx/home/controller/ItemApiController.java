package com.atguigu.ssyx.home.controller;

import com.atguigu.ssyx.common.auth.AuthContextHolder;
import com.atguigu.ssyx.common.result.Result;
import com.atguigu.ssyx.home.config.ThreadPoolConfig;
import com.atguigu.ssyx.home.service.ItemService;
import io.swagger.annotations.Api;
import org.checkerframework.checker.units.qual.A;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;
import java.util.concurrent.ExecutionException;

@Api("商品详情")
@RestController
@RequestMapping("api/home/")
public class ItemApiController {
    @Autowired
    private ItemService itemService;

    @GetMapping("item/{id}")
    public Result index(@PathVariable("id") Long id) throws ExecutionException, InterruptedException {
        Long userId = AuthContextHolder.getUserId();
        Map<String, Object> map = itemService.findItem(id, userId);
        return Result.ok(map);
    }
}
