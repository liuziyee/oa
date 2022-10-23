package com.dorohedoro;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@MapperScan("com.dorohedoro.mapper")
public class OAWeChatApplication {
    public static void main(String[] args) {
        SpringApplication.run(OAWeChatApplication.class, args);
    }
}
