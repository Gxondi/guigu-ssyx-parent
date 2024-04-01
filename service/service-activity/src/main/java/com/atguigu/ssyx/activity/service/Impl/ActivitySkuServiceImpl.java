package com.atguigu.ssyx.activity.service.Impl;

import com.atguigu.ssyx.activity.mapper.ActivitySkuMapper;
import com.atguigu.ssyx.activity.service.ActivitySkuService;
import com.atguigu.ssyx.model.activity.ActivitySku;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

@Service
public class ActivitySkuServiceImpl extends ServiceImpl<ActivitySkuMapper, ActivitySku> implements ActivitySkuService {
}
