package com.ithang.reggie.filter;

import com.alibaba.fastjson.JSON;
import com.ithang.reggie.common.BaseContext;
import com.ithang.reggie.common.Result;
import jakarta.servlet.*;
import jakarta.servlet.annotation.WebFilter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.AntPathMatcher;

import java.io.IOException;


/**
 * @ClassName LoginCheckFilter
 * @Description 检查用户是否完成登录过滤器
 * @Author QiuLiHang
 * @DATE 2023/5/12 20:11
 * @Version 1.0
 */
@Slf4j
@WebFilter(filterName = "LoginCheckFilter",urlPatterns = "/*")
public class LoginCheckFilter implements Filter {
    // 路径匹配器,支持通配符写法
    public static final AntPathMatcher PATH_MATCHER = new AntPathMatcher();

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        HttpServletRequest request = (HttpServletRequest) servletRequest;
        HttpServletResponse response = (HttpServletResponse) servletResponse;

        // 1、获取本次请求的URI()
        // URI ：Uniform Resource Identifier，统一资源标识符；
        // URL：Uniform Resource Locator，统一资源定位符；
        String requestURI = request.getRequestURI();
        log.info("拦截到请求: {}",requestURI);
        // 放行路径,不需要处理的请求路径
        String[] urls = new String[]{
                "/employee/login",
                "/employee/logout",
                "/backend/**",
                "/front/**",
                "/common/**",
                "/user/sendMsg",
                "/user/login"
        };

        // 2、判断本次请求是否需要处理。
        boolean check = check(urls, requestURI);

        // 3、如果不需要处理，则直接放行。
        if(check){
            log.info("本次请求{}不需要处理",requestURI);
            filterChain.doFilter(request,response);
            return;
        }

        // 4.1、判断登录状态，如果已登录，则直接放行。
        // 从Session里面取值
        if(request.getSession().getAttribute("employee") != null){
            log.info("用户已登录,用户id为: {}",request.getSession().getAttribute("employee"));
            // 设置登录用户id，动态填充
            Long empID  = (Long) request.getSession().getAttribute("employee");
            BaseContext.setCurrentId(empID);

            filterChain.doFilter(request,response);
            return;
        }
        // 4.2、判断登录状态(移动)，如果已登录，则直接放行。
        // 从Session里面取值
        if(request.getSession().getAttribute("user") != null){
            log.info("用户已登录,用户id为: {}",request.getSession().getAttribute("user"));
            // 设置登录用户id，动态填充
            Long userId  = (Long) request.getSession().getAttribute("user");
            BaseContext.setCurrentId(userId);
            filterChain.doFilter(request,response);
            return;
        }
        log.info("用户未登录");
        // 5、如果未登录则返回未登录结果,通过输出流方式向客户端页面响应数据
        response.getWriter().write(JSON.toJSONString(Result.error("NOTLOGIN")));
        return;
    }

    /**
     * 路径匹配，检查本次请求是否需要放行
     * @param requestURI
     * @param urls
     * @return
     */
    public boolean check(String[] urls,String requestURI){
        for (String url:urls) {
            boolean match = PATH_MATCHER.match(url, requestURI);
            if(match) {
                return true;
            }
        }
        return false;
    }
}
