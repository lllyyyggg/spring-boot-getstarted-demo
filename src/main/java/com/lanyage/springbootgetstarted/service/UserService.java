package com.lanyage.springbootgetstarted.service;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.lanyage.springbootgetstarted.bean.User;
import com.lanyage.springbootgetstarted.mapper.UserMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@CacheConfig(cacheNames = "users", cacheManager = "redisManager")
public class UserService {

    @Autowired
    private UserMapper userMapper;

    @Cacheable(value = "users")
    public PageInfo<User> loadUsers(int page, int size) {
        PageHelper.startPage(page, size);   //设置起始页和每页大小
        List<User> users = userMapper.list();
        PageInfo<User> userPageInfo = new PageInfo<>(users);    //封装成为pageInfo
        return userPageInfo;
    }

    @Cacheable(value = "users", key = "#username"/*, condition = "#username.equals('lanyage')"*/)//先查缓存，如果没有就查数据库
    public User findOne(String username) {
        User user = userMapper.findByUsername(username);
        return user;
    }

    @CachePut(value = "users", key = "#user.username")
    public User save(User user) {
        userMapper.save(user);
        return user;
    }

}
