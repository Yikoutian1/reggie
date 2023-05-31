package com.ithang.reggie.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ithang.reggie.common.CustomException;
import com.ithang.reggie.dto.SetmealDto;
import com.ithang.reggie.entity.Setmeal;
import com.ithang.reggie.entity.SetmealDish;
import com.ithang.reggie.mapper.SetmealMapper;
import com.ithang.reggie.service.SetmealDishService;
import com.ithang.reggie.service.SetmealService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;


/**
 * @ClassName SetmealServiceImpl
 * @Description TODO
 * @Author QiuLiHang
 * @DATE 2023/5/21 22:23
 * @Version 1.0
 */
@Service
@Slf4j
public class SetmealServiceImpl extends ServiceImpl<SetmealMapper, Setmeal> implements SetmealService {
    @Autowired
    private SetmealDishService setmealDishService;
    /**
     * 新增套餐同时保存套餐与菜品的关联关系
     * @param setmealDto
     */
    //@Transactional(rollbackFor = Exception.class)
    public void saveWithDish(SetmealDto setmealDto) {
        // 保存套餐的基本信息，操作setmeal,执行insert操作
        this.save(setmealDto);
        // 获取菜品套餐关联信息集合，此时还要setmealID没有保存进来
        List<SetmealDish> setmealDishes = setmealDto.getSetmealDishes();
        setmealDishes.forEach(item-> item.setSetmealId(setmealDto.getId()));
        // 保存套餐和菜品的关联信息，操作setmeal_dish, 执行insert操作
        setmealDishService.saveBatch(setmealDishes);
    }

    /**
     * 删除套餐，同时需要删除套餐和菜品的关联数据
     * @param ids
     */
    @Transactional(rollbackFor = Exception.class)
    public void removeWithDish(List<Long> ids) {
        try {
            // select count(*) from setmeal where id in (1,2,3) and status = 1
            LambdaQueryWrapper<Setmeal> queryWrapper = new LambdaQueryWrapper<>();
            // 查询状态是否能删除
            queryWrapper.in(Setmeal::getId,ids);
            queryWrapper.eq(Setmeal::getStatus,1);


            int count = (int)this.count(queryWrapper);
            if(count > 0){
                // 如果不能删除，抛出一个业务异常
                throw new CustomException("套餐正在售卖中，不能删除");
            }
            // 如果可以删除，先删除套餐表中的数据 -- setmeal
            this.removeByIds(ids);
            // 删除关系表中的数据 -- setmeal_dish
            LambdaQueryWrapper<SetmealDish> wrapper = new LambdaQueryWrapper<>();
            // 删除对应关系表的数据 -- setmeal_dish
            // delete from setmeal_dish where setmeal_id in (1,2,3)
            wrapper.in(SetmealDish::getSetmealId,ids);
            setmealDishService.remove(wrapper);
        }catch (Exception e){
            throw new RuntimeException("删除失败", e);
        }
    }
}
