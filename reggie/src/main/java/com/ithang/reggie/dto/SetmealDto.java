package com.ithang.reggie.dto;


import com.ithang.reggie.entity.Setmeal;
import com.ithang.reggie.entity.SetmealDish;
import lombok.Data;
import java.util.List;

@Data
public class SetmealDto extends Setmeal {

    private List<SetmealDish> setmealDishes;

    private String categoryName;
}
