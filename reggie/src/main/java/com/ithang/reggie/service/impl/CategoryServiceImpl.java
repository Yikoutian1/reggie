package com.ithang.reggie.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ithang.reggie.common.CustomException;
import com.ithang.reggie.entity.Category;
import com.ithang.reggie.entity.Dish;
import com.ithang.reggie.entity.Setmeal;
import com.ithang.reggie.mapper.CategoryMapper;
import com.ithang.reggie.service.CategoryService;
import com.ithang.reggie.service.DishService;
import com.ithang.reggie.service.SetmealService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @ClassName CategoryServiceImpl
 * @Description TODO
 * @Author QiuLiHang
 * @DATE 2023/5/15 17:08
 * @Version 1.0
 */
@Service
@Slf4j
public class CategoryServiceImpl extends ServiceImpl<CategoryMapper, Category> implements CategoryService {

    @Autowired
    private DishService dishService;

    @Autowired
    private SetmealService setmealService;
    /**
     * 根据id删除分类，删除之前需要进行判断
     * @param ids
     */
    @Override
    public void removeById(Long ids){
        LambdaQueryWrapper<Dish> dishLambdaQueryWrapper = new LambdaQueryWrapper<>();
        // 添加查询条件，根据分类id进行查询
        dishLambdaQueryWrapper.eq(Dish::getCategoryId,ids);
        int countDish = (int) dishService.count(dishLambdaQueryWrapper);
        // 查询当前分类是否关联了菜品，如果已经关联，抛出一个业务异常
        if(countDish > 0){
            // 已经关联了菜品，如果已经关联，则抛出异常
            throw new CustomException("当前分类下关联了菜品，不能删除");
            //这个异常信息抛出，信息被全局异常给捕获了，然后返回到前端了

        }
        // 查询当前分类是否关联了套餐，如果已经关联，抛出一个业务异常
        LambdaQueryWrapper<Setmeal> setmealLambdaQueryWrapper = new LambdaQueryWrapper<>();
        // 添加查询条件，根据分类id进行查询
        setmealLambdaQueryWrapper.eq(Setmeal::getCategoryId,ids);
        int countSetmeal = (int) setmealService.count(setmealLambdaQueryWrapper);
        if(countSetmeal > 0){
            // 已经关联了菜品，如果已经关联，则抛出异常
            throw new CustomException("当前分类下关联了套餐，不能删除");
        }
        // 正常删除分类
        super.removeById(ids);
    }
}
