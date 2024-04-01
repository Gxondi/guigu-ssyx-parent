package com.atguigu.ssyx.product.service;

import com.atguigu.ssyx.model.product.Category;
import com.atguigu.ssyx.vo.product.CategoryQueryVo;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import org.springframework.stereotype.Repository;


public interface CategoryService extends IService<Category> {
    IPage<Category> selectPage(Page<Category> pageParam, CategoryQueryVo categoryQueryVo);

    void updateCategoryById(Category category);
}
