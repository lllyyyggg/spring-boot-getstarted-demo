package com.lanyage.springbootgetstarted.interceptor;

import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Component
public class LoginInterceptor implements HandlerInterceptor {
    private static final Logger logger = LoggerFactory.getLogger(LoginInterceptor.class);
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        logger.info("开始拦截[{}]", request.getRequestURL().toString());
        String method = request.getMethod();
        String token = request.getParameter("token");
        //System.out.println(method); //方法类型GET,POST
        //System.out.println(token);  //token
        //System.out.println(handler.getClass());
        return true;
    }
}
