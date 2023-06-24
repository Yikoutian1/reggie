package com.ithang.reggie.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.ithang.reggie.common.Result;
import com.ithang.reggie.entity.Category;
import com.ithang.reggie.entity.Employee;
import com.ithang.reggie.service.EmployeeService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.DigestUtils;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;

/**
 * @ClassName EmployeeController
 * @Description TODO
 * @Author QiuLiHang
 * @DATE 2023/5/11 20:23
 * @Version 1.0
 */
@Slf4j
@RestController
@RequestMapping("/employee")
public class EmployeeController {
    @Autowired
    private EmployeeService employeeService;

    /**
     * 员工登录
     * @param request
     * @param employee
     * @return
     */
    @PostMapping("/login")
    public Result<Employee> login(HttpServletRequest request
            ,@RequestBody Employee employee){
        // 1、将页面提交的密码password进行md5加密处理
        String password = employee.getPassword();
        password = DigestUtils.md5DigestAsHex(password.getBytes());

        // 2、根据页面提交的用户名username查询数据库
        LambdaQueryWrapper<Employee> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Employee::getUsername,employee.getUsername());
        // user在数据库是unique
        Employee emp = employeeService.getOne(wrapper);

        // 3、如果没有查询到则返回登录失败结果
        if(emp==null){
            return Result.error("登录失败");
        }

        // 4、密码比对，如果不一-致则返回登录失败结果
        if(!emp.getPassword().equals(password)){
            return Result.error("登录失败");
        }

        // 5、查看员工状态，如果为已禁用状态，则返回员工已禁用结果
        if(emp.getStatus() == 0){
            return Result.error("账号已禁用");
        }

        // 6、登录成功，将员Tid存入Session并返回登录成功结果
        request.getSession().setAttribute("employee",emp.getId());
        return Result.success(emp);
    }

    /**
     * 员工退出
     * @return
     */
    @PostMapping("/logout")
    public Result<String> logout(HttpServletRequest request){
        // 清理Session中保存的当前登录员工的id
        request.getSession().removeAttribute("employee");
        return Result.success("退出成功");
    }

    /**
     * 新增员工
     * @param employee
     * @return
     */
    @PostMapping()
    public Result<String> save(HttpServletRequest request,@RequestBody Employee employee){
        // 设置初始密码,md5加密处理
        employee.setPassword(DigestUtils.md5DigestAsHex("123456".getBytes()));
//------------------------mybatis-plus自动填充------------------------------
//        employee.setCreateTime(LocalDateTime.now());
//        employee.setUpdateTime(LocalDateTime.now());
//        // 获取当前登录用户id
//        Long empId = (Long) request.getSession().getAttribute("employee");
//        employee.setCreateUser(empId);
//
//        employee.setUpdateUser(empId);
//----------------------------------------------------------------------
        // 此时的save为IService中的,mybatis-plus里面的
        employeeService.save(employee);
        return Result.success("新增员工成功");
    }

    /**
     * 员工信息的分页查询
     * @param page
     * @param pageSize
     * @param name
     * @return
     */
    @GetMapping("/page")
    public Result<Page> page(int page,int pageSize,String name) {// Page泛型是mybatisPlus里面的分页泛型,其中name为搜索框框里面的
        log.info("page = {},pageSize = {},name = {}", page, pageSize, name);
        // 构造分页构造器
        Page pageInfo = new Page(page, pageSize);

        // 构造条件构造器
        LambdaQueryWrapper<Employee> queryWrapper = new LambdaQueryWrapper<>();
        // 添加一个过滤条件
        //                  if(name!=null)
        queryWrapper.like(StringUtils.isNotEmpty(name), Employee::getName, name);
        // 添加排序条件
        queryWrapper.orderByDesc(Employee::getUpdateTime);
        // 执行查询
        employeeService.page(pageInfo, queryWrapper);
        return Result.success(pageInfo);
    }

    /**
     * 通用update方法,根据id修改员工信息
     * @param request
     * @return
     */
    @PutMapping()
    public Result<String> update(HttpServletRequest request,@RequestBody Employee employee){

//        // 获取当前登录用户id (Long强转精度丢失)
//        Long empId = (Long) request.getSession().getAttribute("employee");
//        employee.setUpdateTime(LocalDateTime.now());
//        employee.setUpdateUser(empId);
        log.info(employee.toString());
        long id = Thread.currentThread().getId();
        log.info("线程id为：{}",id);

        // 这里使用的是mybatisPlus里面的sql
        employeeService.updateById(employee);

        return Result.success("用户信息更新成功");
    }

    /**
     *  根据id查询信息
     * @param id  通过{id}获取页面返回的数据,@并且用PathVariable修饰
     * @return
     */
    @GetMapping("/{id}")
    public Result<Employee> getById(@PathVariable Long id){
        Employee employee = employeeService.getById(id);
        if(employee!=null&&employee.getStatus()!=0) {
            return Result.success(employee);
        }
        return Result.error("错误的操作");
    }

}
