package com.ithang.reggie.controller;

import com.ithang.reggie.service.OrderDetailService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @ClassName OrderDetailController
 * @Description 订单明细
 * @Author QiuLiHang
 * @DATE 2023/6/24 024 20:45
 * @Version 1.0
 */
@Slf4j
@RestController
@RequestMapping("/orderDetail")
public class OrderDetailController {
    @Autowired
    private OrderDetailService orderDetailService;
}
