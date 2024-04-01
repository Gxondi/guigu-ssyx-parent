package com.atguigu.ssyx.search.service;

import com.atguigu.ssyx.model.product.SkuInfo;
import com.atguigu.ssyx.model.search.SkuEs;
import com.atguigu.ssyx.vo.search.SkuEsQueryVo;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface SkuApiService {
    void upperSku(Long skuId);

    void lowerSku(Long skuId);

    List<SkuEs> findHostSkuList();

    Page<SkuEs> search(Pageable pageable, SkuEsQueryVo skuEsQueryVo);

    void incrHotScore(Long skuId);


//    List<SkuInfo> findSkuInfoList(Long page , Long limit, SkuEs skuEs);
}
