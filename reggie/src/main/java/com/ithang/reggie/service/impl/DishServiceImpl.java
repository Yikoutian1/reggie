package com.ithang.reggie.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ithang.reggie.common.CustomException;
import com.ithang.reggie.dto.DishDto;
import com.ithang.reggie.entity.Dish;
import com.ithang.reggie.entity.DishFlavor;
import com.ithang.reggie.mapper.DishMapper;
import com.ithang.reggie.service.DishFlavorService;
import com.ithang.reggie.service.DishService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

/**
 * @ClassName DishServiceImpl
 * @Description 同时操作俩张表的操作实现类
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
     * 带事务回滚的
     */
    @Transactional(rollbackFor = Exception.class)
    public void saveWithFlavor(DishDto dishDto) {
        try {
            // 保存菜品的基本信息到菜品表dish
            this.save(dishDto);
            // 菜品id
            Long dishId = dishDto.getId();
            // 保存菜品的口味数据到菜品口味表dish_flavor
            // (从前面的集合中)saveBatch批量保存
            // dishFlavorService.saveBatch(dishDto.getFlavors());不能保存DishId
            // 菜品口味
            List<DishFlavor> flavors = dishDto.getFlavors();
            flavors.forEach(item -> item.setDishId(dishId));
            dishFlavorService.saveBatch(flavors);
        } catch (Exception e) {
            throw new RuntimeException("保存菜品失败", e);
        }
    }
    // 不带事务回滚的
    /*
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
    */


    public DishDto getByIdWithFlavor(Long id) {
        // 分为两步
        // 查询菜品基本信息,从dish表查
        Dish dish = this.getById(id);
        DishDto dishDto = new DishDto();
        // 对象拷贝
        BeanUtils.copyProperties(dish,dishDto);

        // 查询当前菜品的口味信息,dish_flavor
        LambdaQueryWrapper<DishFlavor> queryWrapper = new LambdaQueryWrapper<>();
        // 单独查,单独给dishDTO设置Flavors
        queryWrapper.eq(DishFlavor::getDishId,dish);

        List<DishFlavor> flavors = dishFlavorService.list(queryWrapper);
        dishDto.setFlavors(flavors);

        return dishDto;
    }
    @Transactional(rollbackFor = Exception.class)
    public void updateDishWithFlavor(DishDto dishDto) {
        try {
            // 更新dish表基本信息
            this.updateById(dishDto);
            // 清理当前菜品对应口味数据---dish_ flavor表的delete操作
            LambdaQueryWrapper<DishFlavor> queryWrapper = new LambdaQueryWrapper<>();
            // 根据dishID查询并删除
            queryWrapper.eq(DishFlavor::getDishId,dishDto.getId());

            dishFlavorService.remove(queryWrapper);
            // 添加当前提交过来的口味数据---dish_ flavor表的insert操作
            List<DishFlavor> flavors = dishDto.getFlavors();
            flavors.forEach(item->item.setDishId(dishDto.getId()));
            // 这个问题跟当时新增一模一样,上面的saveWithFlavor()函数
            dishFlavorService.saveBatch(flavors);
        } catch (Exception e) {
            throw new RuntimeException("保存失败", e);
        }
    }

    /**
     * 菜品口味的删除
     * @param ids
     */
    @Transactional(rollbackFor = Exception.class)
    @Override
    public void deleteByIds(List<Long> ids) {
        try {
            LambdaQueryWrapper<Dish> queryWrapper = new LambdaQueryWrapper<>();
            queryWrapper.in(ids!=null,Dish::getId,ids);
            List<Dish> list = this.list(queryWrapper);
            list.forEach((item)->{
                Integer status = item.getStatus();
                if(status == 0){
                    // 如果不在售卖
                    this.removeById(item);
                }else{
                    throw new CustomException("删除菜品中有正在售卖菜品,无法全部删除");
                }
            });
        } catch (Exception e) {
            throw new RuntimeException("保存失败", e);
        }
    }
}
