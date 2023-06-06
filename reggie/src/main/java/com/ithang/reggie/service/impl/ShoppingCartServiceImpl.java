package com.ithang.reggie.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ithang.reggie.entity.ShoppingCart;
import com.ithang.reggie.mapper.ShoppingCartMapper;
import com.ithang.reggie.service.ShoppingCartService;
import org.springframework.stereotype.Service;

/**
 * @ClassName ShoppingCartServiceImpl
 * @Description TODO
 * @Author QiuLiHang
 * @DATE 2023/6/6 006 22:48
 * @Version 1.0
 */
@Service
public class ShoppingCartServiceImpl extends ServiceImpl<ShoppingCartMapper, ShoppingCart> implements ShoppingCartService {
}
