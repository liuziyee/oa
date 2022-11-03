package com.dorohedoro.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Data
@ConfigurationProperties(prefix = "app")
public class AppProperties {
    
    private Jwt jwt = new Jwt();

    @Data
    public static class Jwt {
        private int expire = 5; // 过期时间(天)
        private int cacheExpire = 10; // 缓存过期时间(天)
    }
}
