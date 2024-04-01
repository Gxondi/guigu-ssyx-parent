package com.atguigu.ssyx.product.service;

import com.atguigu.ssyx.model.product.SkuImage;
import com.baomidou.mybatisplus.extension.service.IService;
import org.springframework.stereotype.Repository;

import java.util.List;
@Repository
public interface SkuImageService extends IService<SkuImage> {
    List<SkuImage> findBySkuId(Long skuId);
}
