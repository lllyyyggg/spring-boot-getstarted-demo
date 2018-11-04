package com.lanyage.springbootgetstarted;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
//@EnableScheduling
@EnableAspectJAutoProxy
public class SpringBootGetstartedApplication {

	public static void main(String[] args) {
		SpringApplication.run(SpringBootGetstartedApplication.class, args);
	}
}
