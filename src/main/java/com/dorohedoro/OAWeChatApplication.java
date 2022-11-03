package com.dorohedoro;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.ServletComponentScan;

@SpringBootApplication
@MapperScan("com.dorohedoro.mapper")
@ServletComponentScan
public class OAWeChatApplication {
    public static void main(String[] args) {
        SpringApplication.run(OAWeChatApplication.class, args);
    }
}
