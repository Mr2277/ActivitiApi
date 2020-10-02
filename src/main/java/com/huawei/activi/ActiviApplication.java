package com.huawei.activi;

import org.activiti.spring.boot.SecurityAutoConfiguration;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(exclude = SecurityAutoConfiguration.class)
public class ActiviApplication {

    public static void main(String[] args) {
        SpringApplication.run(ActiviApplication.class, args);
    }

}
