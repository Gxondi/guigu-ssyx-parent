package com.atguigu.ssyx.search.service.Impl;

import com.atguigu.ssyx.client.order.ActivityFeignClient;
import com.atguigu.ssyx.client.order.ProductFeignClient;
import com.atguigu.ssyx.common.auth.AuthContextHolder;
import com.atguigu.ssyx.enums.SkuType;
import com.atguigu.ssyx.model.product.Category;
import com.atguigu.ssyx.model.product.SkuInfo;
import com.atguigu.ssyx.model.search.SkuEs;
import com.atguigu.ssyx.search.repository.SkuRepository;
import com.atguigu.ssyx.search.service.SkuApiService;
import com.atguigu.ssyx.vo.search.SkuEsQueryVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class SkuApiServiceImpl implements SkuApiService {
    @Autowired
    private ProductFeignClient productFeignClient;
    @Autowired
    private SkuRepository skuRepository;
    @Autowired
    private ActivityFeignClient activityFeignClient;
    @Autowired
    private RedisTemplate redisTemplate;
    @Override
    public void upperSku(Long skuId) {
        SkuEs skuEs = new SkuEs();
        SkuInfo skuInfo = productFeignClient.getSkuInfo(skuId);
        if (skuInfo == null) {
            return;
        }
        Category category = productFeignClient.getCategory(skuInfo.getCategoryId());
        if (category != null) {
            skuEs.setCategoryId(category.getId());
            skuEs.setCategoryName(category.getName());
        }
        skuEs.setId(skuInfo.getId());
        skuEs.setKeyword(skuInfo.getSkuName() + "," + skuEs.getCategoryName());
        skuEs.setWareId(skuInfo.getWareId());
        skuEs.setIsNewPerson(skuInfo.getIsNewPerson());
        skuEs.setImgUrl(skuInfo.getImgUrl());
        skuEs.setTitle(skuInfo.getSkuName());
        if (skuInfo.getSkuType() == SkuType.COMMON.getCode()) {
            skuEs.setSkuType(0);
            skuEs.setPrice(skuInfo.getPrice().doubleValue());
            skuEs.setStock(skuInfo.getStock());
            skuEs.setSale(skuInfo.getSale());
            skuEs.setPerLimit(skuInfo.getPerLimit());
        } else {
            //TODO 待完善-秒杀商品

        }
        SkuEs save = skuRepository.save(skuEs);

    }

    @Override
    public void lowerSku(Long skuId) {
        skuRepository.deleteById(skuId);
    }

    @Override
    public List<SkuEs> findHostSkuList() {
        Pageable pageable = PageRequest.of(0, 10);
        Page<SkuEs> skuEsPage = skuRepository.findByOrderByHotScoreDesc(pageable);
        List<SkuEs> content = skuEsPage.getContent();
        return content;
    }

    @Override
    public Page<SkuEs> search(Pageable pageable, SkuEsQueryVo skuEsQueryVo) {
        //设置当前的仓库id（从当前线程中取出来）
        skuEsQueryVo.setWareId(AuthContextHolder.getWareId());
        Page<SkuEs> page = null;
        //1.判断是否有关键字
        if(StringUtils.isEmpty(skuEsQueryVo.getKeyword())) {
            page = skuRepository.findByCategoryIdAndWareId(skuEsQueryVo.getCategoryId(), skuEsQueryVo.getWareId(), pageable);
        } else {
            page = skuRepository.findByKeywordAndWareId(skuEsQueryVo.getKeyword(), skuEsQueryVo.getWareId(), pageable);
        }
        //远程调用查询活动信息
        List<SkuEs> skuEsList = page.getContent();
        if (!CollectionUtils.isEmpty(skuEsList)){
            List<Long> skuIds = skuEsList.stream().map(item -> {
                return item.getId();
            }).collect(Collectors.toList());
            //远程调用查询活动信息
            //一个skuId对应可以对应多个活动规则
            System.out.println("skuIds = " + skuIds);
            Map<Long,List<String>> activityInfos = activityFeignClient.findActivity(skuIds);
            if (!CollectionUtils.isEmpty(activityInfos)){
                skuEsList.forEach(skuEs -> {
                    skuEs.setRuleList(activityInfos.get(skuEs.getId()));
                });
            }
        }
        return page;
    }

    @Override
    public void incrHotScore(Long skuId) {
        String hotKey = "hotScore";
        Double hotScore = redisTemplate.opsForZSet().incrementScore(hotKey, "skuId" + skuId, 1);
        if (hotScore % 10 == 0) {
            //更新es
            Optional<SkuEs> optional = skuRepository.findById(skuId);
            SkuEs skuEs = optional.get();
            skuEs.setHotScore(hotScore.longValue());
            skuRepository.save(skuEs);
        }
    }


//    @Override
//    public List<SkuInfo> findSkuInfoList(Long page, Long limit, SkuEs skuEs) {
//        Pageable pageable = PageRequest.of(Integer.parseInt(page.toString()), Integer.parseInt(limit.toString()));
//        Page<SkuInfo> skuEsPage = skuRepository.findByKeywordOrCategoryIdOrWareId(pageable);
//        List<SkuInfo> content = skuEsPage.getContent();
//        return content;
//    }

}
