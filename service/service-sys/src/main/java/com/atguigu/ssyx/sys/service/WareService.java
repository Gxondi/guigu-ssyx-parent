package com.atguigu.ssyx.sys.service;

import com.atguigu.ssyx.model.sys.Ware;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;

public interface WareService extends IService<Ware> {
    IPage<Ware> selectPage(Page pageParam, Ware ware);

    void findAllList();
}
