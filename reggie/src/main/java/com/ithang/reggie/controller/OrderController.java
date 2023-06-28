package com.ithang.reggie.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.ithang.reggie.common.Result;
import com.ithang.reggie.entity.Orders;
import com.ithang.reggie.service.OrderService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * @ClassName OrderController
 * @Description TODO
 * @Author QiuLiHang
 * @DATE 2023/6/24 024 20:44
 * @Version 1.0
 */
@Slf4j
@RestController
@RequestMapping("/order")
public class OrderController {
    @Autowired
    private OrderService orderService;

    /**
     * 提交订单
     * @param orders
     * @return
     */
    @PostMapping("/submit")
    public Result<String> submit(@RequestBody Orders orders){
        log.info("订单数据：{} ",orders);
        orderService.submit(orders);
        return Result.success("下单成功");
    }
    /**
     * 后台显示订单信息
     */
    @GetMapping("/page")
    public Result<Page> page(int page, int pageSize, Long number, String beginTime, String endTime) {
        log.info("page={},pageSize={},number={},beginTime={},endTime={}",page,pageSize,number,beginTime,endTime);
        //分页构造器对象
        Page<Orders> pageInfo = new Page<>(page,pageSize);
        //构造条件查询对象
        LambdaQueryWrapper<Orders> queryWrapper = new LambdaQueryWrapper<>();
        //链式编程写查询条件
        queryWrapper.like(number!=null,Orders::getNumber,number)
                //前面加上判定条件是十分必要的，用户没有填写该数据，查询条件上就不添加它
                .gt(StringUtils.isNotBlank(beginTime),Orders::getOrderTime,beginTime)//大于起始时间
                .lt(StringUtils.isNotBlank(endTime),Orders::getOrderTime,endTime);//小于结束时间
        orderService.page(pageInfo,queryWrapper);
        return Result.success(pageInfo);
    }


}
