package com.atguigu.ssyx.product.service.Impl;

import com.atguigu.ssyx.model.product.SkuPoster;
import com.atguigu.ssyx.product.mapper.SkuPosterMapper;
import com.atguigu.ssyx.product.service.SkuPosterService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class SkuPosterServiceImpl extends ServiceImpl<SkuPosterMapper, SkuPoster> implements SkuPosterService {
    @Override
    public List<SkuPoster> findBySkuId(Long skuId) {
        LambdaQueryWrapper<SkuPoster> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(SkuPoster::getSkuId, skuId);
        List<SkuPoster> skuPosters = baseMapper.selectList(wrapper);
        return skuPosters;
    }
}
