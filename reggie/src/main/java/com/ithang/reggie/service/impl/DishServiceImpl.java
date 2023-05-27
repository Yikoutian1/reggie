package com.ithang.reggie.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ithang.reggie.dto.DishDto;
import com.ithang.reggie.entity.Dish;
import com.ithang.reggie.entity.DishFlavor;
import com.ithang.reggie.mapper.DishMapper;
import com.ithang.reggie.service.DishFlavorService;
import com.ithang.reggie.service.DishService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

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
    /**
     * 注入DishFlavorService
     */
    @Autowired
    private DishFlavorService dishFlavorService;

    /**
     * 新增菜品，同时保存对应口味数据
     * 需要控制多张表,需要开启事务支持@Transactional
     * @param dishDto
     */
    @Transactional
    public void saveWithFlavor(DishDto dishDto) {
        // 保存菜品的基本信息到菜品表dish
        this.save(dishDto);
        // 菜品id
        Long dishId = dishDto.getId();
        // 保存菜品的口味数据到菜品口味表dish_flavor
        // (从前面的集合中)saveBatch批量保存
        // dishFlavorService.saveBatch(dishDto.getFlavors());不能保存DishId
        // 菜品口味
        List<DishFlavor> flavors = dishDto.getFlavors();
//        法一:通过stream流遍历id集合
//        flavors.stream().map((item)->{
//            item.setDishId(dishId);
//            return item;
//        }).collect(Collectors.toList());
//        法二:
        flavors.forEach(item->item.setDishId(dishId));
        dishFlavorService.saveBatch(flavors);
    }
}
