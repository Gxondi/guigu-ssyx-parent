package com.atguigu.ssyx.client.user;

import com.atguigu.ssyx.model.user.User;
import com.atguigu.ssyx.vo.user.LeaderAddressVo;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(value = "service-user")//指定调用的服务
public interface UserFeignClient {
    @GetMapping("api/user/leader/inner/getUserAddressByUserId/{userId}")
    LeaderAddressVo findUserById(@PathVariable(value = "userId") Long userId);

    @GetMapping("api/user/leader/inner/getUserInfo/{userId}")
    User getUserInfo(@PathVariable Long userId);
}
