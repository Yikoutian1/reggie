package com.ithang.reggie.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.ithang.reggie.common.BaseContext;
import com.ithang.reggie.common.Result;
import com.ithang.reggie.entity.ShoppingCart;
import com.ithang.reggie.service.ShoppingCartService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

/**
 * @ClassName ShoppingCartController
 * @Description TODO
 * @Author QiuLiHang
 * @DATE 2023/6/6 006 22:51
 * @Version 1.0
 */
@Slf4j
@RestController
@RequestMapping("/shoppingCart")
public class ShoppingCartController {
    @Autowired
    private ShoppingCartService shoppingCartService;

    /**
     * 添加购物车
     * @param shoppingCart
     * @return
     */
    @PostMapping("/add")
    public Result<ShoppingCart> add(@RequestBody ShoppingCart shoppingCart){
        // 设置当前购物车用户id,指定当前是哪个用户的购物车数据,从BaseContext中获取,或者从session里面获取都可以
        Long currentId = BaseContext.getCurrentId();
        shoppingCart.setUserId(currentId);
        // 查询当前菜品(dishId)或者套餐(setmealId)是否在购物车中
        Long dishId = shoppingCart.getDishId();

        LambdaQueryWrapper<ShoppingCart> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(ShoppingCart::getUserId,currentId);

        if(dishId != null) {
            //添加到购物车的是菜品
            queryWrapper.eq(ShoppingCart::getDishId,dishId);
        }else {
            //添加到购物车的是套餐
            queryWrapper.eq(ShoppingCart::getSetmealId,shoppingCart.getSetmealId());
        }
        // SQL:select * from shopping_cart where user_id = ? and dish_id/setmeal_id = ?        两个取一个
        ShoppingCart cartServiceOne = shoppingCartService.getOne(queryWrapper);// 查询当前菜品(dishId)或者套餐(setmealId)是否在购物车中
        // 能查出来
        if(cartServiceOne != null){
            //如果已经存在，就在原来数量基础上加一(update)
            Integer number = cartServiceOne.getNumber();
            cartServiceOne.setNumber(number+1);
            shoppingCartService.updateById(cartServiceOne);
        }else {
            //如果不存在，则添加到购物车，数量默认就是一
            shoppingCart.setNumber(1);
            shoppingCart.setCreateTime(LocalDateTime.now());// 设置入库时间
            shoppingCartService.save(shoppingCart);
            cartServiceOne = shoppingCart; // 传出去,然后返回
        }
        return Result.success(cartServiceOne);
    }
    /**
     * 减少购物车菜品/套餐的数量
     * @param shoppingCart
     * @return
     */
    @PostMapping("/sub")
    public Result<ShoppingCart> sub(@RequestBody ShoppingCart shoppingCart){

        log.info(shoppingCart.toString());
        Long userId = BaseContext.getCurrentId();
        Long dishId = shoppingCart.getDishId();
        //SQL : select * from shopping_cart where user_id = ? and dish_id/setmeal_id = ?
        LambdaQueryWrapper<ShoppingCart> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(ShoppingCart::getUserId,userId);

        if(dishId != null){
            //添加到购物车的是菜品
            queryWrapper.eq(ShoppingCart::getDishId,dishId);
        }else{
            //添加到购物车的是套餐
            queryWrapper.eq(ShoppingCart::getSetmealId,shoppingCart.getSetmealId());
        }

        ShoppingCart cartServiceOne = shoppingCartService.getOne(queryWrapper);

        if (cartServiceOne != null) {
            Integer number = cartServiceOne.getNumber();
            //如果数量大于 0 才能 - 1
            if (number > 0) {
                cartServiceOne.setNumber(number - 1);
                shoppingCartService.updateById(cartServiceOne);
                //如果数量再减少等于0 则 该商品/套餐 移出购物车
                if (number - 1 == 0) {
                    shoppingCartService.removeById(cartServiceOne.getId());
                }
            }
        }
        return Result.success(cartServiceOne);
    }
    @DeleteMapping("/clean")
    public Result<String> clean(){
        // SQL : delete from shopping_cart where user_id = ?
        Long userId = BaseContext.getCurrentId();
        // 判断是否登录
        if(userId == null){
            return Result.error("请先登录用户");
        }
        LambdaQueryWrapper<ShoppingCart> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(ShoppingCart::getUserId,userId);
        // removeById只能删除一条
        shoppingCartService.remove(queryWrapper);
        return Result.success("清除成功");
    }
    @GetMapping("/list")
    public Result<List<ShoppingCart>> list(){
        log.info("购物车查看...");
        LambdaQueryWrapper<ShoppingCart> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(ShoppingCart::getUserId,BaseContext.getCurrentId());
        queryWrapper.orderByAsc(ShoppingCart::getCreateTime);// 时间升序
        List<ShoppingCart> list = shoppingCartService.list(queryWrapper);
        return Result.success(list);
    }
}
