package com.atguigu.ssyx.acl.controller;

import com.atguigu.ssyx.common.result.Result;
import com.atguigu.ssyx.common.result.ResultCodeEnum;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@Api(tags = "登录接口")
@RestController
@RequestMapping("/admin/acl/index")
//@CrossOrigin
public class IndexController {
    @ApiOperation(value = "登录")
    @PostMapping("/login")
    public Result login(){
        String username = "admin";
        String password = "admin";
        Map<String, String> map = new HashMap<>();
        map.put("username", username);
        map.put("password", password);
        return Result.ok(map);
    }
    @ApiOperation(value = "用户信息")
    @GetMapping("/info")
    public Result getInfo(){
        String name = "admin";
        String avatar = "https://wpimg.wallstcn.com/f778738c-e4f8-4870-b634-56703b4acafe.gif";
        Map<String, String> map = new HashMap<>();
        map.put("name", name);
        map.put("avatar", avatar);
        return Result.ok(map);
    }
}
