package com.ithang.reggie.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ithang.reggie.entity.Dish;
import com.ithang.reggie.mapper.DishMapper;
import com.ithang.reggie.service.DishService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * @ClassName DishServiceImpl
 * @Description TODO
 * @Author QiuLiHang
 * @DATE 2023/5/21 22:23
 * @Version 1.0
 */
@Service
@Slf4j
public class DishServiceImpl extends ServiceImpl<DishMapper, Dish> implements DishService {
}
