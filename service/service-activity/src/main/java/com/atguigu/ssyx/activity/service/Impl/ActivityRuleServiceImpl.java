package com.atguigu.ssyx.activity.service.Impl;

import com.atguigu.ssyx.activity.mapper.ActivityRuleMapper;
import com.atguigu.ssyx.activity.service.ActivityRuleService;
import com.atguigu.ssyx.model.activity.ActivityRule;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

@Service
public class ActivityRuleServiceImpl extends ServiceImpl<ActivityRuleMapper, ActivityRule> implements ActivityRuleService {
}
