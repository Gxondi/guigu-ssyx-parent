package com.atguigu.ssyx.user.service.Impl;

import com.atguigu.ssyx.model.user.Leader;
import com.atguigu.ssyx.model.user.User;
import com.atguigu.ssyx.model.user.UserDelivery;
import com.atguigu.ssyx.user.mapper.UserDeliveryMapper;
import com.atguigu.ssyx.user.mapper.UserLeaderMapper;
import com.atguigu.ssyx.user.mapper.UserMapper;
import com.atguigu.ssyx.user.service.UserService;
import com.atguigu.ssyx.vo.user.LeaderAddressVo;
import com.atguigu.ssyx.vo.user.UserLoginVo;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UserServiceImpl extends ServiceImpl<UserMapper, User>implements UserService {
   @Autowired
   private UserDeliveryMapper userDeliveryMapper;
   @Autowired
   private UserLeaderMapper userLeaderMapper;
    @Override
    public User getUserByOpenId(String openid) {

        return baseMapper.selectOne(new QueryWrapper<User>().eq("open_id", openid));
    }

    @Override
    public LeaderAddressVo getLeaderAddressByUserId(Long id) {
        LeaderAddressVo leaderAddressVo = new LeaderAddressVo();
        LambdaQueryWrapper<UserDelivery> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(UserDelivery::getUserId, id)
                .eq(UserDelivery::getIsDefault, 1);
        System.out.println("id = " + id);
        UserDelivery userDelivery = userDeliveryMapper.selectOne(queryWrapper);
        System.out.println("userDelivery = " + userDelivery);
        if (userDelivery == null) {
            return null;
        }
        Long leaderId = userDelivery.getLeaderId();
        Leader leader = userLeaderMapper.selectById(leaderId);
        BeanUtils.copyProperties(leader, leaderAddressVo);
        leaderAddressVo.setUserId(id);
        leaderAddressVo.setLeaderId(leader.getId());
        leaderAddressVo.setLeaderName(leader.getName());
        leaderAddressVo.setLeaderPhone(leader.getPhone());
        leaderAddressVo.setWareId(userDelivery.getWareId());
        leaderAddressVo.setStorePath(leader.getStorePath());
        return leaderAddressVo;
    }

    @Override
    public UserLoginVo getLoginUserInfo(Long id) {
        UserLoginVo userLoginVo = new UserLoginVo();
        User user = baseMapper.selectById(id);
        BeanUtils.copyProperties(user, userLoginVo);
        LambdaQueryWrapper<UserDelivery> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(UserDelivery::getUserId, id).eq(UserDelivery::getIsDefault, 1);
        UserDelivery userDelivery = userDeliveryMapper.selectOne(queryWrapper);
        if (userDelivery != null) {
            userLoginVo.setWareId(userDelivery.getWareId());
            userLoginVo.setLeaderId(userDelivery.getLeaderId());
        }else {
            userLoginVo.setWareId(1L);
            userLoginVo.setLeaderId(1L);
        }

        return userLoginVo;
    }
}
