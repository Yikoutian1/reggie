package com.ithang.reggie.dto;

import com.ithang.reggie.entity.Dish;
import com.ithang.reggie.entity.DishFlavor;
import lombok.Data;
import java.util.ArrayList;
import java.util.List;

/**
 * 封装页面提交数据
 * DTO,全称为Data Transfer Object，即数据传输对象，一般用于展示层与服务层之间的数据传输。
 * 就比如在菜品管理中的添加菜品方法，它需要传的数据并不与实体类中的数据一一对应
 */
@Data
public class DishDto extends Dish { // 继承了Dish的属性
    /**
     * 菜品对应的口味数据
     * 其中还对应了DishFlavor JSON
     */
    private List<DishFlavor> flavors = new ArrayList<>();

    private String categoryName;

    private Integer copies;
}
