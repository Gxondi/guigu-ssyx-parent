package com.atguigu.ssyx.sys.service.Impl;

import com.atguigu.ssyx.model.sys.Region;
import com.atguigu.ssyx.sys.mapper.RegionMapper;
import com.atguigu.ssyx.sys.service.RegionService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class RegionServiceImpl extends ServiceImpl<RegionMapper, Region> implements RegionService {
    @Override
    public List<Region> findRegionByKeyword(String keyword) {
        LambdaQueryWrapper<Region> wrapper = new LambdaQueryWrapper();
        wrapper.like(Region::getName, keyword);
        if (keyword != null) {
            return baseMapper.selectList(wrapper);
        }
        return baseMapper.selectList(null);
    }
}
