package com.ithang.reggie.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ithang.reggie.entity.Employee;
import com.ithang.reggie.mapper.EmployeeMapper;
import com.ithang.reggie.service.EmployeeService;
import org.springframework.stereotype.Service;

/**
 * @ClassName EmployeeServiceImpl
 * @Description TODO
 * @Author QiuLiHang
 * @DATE 2023/5/11 20:20
 * @Version 1.0
 */
@Service
public class EmployeeServiceImpl
        // 实现父类，实现父接口
        extends ServiceImpl<EmployeeMapper,Employee>
        implements EmployeeService{

}
