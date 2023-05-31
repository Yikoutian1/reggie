package com.ithang.reggie.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.ithang.reggie.dto.SetmealDto;
import com.ithang.reggie.entity.Setmeal;

import java.util.List;

public interface SetmealService extends IService<Setmeal> {
    /**
     * 新增套餐同时保存套餐与菜品的关联关系
     * @param setmealDto
     */
    public void saveWithDish(SetmealDto setmealDto);
    void removeWithDish(List<Long> ids);
}
