package com.atguigu.ssyx.sys.service.Impl;

import com.atguigu.ssyx.common.exception.SsyxException;
import com.atguigu.ssyx.common.result.ResultCodeEnum;
import com.atguigu.ssyx.model.sys.RegionWare;
import com.atguigu.ssyx.model.sys.Ware;
import com.atguigu.ssyx.sys.mapper.RegionWareMapper;
import com.atguigu.ssyx.sys.service.RegionWareService;
import com.atguigu.ssyx.vo.sys.RegionWareQueryVo;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class RegionWareServiceImpl extends ServiceImpl<RegionWareMapper, RegionWare> implements RegionWareService {
    @Autowired
    private RegionWareMapper regionWareMapper;
    @Override
    public IPage<RegionWare> selectPage(Page pageParam, RegionWareQueryVo regionWareQueryVo) {
        //根据关键字模糊查询
        String keyword = regionWareQueryVo.getKeyword();
        LambdaQueryWrapper<RegionWare> wrapper = new LambdaQueryWrapper();
        if (keyword != null) {
            wrapper.like(RegionWare::getWareName, keyword);
            return baseMapper.selectPage(pageParam, wrapper);
        }
        return  baseMapper.selectPage(pageParam, null);
    }

    @Override
    public void updateStatus(Long id, Integer status) {
        LambdaQueryWrapper<RegionWare> wrapper = new LambdaQueryWrapper();
        wrapper.eq(RegionWare::getId, id);
        RegionWare regionWare = baseMapper.selectOne(wrapper);
        regionWare.setStatus(status);
        baseMapper.updateById(regionWare);
    }

    @Override
    public void saveRegionWare(RegionWare regionWare) {
        LambdaQueryWrapper<RegionWare> queryWrapper = new LambdaQueryWrapper();
        queryWrapper.eq(RegionWare::getRegionId, regionWare.getRegionId());
        //判断是否已经开通
        Integer count = regionWareMapper.selectCount(queryWrapper);
        if(count > 0) {
            throw new SsyxException(ResultCodeEnum.REGION_OPEN);
        }
        baseMapper.insert(regionWare);
    }
}
