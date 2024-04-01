package com.atguigu.ssyx.home.service;

import com.baomidou.mybatisplus.extension.service.IService;
import org.springframework.stereotype.Repository;

import java.util.Map;

@Repository
public interface HomeService {
    Map<String, Object> homeData(Long userId);
}
