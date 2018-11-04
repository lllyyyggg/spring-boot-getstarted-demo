package com.lanyage.springbootgetstarted.interceptor;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebAppConfig implements WebMvcConfigurer {

    @Autowired
    private LoginInterceptor loginInterceptor;

    private static final Logger logger = LoggerFactory.getLogger(WebAppConfig.class);

    //配置拦截器
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        logger.info("[{}] has been created", "WebMvcConfigurer");
        registry.addInterceptor(loginInterceptor).addPathPatterns("/*");
    }
}
