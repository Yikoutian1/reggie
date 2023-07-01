package com.ithang.reggie.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;

import com.ithang.reggie.common.Result;
import com.ithang.reggie.entity.User;
import com.ithang.reggie.service.UserService;
import com.ithang.reggie.utils.EmailUtils;
import com.ithang.reggie.utils.ValidateCodeUtils;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;
import java.util.concurrent.TimeUnit;

@RestController
@RequestMapping("/user")
@Slf4j
public class UserController {

    @Autowired
    private UserService userService;
    @Autowired
    private RedisTemplate redisTemplate;
    /**
     * 发送手机短信验证码
     * @param user
     * @return
     */
    @PostMapping("/sendMsg")
    public Result<String> sendMsg(@RequestBody User user, HttpSession session){
        //获取手机号
        String phone = user.getPhone();

        if(StringUtils.isNotEmpty(phone)){
            //生成随机的4位验证码
            String code = ValidateCodeUtils.generateValidateCode(4).toString();
            log.info("code={}",code);

            //调用阿里云提供的短信服务API完成发送短信
            //SMSUtils.sendMessage("瑞吉外卖","",phone,code);
            //邮箱
            EmailUtils.sendAuthCodeEmail(phone,code);


            //需要将生成的验证码保存到Session
            //session.setAttribute(phone,code);
            //存入Redis中,设置有效期为五分钟
            redisTemplate.opsForValue().set(phone,code,5, TimeUnit.MINUTES);

            return Result.success("手机验证码短信发送成功");
        }

        return Result.error("短信发送失败");
    }

    /**
     * 手机号移动端用户登录
     * @param map
     * @param session
     * @return
     */
    /*
    @PostMapping("/login")
    public Result<User> login(@RequestBody Map map, HttpSession session){
        log.info(map.toString());

        //获取手机号
        String phone = map.get("phone").toString();

        //获取验证码
        String code = map.get("code").toString();

        //从Session中获取保存的验证码
        Object codeInSession = session.getAttribute(phone);

        //进行验证码的比对（页面提交的验证码和Session中保存的验证码比对）
        if(codeInSession != null && codeInSession.equals(code)){
            //如果能够比对成功，说明登录成功

            LambdaQueryWrapper<User> queryWrapper = new LambdaQueryWrapper<>();
            queryWrapper.eq(User::getPhone,phone);

            User user = userService.getOne(queryWrapper);
            if(user == null){
                //判断当前手机号对应的用户是否为新用户，如果是新用户就自动完成注册
                user = new User();
                user.setPhone(phone);
                user.setStatus(1);
                userService.save(user);
            }
            session.setAttribute("user",user.getId());
            return Result.success(user);
        }
        return Result.error("登录失败");
    }*/

    /**
     *  移动应用登录端
     * @param map
     * @param session
     * @return
     * @discrible 这里使用map来接收前端传过来的值
     */
    @PostMapping("/login")
    private Result<User> login(@RequestBody Map map, HttpSession session) {
        log.info(map.toString());

        // 使用map来接收参数,接收键值参数
        // 获取到手机号
        String phone = map.get("phone").toString();
        // 获取到验证码
        String code = map.get("code").toString();
        // 从Session中获取到保存的验证码
        // 将session中获取到的验证码和前端提交过来的验证码进行比较，这样就可以实现一个验证的方式
        // 比对页面提交的验证码和session中

        //获取session中phone字段对应的验证码
        //Object codeInSession = session.getAttribute(phone);

        // 从redis中获取验证码
        Object codeInSession = redisTemplate.opsForValue().get(phone);


        // 下面进行比对
        if (codeInSession != null && codeInSession.equals(code)) {
            LambdaQueryWrapper<User> userLambdaQueryWrapper = new LambdaQueryWrapper<>();
            // 在表中根据号码来查询是否存在该邮箱用户
            userLambdaQueryWrapper.eq(User::getPhone, phone);
            User user = userService.getOne(userLambdaQueryWrapper);
            //判断当前的手机号在数据库查询是否有记录，如果没有记录，说明是一个新的用户，然后自动将这个手机号进行注册
            if(user == null){
                //判断当前手机号对应的用户是否为新用户，如果是新用户就自动完成注册
                user = new User();
                user.setPhone(phone);
                user.setStatus(1);
                // insert
                userService.save(user);
            }
            // 这里我们将user存储进去，后面各项操作，我们会用，其中拦截器那边会判断用户是否登录，所以我们将这个存储进去，
            session.setAttribute("user",user.getId());
            // 登录成功需要返回user,在浏览器需要保存一份


            // 如果登录成功,则删除redis缓存的验证码
            redisTemplate.delete(phone);


            return Result.success(user);
        }
        return Result.error("验证失败");
    }
    @PostMapping("/logout")
    public Result<String> logout(HttpServletRequest request){
        request.getSession().removeAttribute("User");
        return Result.success("退出登录");
    }
}
