package com.atguigu.ssyx.product.service;

import com.atguigu.ssyx.model.product.AttrGroup;
import com.atguigu.ssyx.model.product.SkuAttrValue;
import com.atguigu.ssyx.model.product.SkuInfo;
import com.atguigu.ssyx.vo.product.SkuInfoQueryVo;
import com.atguigu.ssyx.vo.product.SkuInfoVo;
import com.atguigu.ssyx.vo.product.SkuStockLockVo;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

public interface SkuInfoService extends IService<SkuInfo> {
    IPage<SkuInfo> selectPage(Page pageParam, SkuInfoQueryVo skuInfoQueryVo);

    void saveSkuInfo(SkuInfoVo skuInfoVo);

    SkuInfoVo getSkuInfoById(Long skuId);

    void updateSkuInfo(SkuInfoVo skuInfoVo);

    void updateNewPerson(Long skuId, Integer status);

    void updatePublish(Long skuId, Integer status);

    void updateCheck(Long skuId, Integer status);

    List<SkuInfo> findSkuInfoByKeyword(String keyword);

    List<SkuInfo> findNerPersonActivity();

    Boolean checkAndLock(List<SkuStockLockVo> skuStockLockVos, String orderNo);
}
