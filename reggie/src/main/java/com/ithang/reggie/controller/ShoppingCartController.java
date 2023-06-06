package com.ithang.reggie.controller;

import com.ithang.reggie.service.ShoppingCartService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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

}
