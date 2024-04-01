package com.atguigu.ssyx.product.service;

import com.atguigu.ssyx.model.product.Attr;
import com.baomidou.mybatisplus.extension.service.IService;
import org.springframework.stereotype.Repository;

import java.util.List;


public interface AttrService extends IService<Attr> {
    List<Attr> getAttrList(Long groupId);
}
