package com.lanyage.springbootgetstarted.service;

import com.github.pagehelper.PageInfo;
import com.lanyage.springbootgetstarted.bean.User;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cache.CacheManager;
import org.springframework.test.context.junit4.SpringRunner;

import static org.junit.Assert.*;

@RunWith(SpringRunner.class)
@SpringBootTest
public class UserServiceTest {

    @Autowired
    private UserService userService;

    @Test
    public void loadUsers() {
        PageInfo<User> userPageInfo = userService.loadUsers(2, 1);
        System.out.println(userPageInfo.getTotal());
        System.out.println(userPageInfo.getPages());

        for (User user : userPageInfo.getList()) {
            System.out.println(user);
        }
    }

    @Test
    public void findOne() {
        User user = userService.findOne("admin");
        System.out.println(user);
    }
}