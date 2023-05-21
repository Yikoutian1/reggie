package com.ithang.reggie.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.ithang.reggie.common.R;
import com.ithang.reggie.entity.Category;
import com.ithang.reggie.entity.Employee;
import com.ithang.reggie.service.CategoryService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;


/**
 * @ClassName CategoryController
 * @Description 分类管理
 * @Author QiuLiHang
 * @DATE 2023/5/15 17:10
 * @Version 1.0
 */
@Slf4j
@RestController
@RequestMapping("/category")
public class CategoryController {
    @Autowired
    private CategoryService categoryService;
    /**
     * 新增分类
     * @param category
     * @return
     */
    @PostMapping
    public R<String> save(@RequestBody Category category){
        categoryService.save(category);
        return R.success("添加成功");
    }
    /**
     * 分类信息的分页查询
     * @param page
     * @param pageSize
     * @return
     */
    @GetMapping("/page")
    public R<Page> page(int page, int pageSize) {
        // 构造分页构造器
        Page pageInfo = new Page(page, pageSize);
        // 构造条件构造器
        LambdaQueryWrapper<Category> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.orderByAsc(Category::getSort);
        // 执行查询
        categoryService.page(pageInfo, queryWrapper);
        return R.success(pageInfo);
    }
    /**
     * 分类更新
     * @param category
     * @return
     */
    @PutMapping()
    public R<String> update(@RequestBody Category category){
        categoryService.updateById(category);
        return R.success("更新数据成功");
    }

    /**
     * 分类删除
     * (传入id进行删除)
     * @param ids
     * @return
     */
    @DeleteMapping()
    public R<String> delete(Long ids){
        // 这里重写了removeById，在CategoryServiceImpl里面
        categoryService.removeById(ids);
        return R.success("删除数据成功");
    }
    @GetMapping("/list")
    public R<List<Category>> list(Category category){
        //条件构造器
        LambdaQueryWrapper<Category> queryWrapper = new LambdaQueryWrapper<>();
        //添加条件
        queryWrapper.eq(category.getType() != null,Category::getType,category.getType());
        //添加排序条件
        queryWrapper.orderByAsc(Category::getSort).orderByDesc(Category::getUpdateTime);
        List<Category> list = categoryService.list(queryWrapper);
        return R.success(list);
    }
}
