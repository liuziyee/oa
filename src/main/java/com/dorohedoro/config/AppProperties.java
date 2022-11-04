package com.dorohedoro.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Data
@ConfigurationProperties(prefix = "app")
public class AppProperties {
    
    private Jwt jwt = new Jwt();

    @Data
    public static class Jwt {
        private int expire = 5; // 访问令牌过期时间(天)
        private int cacheExpire = 10; // 缓存令牌过期时间(天)
    }
}
