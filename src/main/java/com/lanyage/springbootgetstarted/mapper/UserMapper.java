package com.lanyage.springbootgetstarted.mapper;

import com.lanyage.springbootgetstarted.bean.User;
import org.apache.ibatis.annotations.*;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;

@Mapper
public interface UserMapper {

    @Select("select * from sys_user")
    List<User> list();

    @Select("select * from sys_user where username = #{username}")
    User findByUsername(@Param("username") String username);

    @Insert("insert into sys_user(id, username, password) values(#{user.id},#{user.username},#{user.password})")
    void save(@Param("user") User user);

    @Update("update sys_user set password = #{user.password} where username = #{user.username}")
    void update(@Param("user") User user);
}
