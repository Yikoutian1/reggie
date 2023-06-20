package com.ithang.reggie.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.ithang.reggie.entity.OrderDetail;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface OrdersDetailMapper extends BaseMapper<OrderDetail> {
}
