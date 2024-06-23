package com.wenying;

import org.springframework.beans.factory.annotation.Configurable;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@Configurable
@EnableScheduling//可以被使用的注解 启动任务调度
public class Application {

    public static void main(String[] args){
        SpringApplication.run(Application.class);
    }

}
