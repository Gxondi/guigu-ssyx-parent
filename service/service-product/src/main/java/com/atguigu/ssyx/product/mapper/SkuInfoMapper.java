package com.atguigu.ssyx.product.mapper;

import com.atguigu.ssyx.model.product.SkuInfo;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface SkuInfoMapper extends BaseMapper<SkuInfo> {
    void unlockStock(@Param("skuId") Long skuId,@Param("skuNum") Integer skuNum);
    //验证库存
    SkuInfo checkStock(@Param("skuId") Long skuId,@Param("skuNum") Integer skuNum);
    //加锁
    Integer lockStock(@Param("skuId") Long skuId,@Param("skuNum") Integer skuNum);
}
