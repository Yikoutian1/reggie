package com.ithang.reggie.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ithang.reggie.entity.OrderDetail;
import com.ithang.reggie.mapper.OrderDetailMapper;
import com.ithang.reggie.service.OrderDetailService;
import org.springframework.stereotype.Service;

/**
 * @ClassName OrdersDetailServiceImpl
 * @Description TODO
 * @Author QiuLiHang
 * @DATE 2023/6/20 020 19:25
 * @Version 1.0
 */
@Service
public class OrdersDetailServiceImpl extends ServiceImpl<OrderDetailMapper, OrderDetail> implements OrderDetailService {
}
