package com.lanyage.springbootgetstarted.bean;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Component;

@Component
@PropertySource(value = "classpath:test.properties")
public class TestProperties {
    @Value("${test.username}")
    private String username;
    @Value("${test.password}")
    private String password;

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    @Override
    public String toString() {
        return "TestProperties{" +
                "username='" + username + '\'' +
                ", password='" + password + '\'' +
                '}';
    }
}
