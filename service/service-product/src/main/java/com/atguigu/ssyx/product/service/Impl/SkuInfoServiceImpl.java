package com.atguigu.ssyx.product.service.Impl;

import com.atguigu.ssyx.common.config.RedissonConfig;
import com.atguigu.ssyx.common.constant.MqConst;
import com.atguigu.ssyx.common.constant.RedisConst;
import com.atguigu.ssyx.common.exception.SsyxException;
import com.atguigu.ssyx.common.result.ResultCodeEnum;
import com.atguigu.ssyx.common.service.RabbitService;
import com.atguigu.ssyx.model.product.*;
import com.atguigu.ssyx.product.mapper.SkuImageMapper;
import com.atguigu.ssyx.product.mapper.SkuInfoMapper;
import com.atguigu.ssyx.product.service.SkuAttrValueService;
import com.atguigu.ssyx.product.service.SkuImageService;
import com.atguigu.ssyx.product.service.SkuInfoService;
import com.atguigu.ssyx.product.service.SkuPosterService;
import com.atguigu.ssyx.vo.product.SkuInfoQueryVo;
import com.atguigu.ssyx.vo.product.SkuInfoVo;
import com.atguigu.ssyx.vo.product.SkuStockLockVo;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class SkuInfoServiceImpl extends ServiceImpl<SkuInfoMapper, SkuInfo> implements SkuInfoService {
    @Autowired
    private SkuInfoMapper skuInfoMapper;
    @Autowired
    private SkuImageService skuImageService;
    @Autowired
    private SkuAttrValueService skuAttrValueService;
    @Autowired
    private SkuPosterService skuPosterService;
    @Autowired
    private RabbitService rabbitService;
    @Autowired
    private RedisTemplate redisTemplate;

    @Autowired
    private RedissonClient redissonClient;
    @Override
    public IPage<SkuInfo> selectPage(Page pageParam, SkuInfoQueryVo skuInfoQueryVo) {
        String keyword = skuInfoQueryVo.getKeyword();
        String skuType = skuInfoQueryVo.getSkuType();
        Long categoryId = skuInfoQueryVo.getCategoryId();
        LambdaQueryWrapper<SkuInfo> wrapper = new LambdaQueryWrapper();
        if (!StringUtils.isEmpty(keyword)) {
            wrapper.like(SkuInfo::getSkuName, keyword);
        }
        if (!StringUtils.isEmpty(skuType)) {
            wrapper.eq(SkuInfo::getSkuType, skuType);
        }
        if (!StringUtils.isEmpty(categoryId)) {
            wrapper.eq(SkuInfo::getCategoryId, categoryId);
        }
        IPage<SkuInfo> page = baseMapper.selectPage(pageParam, wrapper);
        return page;

    }
    @Transactional
    @Override
    public void saveSkuInfo(SkuInfoVo skuInfoVo) {
        /**
         * 保存sku基本信息
         */
        SkuInfo skuInfo = new SkuInfo();
        BeanUtils.copyProperties(skuInfoVo, skuInfo);
        baseMapper.insert(skuInfo);
        //sku_id 和 海报图片关联插入数据库
        List<SkuPoster> skuPosterList = skuInfoVo.getSkuPosterList();
        if (!CollectionUtils.isEmpty(skuPosterList)) {
            for (SkuPoster skuPoster : skuPosterList) {
                skuPoster.setSkuId(skuInfo.getId());
            }
            skuPosterService.saveBatch(skuPosterList);
        }
        //sku_id 和 sku图片关联插入数据库
        List<SkuImage> skuImagesList = skuInfoVo.getSkuImagesList();
        if (!CollectionUtils.isEmpty(skuImagesList)) {
            for (SkuImage skuImage : skuImagesList) {
                skuImage.setSkuId(skuInfo.getId());
            }
            skuImageService.saveBatch(skuImagesList);
        }
        //sku_id 和 sku属性关联插入数据库
        List<SkuAttrValue> skuAttrValueList = skuInfoVo.getSkuAttrValueList();
        if (!CollectionUtils.isEmpty(skuAttrValueList)) {
            for (SkuAttrValue skuAttrValue : skuAttrValueList) {
                skuAttrValue.setSkuId(skuInfo.getId());
            }
            skuAttrValueService.saveBatch(skuAttrValueList);
        }
    }
    @Transactional
    @Override
    public SkuInfoVo getSkuInfoById(Long skuId) {
        //要会显得数据
        SkuInfoVo skuInfoVo = new SkuInfoVo();
        SkuInfo skuInfo = skuInfoMapper.selectById(skuId);
        List<SkuImage> skuImages = skuImageService.findBySkuId(skuId);
        List<SkuAttrValue> skuAttrValues = skuAttrValueService.findBySkuId(skuId);
        List<SkuPoster> skuPosters = skuPosterService.findBySkuId(skuId);
        BeanUtils.copyProperties(skuInfo, skuInfoVo);

        skuInfoVo.setSkuImagesList(skuImages);
        skuInfoVo.setSkuAttrValueList(skuAttrValues);
        skuInfoVo.setSkuPosterList(skuPosters);

        return skuInfoVo;
    }

    @Transactional
    @Override
    public void updateSkuInfo(SkuInfoVo skuInfoVo) {
        Long id = skuInfoVo.getId();
        //更新sku基本信息
        SkuInfo skuInfo = new SkuInfo();
        BeanUtils.copyProperties(skuInfoVo, skuInfo);
        baseMapper.delete(new LambdaQueryWrapper<SkuInfo>().eq(SkuInfo::getId, id));
        baseMapper.insert(skuInfo);
        //更新sku图片信息
        skuImageService.remove(new LambdaQueryWrapper<SkuImage>().eq(SkuImage::getSkuId, id));
        List<SkuImage> skuImagesList = skuInfoVo.getSkuImagesList();
        if (!CollectionUtils.isEmpty(skuImagesList)) {
            for (SkuImage skuImage : skuImagesList) {
                skuImage.setSkuId(id);
            }
            skuImageService.saveBatch(skuImagesList);
        }
        //更新sku属性信息
        skuAttrValueService.remove(new LambdaQueryWrapper<SkuAttrValue>().eq(SkuAttrValue::getSkuId, id));
        List<SkuAttrValue> skuAttrValueList = skuInfoVo.getSkuAttrValueList();
        if (!CollectionUtils.isEmpty(skuAttrValueList)) {
            for (SkuAttrValue skuAttrValue : skuAttrValueList) {
                skuAttrValue.setSkuId(id);
            }
            skuAttrValueService.saveBatch(skuAttrValueList);
        }
        //更新sku海报信息
        skuPosterService.remove(new LambdaQueryWrapper<SkuPoster>().eq(SkuPoster::getSkuId, id));
        List<SkuPoster> skuPosterList = skuInfoVo.getSkuPosterList();
        if (!CollectionUtils.isEmpty(skuPosterList)) {
            for (SkuPoster skuPoster : skuPosterList) {
                skuPoster.setSkuId(id);
            }
            skuPosterService.saveBatch(skuPosterList);
        }
    }

    @Override
    public void updateNewPerson(Long skuId, Integer status) {
        SkuInfo skuInfo = baseMapper.selectById(skuId);
        skuInfo.setIsNewPerson(status);
        baseMapper.update(skuInfo, new LambdaQueryWrapper<SkuInfo>().eq(SkuInfo::getId, skuId));
    }
    @Transactional
    @Override
    public void updatePublish(Long skuId, Integer status) {
        if (status == 1) {
            SkuInfo skuInfo = baseMapper.selectById(skuId);
            skuInfo.setId(skuId);
            skuInfo.setPublishStatus(1);
            baseMapper.update(skuInfo, new LambdaQueryWrapper<SkuInfo>().eq(SkuInfo::getId, skuId));
            //商品上架 后续会完善：发送mq消息更新es数据
            rabbitService.sendMessage(MqConst.EXCHANGE_GOODS_DIRECT, MqConst.ROUTING_GOODS_UPPER, skuId);
        } else {
            SkuInfo skuInfo = baseMapper.selectById(skuId);
            skuInfo.setId(skuId);
            skuInfo.setPublishStatus(0);
            baseMapper.update(skuInfo, new LambdaQueryWrapper<SkuInfo>().eq(SkuInfo::getId, skuId));
            rabbitService.sendMessage(MqConst.EXCHANGE_GOODS_DIRECT, MqConst.ROUTING_GOODS_LOWER, skuId);
        }
    }

    @Override
    public void updateCheck(Long skuId, Integer status) {
        SkuInfo skuInfo = baseMapper.selectById(skuId);
        skuInfo.setCheckStatus(status);
        baseMapper.update(skuInfo, new LambdaQueryWrapper<SkuInfo>().eq(SkuInfo::getId, skuId));

    }

    @Override
    public List<SkuInfo> findSkuInfoByKeyword(String keyword) {
        LambdaQueryWrapper<SkuInfo> wrapper = new LambdaQueryWrapper<>();
        wrapper.like(SkuInfo::getSkuName, keyword);
        List<SkuInfo> skuInfoList = baseMapper.selectList(wrapper);
        return skuInfoList;
    }

    @Override
    public List<SkuInfo> findNerPersonActivity() {
       //isNewPerson 1 新人专享
       //publishStatus 1 上架
       //页面显示三条数据
        LambdaQueryWrapper<SkuInfo> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(SkuInfo::getIsNewPerson, 1);
        wrapper.eq(SkuInfo::getPublishStatus, 1).orderByDesc(SkuInfo::getStock);
        Page<SkuInfo> page = new Page<>(1, 3);
        Page<SkuInfo> skuInfoPage = baseMapper.selectPage(page, wrapper);
        List<SkuInfo> records = skuInfoPage.getRecords();
        return records;

    }

    @Override
    public Boolean checkAndLock(List<SkuStockLockVo> skuStockLockVos, String orderNo) {
        //判断skuStockLockVos是否为空
        if (CollectionUtils.isEmpty(skuStockLockVos)){
            throw  new SsyxException(ResultCodeEnum.DATA_ERROR);
        }
        //遍历得到每一个商品进行查询库存和锁库存
        skuStockLockVos.forEach(skuStockLockVo -> {
            this.checkLock(skuStockLockVo);
        });

        //有一个商品锁定失败的话，锁定的所有商品都被解锁
        boolean b = skuStockLockVos.stream().anyMatch(skuStockLockVo -> skuStockLockVo.getIsLock()==false);
        if (b){
            //锁定的所有商品都被解锁
            skuStockLockVos.stream().filter(SkuStockLockVo :: getIsLock)
                    .forEach(skuStockLockVo -> {
                        baseMapper.unlockStock(skuStockLockVo.getSkuId(),skuStockLockVo.getSkuNum());
                    });
            return false;
        };
        //如果商品都锁定成功的话，进行redis缓存，为了后面数据进行解锁
        redisTemplate.opsForValue().set(RedisConst.SROCK_INFO + orderNo,skuStockLockVos);
        return true;
    }

    private void checkLock(SkuStockLockVo skuStockLockVo) {
        RLock fairLock = redissonClient.getFairLock(RedisConst.SKUKEY_SUFFIX + skuStockLockVo.getSkuId());
        fairLock.lock();
        try {
            //验证库存
            SkuInfo skuInfo  = baseMapper.checkStock(skuStockLockVo.getSkuId(),skuStockLockVo.getSkuNum());
            if(skuInfo == null){
                skuStockLockVo.setIsLock(false);
                return;
            }
            //满足条件的话
         Integer row = baseMapper.lockStock(skuStockLockVo.getSkuId(),skuStockLockVo.getSkuNum());
            if (row == 1){
                //加锁成功
                skuStockLockVo.setIsLock(true);
            }
        }finally {
            fairLock.unlock();
        }
    }
}
