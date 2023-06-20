package com.ithang.reggie.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ithang.reggie.entity.Orders;
import com.ithang.reggie.mapper.OrdersMapper;
import com.ithang.reggie.service.OrdersService;
import org.springframework.stereotype.Service;

/**
 * @ClassName OrderServiceImpl
 * @Description TODO
 * @Author QiuLiHang
 * @DATE 2023/6/20 020 19:33
 * @Version 1.0
 */
@Service
public class OrderServiceImpl extends ServiceImpl<OrdersMapper, Orders> implements OrdersService {
}
