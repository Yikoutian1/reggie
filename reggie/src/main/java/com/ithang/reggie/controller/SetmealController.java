package com.ithang.reggie.controller;

import com.ithang.reggie.common.R;
import com.ithang.reggie.dto.SetmealDto;
import com.ithang.reggie.service.SetmealDishService;
import com.ithang.reggie.service.SetmealService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @ClassName SetmealController
 * @Description TODO
 * @Author QiuLiHang
 * @DATE 2023/5/28 028 21:46
 * @Version 1.0
 */
@Slf4j
@RestController
@RequestMapping("/setmeal")
public class SetmealController {
    @Autowired
    private SetmealService setmealService;
    @Autowired
    private SetmealDishService setmealDishService;

    /**
     * 新增套餐
     * @param setmealDto
     * @return
     */
    @PostMapping()
    public R<String> save(@RequestBody SetmealDto setmealDto){

        return R.success("添加套餐成功");
    }
}
