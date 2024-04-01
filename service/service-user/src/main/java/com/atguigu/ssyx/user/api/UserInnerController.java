package com.atguigu.ssyx.user.api;

import com.atguigu.ssyx.model.user.User;
import com.atguigu.ssyx.user.service.UserService;
import com.atguigu.ssyx.vo.user.LeaderAddressVo;
import com.atguigu.ssyx.vo.user.UserLoginVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("api/user/leader")
public class UserInnerController {
    @Autowired
    private UserService userService;
    @GetMapping("/inner/getUserAddressByUserId/{userId}")
    public LeaderAddressVo findUserById(@PathVariable(value = "userId") Long userId){
        return userService.getLeaderAddressByUserId(userId);
    }

    @GetMapping("inner/getUserInfo/{userId}")
    public User getUserInfo(@PathVariable Long userId){
        return userService.getById(userId);
    }
}
