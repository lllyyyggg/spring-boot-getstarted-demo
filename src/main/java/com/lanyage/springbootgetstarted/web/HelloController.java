package com.lanyage.springbootgetstarted.web;

import com.lanyage.springbootgetstarted.bean.TestProperties;
import com.lanyage.springbootgetstarted.bean.User;
import com.lanyage.springbootgetstarted.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
public class HelloController {

    private static final Logger logger = LoggerFactory.getLogger(HelloController.class);

    @Autowired
    private TestProperties testUser;
    @Autowired
    private UserService userService;

    @GetMapping("/hello")
    public String hello() {
        //System.out.println(testUser);
        //ServletRequestAttributes servletRequestAttributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        //HttpServletRequest request = servletRequestAttributes.getRequest();
        //logger.info("URL : " + request.getRequestURL().toString());
        //logger.info("HTTP_METHOD : " + request.getMethod());
        //logger.info("IP : " + request.getRemoteAddr());
        return "Hello World";
    }

    @GetMapping("/users/{username}")
    public User find(@PathVariable("username") String username) {
        return userService.findOne(username);
    }

    @PutMapping("/users")
    public void save(@RequestBody User user) {
        userService.save(user);
    }

    @GetMapping("users")
    public Object users() {
        return userService.loadUsers(0, 2);
    }

}
