package com.atguigu.ssyx.user.controller;

import com.alibaba.fastjson.JSONObject;
import com.atguigu.ssyx.common.auth.AuthContextHolder;
import com.atguigu.ssyx.common.constant.RedisConst;
import com.atguigu.ssyx.common.exception.SsyxException;
import com.atguigu.ssyx.common.result.Result;
import com.atguigu.ssyx.common.utils.JwtHelper;
import com.atguigu.ssyx.enums.UserType;
import com.atguigu.ssyx.model.user.User;
import com.atguigu.ssyx.user.service.UserService;
import com.atguigu.ssyx.user.utils.ConstantPropertiesUtil;
import com.atguigu.ssyx.user.utils.HttpClientUtils;
import com.atguigu.ssyx.vo.user.LeaderAddressVo;
import com.atguigu.ssyx.vo.user.UserLoginVo;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import static com.atguigu.ssyx.common.result.ResultCodeEnum.FETCH_ACCESSTOKEN_FAILD;

@Api(tags = "用户管理")
@RestController
@RequestMapping("/api/user/weixin")
public class WeiXinApiController {
    @Autowired
    private UserService userService;
    @Autowired
    private RedisTemplate redisTemplate;
    @ApiOperation("获取微信登录openid")
    @GetMapping("/wxLogin/{code}")
    public Result wxLogin(@PathVariable String code) {
        //拼装openid+appid+appsecret
        String wxOpenAppId = ConstantPropertiesUtil.WX_OPEN_APP_ID;
        String wxOpenAppSecret = ConstantPropertiesUtil.WX_OPEN_APP_SECRET;
        //使用code和appid以及appscrect换取access_token
        StringBuffer baseAccessTokenUrl = new StringBuffer()
                .append("https://api.weixin.qq.com/sns/jscode2session")
                .append("?appid=%s")
                .append("&secret=%s")
                .append("&js_code=%s")
                .append("&grant_type=authorization_code");
        String accessTokenUrl = String.format(baseAccessTokenUrl.toString(), wxOpenAppId, wxOpenAppSecret, code);
        //发送请求,返回openid和session_key
        String result = null;
        try {
            result = HttpClientUtils.get(accessTokenUrl);
        } catch (Exception e) {
            throw new SsyxException(FETCH_ACCESSTOKEN_FAILD.getCode(), FETCH_ACCESSTOKEN_FAILD.getMessage());
        }
        //解析json
        JSONObject jsonObject = JSONObject.parseObject(result);
        String openid = jsonObject.getString("openid");
        String string = jsonObject.getString("session_key");
        //根据openid判断用户是否已经注册，如果没有注册则注册用户
        User user = userService.getUserByOpenId(openid);
        if (user == null) {
            user = new User();
            user.setOpenId(openid);
            user.setNickName(openid);
            user.setPhotoUrl("");
            user.setUserType(UserType.USER);
            user.setIsNew(0);
            userService.save(user);
        }
        //根据user查询团长以及提货地点
        LeaderAddressVo leaderAddressVo = userService.getLeaderAddressByUserId(user.getId());
        //生成token字符串
        String token = JwtHelper.createToken(user.getId(), user.getNickName());
        //获取当前用户登录信息
        UserLoginVo userLoginVo = userService.getLoginUserInfo(user.getId());
        //存入redis
        redisTemplate.opsForValue().set(RedisConst.USER_LOGIN_KEY_PREFIX + user.getId(), userLoginVo, RedisConst.USERKEY_TIMEOUT, TimeUnit.DAYS);
        Map<String, Object> map = new HashMap<>();
        map.put("user", user);
        map.put("token", token);
        map.put("leaderAddressVo", leaderAddressVo);
        return Result.ok(map);
    }
    @PostMapping("/auth/updateUser")
    @ApiOperation(value = "更新用户昵称与头像")
    public Result updateUser(@RequestBody User user) {
        User user1 = userService.getById(AuthContextHolder.getUserId());
        //把昵称更新为微信用户
        user1.setNickName(user.getNickName().replaceAll("[ue000-uefff]", "*"));
        user1.setPhotoUrl(user.getPhotoUrl());
        userService.updateById(user1);
        return Result.ok(null);
    }
}
