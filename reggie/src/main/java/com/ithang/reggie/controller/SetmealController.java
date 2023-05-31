package com.ithang.reggie.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.ithang.reggie.common.R;
import com.ithang.reggie.dto.DishDto;
import com.ithang.reggie.dto.SetmealDto;
import com.ithang.reggie.entity.Category;
import com.ithang.reggie.entity.Dish;
import com.ithang.reggie.entity.Setmeal;
import com.ithang.reggie.service.CategoryService;
import com.ithang.reggie.service.SetmealDishService;
import com.ithang.reggie.service.SetmealService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

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
    @Autowired
    private CategoryService categoryService;
    /**
     * 新增套餐
     * @param setmealDto
     * @return
     */
    @PostMapping()
    public R<String> save(@RequestBody SetmealDto setmealDto){
        setmealService.saveWithDish(setmealDto);
        return R.success("添加套餐成功");
    }
    /**
     * 分页
     * 参考 DishController
     * @param page
     * @param pageSize
     * @param name
     * @return
     */
    @GetMapping("/page")
    public R<Page> page(int page,int pageSize,String name){
        // 分页构造器
        Page<Setmeal> pageInfo = new Page<>(page,pageSize);
        Page<SetmealDto> setmealDtoPage = new Page<>();
        LambdaQueryWrapper<Setmeal> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.like(name!=null,Setmeal::getName,name);
        queryWrapper.orderByDesc(Setmeal::getUpdateTime);
        setmealService.page(pageInfo,queryWrapper);

        BeanUtils.copyProperties(pageInfo,setmealDtoPage,"records");
        List<Setmeal> records = pageInfo.getRecords();
        // 重新定义一个带有所有需要的属性值的List集合
        List<SetmealDto> list = records.stream().map((item)->{
            SetmealDto setmealDto = new SetmealDto();
            // 拷贝其他属性(除CategoryName)普通属性
            BeanUtils.copyProperties(item,setmealDto);
            Long id = item.getCategoryId();
            // 根据id查询分类对象(category为分类，categoryName则为分类名称：如儿童套餐)
            Category category = categoryService.getById(id);
            if(category!=null){
                String categoryName = category.getName();
                setmealDto.setCategoryName(categoryName);
            }
            return setmealDto;
            // 通过collect里面的Collectors.toList()转成list集合返回
        }).collect(Collectors.toList());
        setmealDtoPage.setRecords(list);
        return R.success(setmealDtoPage);
    }

    /**
     * 根据ids删除套餐
     * @param ids
     * @return
     */
    @DeleteMapping()
    public R<String> delete(@RequestParam List<Long> ids){
        setmealService.removeWithDish(ids);
        return R.success("删除成功");
    }
}
