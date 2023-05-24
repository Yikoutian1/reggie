package com.ithang.reggie.service.impl;

import com.baomidou.mybatisplus.extension.service.IService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ithang.reggie.entity.DishFlavor;
import com.ithang.reggie.mapper.DishFlavorMapper;
import com.ithang.reggie.mapper.DishMapper;
import com.ithang.reggie.service.DishFlavorService;
import com.ithang.reggie.service.DishService;
import org.springframework.stereotype.Service;

/**
 * @ClassName DishFlavorServiceImpl
 * @Description TODO
 * @Author QiuLiHang
 * @DATE 2023/5/23 21:40
 * @Version 1.0
 */
@Service
public class DishFlavorServiceImpl extends ServiceImpl<DishFlavorMapper, DishFlavor> implements DishFlavorService {
}
