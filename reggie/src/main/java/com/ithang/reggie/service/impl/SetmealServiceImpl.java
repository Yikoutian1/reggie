package com.ithang.reggie.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ithang.reggie.dto.SetmealDto;
import com.ithang.reggie.entity.Setmeal;
import com.ithang.reggie.mapper.SetmealMapper;
import com.ithang.reggie.service.SetmealService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Set;

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

    /**
     * 新增套餐同时保存套餐与菜品的关联关系
     * @param setmealDto
     */
    //@Transactional(rollbackFor = Exception.class)
    public void saveWithDish(SetmealDto setmealDto) {
        // 保存套餐的基本信息，操作setmeal,执行insert操作
        this.save(setmealDto);
        // 保存套餐和菜品的关联信息，操作setmeal_dish, 执行insert操作
        
    }
}
