package com.atguigu.ssyx.user.service.Impl;

import com.atguigu.ssyx.model.user.Leader;
import com.atguigu.ssyx.user.mapper.UserLeaderMapper;
import com.atguigu.ssyx.user.service.UserLeaderService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

@Service
public class UserLeaderServiceImpl extends ServiceImpl<UserLeaderMapper, Leader> implements UserLeaderService {
}
