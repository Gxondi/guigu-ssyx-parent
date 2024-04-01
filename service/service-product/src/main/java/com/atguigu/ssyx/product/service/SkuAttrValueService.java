package com.atguigu.ssyx.product.service;

import com.atguigu.ssyx.model.product.SkuAttrValue;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

public interface SkuAttrValueService extends IService<SkuAttrValue> {
    List<SkuAttrValue> findBySkuId(Long skuId);
}
