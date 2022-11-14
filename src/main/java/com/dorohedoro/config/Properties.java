package com.dorohedoro.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Data
@ConfigurationProperties(prefix = "app")
public class Properties {
    
    private Jwt jwt = new Jwt();

    private WeChat wechat = new WeChat();
    
    private Face face = new Face();

    private Mail mail = new Mail();

    private String imgDir;

    @Data
    public static class Jwt {
        private int expire = 5; // 访问令牌过期时间(天)
        private int cacheExpire = 10; // 缓存令牌过期时间(天)
    }
    
    @Data
    public static class WeChat {
        private String appid;
        private String appSecret;
    }

    @Data
    public static class Face {
        private String createUrl;
        private String checkinUrl;
    }

    @Data
    public static class Mail {
        private String system;
        private String hr;
    }
}
