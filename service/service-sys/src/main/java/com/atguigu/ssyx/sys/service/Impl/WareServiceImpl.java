package com.atguigu.ssyx.sys.service.Impl;

import com.atguigu.ssyx.model.sys.Ware;
import com.atguigu.ssyx.sys.mapper.WareMapper;
import com.atguigu.ssyx.sys.service.WareService;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

@Service
public class WareServiceImpl extends ServiceImpl<WareMapper, Ware> implements WareService {
    @Override
    public IPage<Ware> selectPage(Page pageParam, Ware ware) {
        return null;
    }

    @Override
    public void findAllList() {
        baseMapper.selectList(null);
    }
}
