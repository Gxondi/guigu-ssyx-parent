package com.atguigu.ssyx.product.service.Impl;

import com.atguigu.ssyx.model.product.Attr;
import com.atguigu.ssyx.product.mapper.AttrMapper;
import com.atguigu.ssyx.product.service.AttrService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AttrServiceImpl extends ServiceImpl<AttrMapper, Attr> implements AttrService {
    @Override
    public List<Attr> getAttrList(Long groupId) {
        LambdaQueryWrapper<Attr> wrapper = new LambdaQueryWrapper<Attr>().eq(Attr::getAttrGroupId, groupId);
        List<Attr> list = baseMapper.selectList(wrapper);
        return list;
    }
}
