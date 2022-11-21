package com.dorohedoro.config;

import com.rabbitmq.client.ConnectionFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitConfig {
    
    @Bean
    public ConnectionFactory factory() {
        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost("192.168.1.4");
        factory.setPort(5672);
        return factory;
    }
}
