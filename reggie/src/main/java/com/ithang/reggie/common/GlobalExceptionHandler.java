package com.ithang.reggie.common;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import java.sql.SQLIntegrityConstraintViolationException;

/**
 * @ClassName GlobalExceptionHandler
 * @Description 全局异常处理
 * @Author QiuLiHang
 * @DATE 2023/5/12 21:39
 * @Version 1.0
 */
@Slf4j
// 拦截RestController类,普通的Control
@ControllerAdvice(annotations = {RestController.class, Controller.class})
// 返回JSON数据
@ResponseBody
public class GlobalExceptionHandler{
    /**
     * 异常处理方法,统一处理0
     * @return
     */
    @ExceptionHandler(SQLIntegrityConstraintViolationException.class)
    public Result<String> exceptionHandler(SQLIntegrityConstraintViolationException e){
        log.error(e.getMessage());
        // 唯一约束
        if(e.getMessage().contains("Duplicate entry")) {
            String[] split = e.getMessage().split(" ");
            String msg ="用户" + split[2] + "已存在";
            return Result.error(msg);
        }
        // 试图安抚人员
        return Result.error("服务器繁忙，请稍后再试");
    }
    /**
     * 异常处理方法(可以返回到前端异常信息)
     * @return
     */
    @ExceptionHandler(CustomException.class)
    public Result<String> exceptionHandler(CustomException e){
        log.error(e.getMessage());
        return Result.error(e.getMessage());
    }
}
