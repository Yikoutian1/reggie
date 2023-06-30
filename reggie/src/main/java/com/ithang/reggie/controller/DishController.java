package com.ithang.reggie.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.ithang.reggie.common.CustomException;
import com.ithang.reggie.common.Result;
import com.ithang.reggie.dto.DishDto;
import com.ithang.reggie.entity.Category;
import com.ithang.reggie.entity.Dish;
import com.ithang.reggie.entity.DishFlavor;
import com.ithang.reggie.service.CategoryService;
import com.ithang.reggie.service.DishFlavorService;
import com.ithang.reggie.service.DishService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeUnit;
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
    @Autowired
    private RedisTemplate redisTemplate;
    /**
     * 新增
     * 此时需要操作两张表
     * @param dishDto
     * @return
     */
    @PostMapping()
    public Result<String> save(@RequestBody DishDto dishDto){
        dishService.saveWithFlavor(dishDto);
        return Result.success("新增菜品成功");
    }
    @GetMapping("/page")
    public Result<Page> page(int page, int pageSize,String name) {
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
        return Result.success(dishDtoPage);
    }
    /**
     * 根据id查询菜品信息对应的口味信息
     * 需要回显页面数据    DTO包含了这些信息
     * @param id
     * @return
     */
    @GetMapping("/{id}")
    public Result<DishDto> getBack(@PathVariable Long id){
        DishDto dishDto= dishService.getByIdWithFlavor(id);
        return Result.success(dishDto);
    }

    /**
     * 菜品修改
     * @param dishDto
     * @return
     */
    @PutMapping()
    public Result<String> update(@RequestBody DishDto dishDto){
        dishService.updateDishWithFlavor(dishDto);

        // 清理所有菜品的缓存数据
        //Set keys = redisTemplate.keys("dish_*");
        // 只清理某个分类下面的缓存
        String key = "dish_" + dishDto.getCategoryId() + "_" + "_1";
        redisTemplate.delete(key);

        return Result.success("保存成功");
    }
    // 旧,下面是新的，添加设置口味数据
    /*
    @GetMapping("/list")
    public Result<List<Dish>> list(Dish dish){ // 返回的是多个菜品,在添加菜品那里显示出来的,因为要遍历菜品,然后关联到菜品中
        // 构造查询条件
        LambdaQueryWrapper<Dish> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(dish.getCategoryId()!=null,Dish::getCategoryId,dish.getCategoryId());
        // 1 表示起售的菜品,查询status为1的菜品
        queryWrapper.eq(Dish::getStatus,1);
        // 添加排序条件
        queryWrapper.orderByAsc(Dish::getSort).orderByDesc(Dish::getUpdateTime);
        List<Dish> list = dishService.list(queryWrapper);

        return Result.success(list);
    }
    */
    @GetMapping("/list")
    public Result<List<DishDto>> list(Dish dish){ // 返回的是多个菜品,在添加菜品那里显示出来的,因为要遍历菜品,然后关联到菜品中
        List<DishDto> dishDtoList = null;
        // 动态构造key
        String key = "dish_" + dish.getCategoryId() + "_" + dish.getStatus();//dish_1413342269393674242_1

        //先从redis中获取缓存数据
        dishDtoList = (List<DishDto>)redisTemplate.opsForValue().get(key);
        if(dishDtoList!=null){
            //如果存在，直接返回，无需查询数据库
            return Result.success(dishDtoList);
        }
        //如果不存在,需要查询教斑库,将查询到的菜品数据缓存到redis

        // 构造查询条件
        LambdaQueryWrapper<Dish> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(dish.getCategoryId()!=null,Dish::getCategoryId,dish.getCategoryId());
        // 1 表示起售的菜品,查询status为1的菜品
        queryWrapper.eq(Dish::getStatus,1);
        // 添加排序条件
        queryWrapper.orderByAsc(Dish::getSort).orderByDesc(Dish::getUpdateTime);
        List<Dish> list = dishService.list(queryWrapper);

        dishDtoList = list.stream().map((item) -> {
            DishDto dishDto = new DishDto();
            BeanUtils.copyProperties(list, dishDto);
            Long id = item.getCategoryId();
            Category category = categoryService.getById(id);
            if (category != null) {
                String categoryName = category.getName();
                dishDto.setCategoryName(categoryName);
            }
            // 当前菜品的id
            Long dishId = item.getId();
            LambdaQueryWrapper<DishFlavor> wrapper = new LambdaQueryWrapper<>();
            wrapper.eq(DishFlavor::getDishId,dishId);
            // SQL:select * from dish_flavor where dish_id = ?
            List<DishFlavor> dishFlavorList = dishFlavorService.list(wrapper);
            dishDto.setFlavors(dishFlavorList);
            return dishDto;
        }).collect(Collectors.toList());

        // 如果不存在,则将查询到的数据缓存到redis里面(设置缓存时间10分钟)
        redisTemplate.opsForValue().set(key,dishDtoList,10, TimeUnit.MINUTES);
        return Result.success(dishDtoList);
    }
    @PostMapping("/status/{status}")
    public Result<String> status(@PathVariable("status") int status,@RequestParam("ids") List<Long> ids) {
        LambdaQueryWrapper<Dish> queryWrapper = new LambdaQueryWrapper<>();
        // 停用之前先查看是否有关联套餐
        queryWrapper.eq(Dish::getId,ids);
        long count = dishService.count(queryWrapper);
        if(count>0){
            throw new CustomException("当前有关联套餐,不能禁用");
        }
        ids.forEach((item)->{
            Dish dish = dishService.getById(item);
            dish.setStatus(status);
            dishService.updateById(dish);
        });
        return Result.success("状态改变成功");
    }
    @DeleteMapping()
    public Result<String> delete(@RequestParam("ids") List<Long> ids){
        // 删除菜品,调用直接编写的deleteByIds
        dishService.deleteByIds(ids);
        // 删除菜品口味
        LambdaQueryWrapper<DishFlavor> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.in(ids!=null,DishFlavor::getId,ids);
        dishFlavorService.remove(queryWrapper);
        return Result.success("删除成功");
    }
}
