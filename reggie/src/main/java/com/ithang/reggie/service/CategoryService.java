package com.ithang.reggie.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.ithang.reggie.entity.Category;

public interface CategoryService extends IService<Category> {
    public void removeById(Long id);
}
