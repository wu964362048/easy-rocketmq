package com.mojiwu.easyrocketmq;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class EasyRocketMqApplication {



    public static void main(String[] args) {
        SpringApplication springApplication = new SpringApplication(EasyRocketMqApplication.class);
        springApplication.run(args);
    }

}
