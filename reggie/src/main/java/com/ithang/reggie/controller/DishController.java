package com.ithang.reggie.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.ithang.reggie.common.R;
import com.ithang.reggie.dto.DishDto;
import com.ithang.reggie.entity.Category;
import com.ithang.reggie.entity.Dish;
import com.ithang.reggie.service.CategoryService;
import com.ithang.reggie.service.DishFlavorService;
import com.ithang.reggie.service.DishService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

/**
 * @ClassName DishController
 * @Description 菜品管理
 * @Author QiuLiHang
 * @DATE 2023/5/21 23:28
 * @Version 1.0
 */
@Slf4j
@RestController
@RequestMapping("/dish")
public class DishController {
    @Autowired
    private DishService dishService;
    @Autowired
    private DishFlavorService dishFlavorService;
    @Autowired
    private CategoryService categoryService;
    /**
     * 新增
     * 此时需要操作两张表
     * @param dishDto
     * @return
     */
    @PostMapping()
    public R<String> save(@RequestBody DishDto dishDto){
        dishService.saveWithFlavor(dishDto);
        return R.success("新增菜品成功");
    }
    @GetMapping("/page")
    public R<Page> page(int page, int pageSize,String name) {
        // 构造分页构造器(如果用Dish则前端缺少一个返回值，菜品分类名字)
        Page<Dish> pageInfo = new Page<>(page,pageSize);
        Page<DishDto> dishDtoPage = new Page<>();

        // 构造条件构造器
        LambdaQueryWrapper<Dish> queryWrapper = new LambdaQueryWrapper<>();
        // 添加过滤条件
        queryWrapper.like(name!=null,Dish::getName,name);
        // 添加排序条件(更新时间降序排列)
        queryWrapper.orderByDesc(Dish::getUpdateTime);
        // 执行分页查询
        dishService.page(pageInfo,queryWrapper);

        // 通过对象拷贝，从Dish拷贝到DishDTO中，因为DishDTO继承了Dish属性,其中需要处理不需要处理的records,records是我们前面已经获取的数据
        BeanUtils.copyProperties(pageInfo,dishDtoPage,"records");
        List<Dish> records = pageInfo.getRecords();
        // 重新定义一个带有所有需要的属性值的List集合
        List<DishDto> list = records.stream().map((item)->{
            DishDto dishDto = new DishDto();
            // 拷贝其他属性(除CategoryName)普通属性
            BeanUtils.copyProperties(item,dishDto);

            Long categoryId = item.getCategoryId();
            // 根据id查询分类对象
            Category category = categoryService.getById(categoryId);
            // 把category不等于空先排除，如果能查上来 则获取name并且设置
            if(category != null) {
                // 单独获取名称
                String categoryName = category.getName();
                // 单独设置名称
                dishDto.setCategoryName(categoryName);
            }
            return dishDto;
        // 通过collect里面的Collectors.toList()转成list集合返回
        }).collect(Collectors.toList());

        dishDtoPage.setRecords(list);
        return R.success(dishDtoPage);
    }
    /**
     * 根据id查询菜品信息对应的口味信息
     * 需要回显页面数据    DTO包含了这些信息
     * @param id
     * @return
     */
    @GetMapping("/{id}")
    public R<DishDto> getBack(@PathVariable Long id){
        DishDto dishDto= dishService.getByIdWithFlavor(id);
        return R.success(dishDto);
    }

    /**
     * 菜品修改
     * @param dishDto
     * @return
     */
    @PutMapping()
    public R<String> update(@RequestBody DishDto dishDto){
        dishService.updateDishWithFlavor(dishDto);
        return R.success("保存成功");
    }
    @GetMapping("/list")
    public R<List<Dish>> list(Dish dish){ // 返回的是多个菜品,在添加菜品那里显示出来的,因为要遍历菜品,然后关联到菜品中
        // 构造查询条件
        LambdaQueryWrapper<Dish> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(dish.getCategoryId()!=null,Dish::getCategoryId,dish.getCategoryId());
        // 1 表示起售的菜品,查询status为1的菜品
        queryWrapper.eq(Dish::getStatus,1);
        // 添加排序条件
        queryWrapper.orderByAsc(Dish::getSort).orderByDesc(Dish::getUpdateTime);
        List<Dish> list = dishService.list(queryWrapper);

        return R.success(list);
    }
}
