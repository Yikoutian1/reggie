package com.ithang.reggie.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ithang.reggie.entity.Category;
import com.ithang.reggie.mapper.CategoryMapper;
import com.ithang.reggie.service.CategoryService;
import org.springframework.stereotype.Service;

/**
 * @ClassName CategoryServiceImpl
 * @Description TODO
 * @Author QiuLiHang
 * @DATE 2023/5/15 17:08
 * @Version 1.0
 */
@Service
public class CategoryServiceImpl extends ServiceImpl<CategoryMapper, Category> implements CategoryService {
}
