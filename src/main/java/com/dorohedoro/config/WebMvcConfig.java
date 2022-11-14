package com.dorohedoro.config;

import cn.hutool.core.codec.Base64;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.RedisSerializer;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.security.KeyPair;
import java.security.KeyStore;
import java.security.PrivateKey;
import java.security.PublicKey;

@Slf4j
@Configuration
@EnableConfigurationProperties(Properties.class)
public class WebMvcConfig implements WebMvcConfigurer {

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        log.debug("配置knife4j");
        registry.addResourceHandler("doc.html").addResourceLocations("classpath:/META-INF/resources/");
        registry.addResourceHandler("/webjars/**").addResourceLocations("classpath:/META-INF/resources/webjars/");
    }

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**")
                .allowedOrigins("*")
                .allowedMethods("*")
                .allowedHeaders("*")
                .allowCredentials(true)
                .maxAge(3600);
    }

    @Bean
    public RedisTemplate<String, Object> redisTemplate(RedisConnectionFactory redisConnectionFactory) {
        RedisTemplate<String, Object> redisTemplate = new RedisTemplate();
        redisTemplate.setKeySerializer(RedisSerializer.string());
        redisTemplate.setValueSerializer(RedisSerializer.json());
        redisTemplate.setHashKeySerializer(RedisSerializer.string());
        redisTemplate.setHashValueSerializer(RedisSerializer.json());
        redisTemplate.setConnectionFactory(redisConnectionFactory);
        return redisTemplate;
    }
    
    @Bean
    public KeyPair keyPair() throws Throwable {
        String alias = "keypair";
        char[] password = "dorohedoro".toCharArray();

        ClassPathResource resource = new ClassPathResource("keypair.keystore");
        KeyStore keyStore = KeyStore.getInstance("JKS");
        keyStore.load(resource.getInputStream(), password);

        PrivateKey privateKey = (PrivateKey) keyStore.getKey(alias, password);
        PublicKey publicKey = keyStore.getCertificate(alias).getPublicKey();
        KeyPair keyPair = new KeyPair(publicKey, privateKey);

        log.info("读取keystore文件");
        log.info("公钥: {}, 私钥: {}", Base64.encode(keyPair.getPublic().getEncoded()), 
                Base64.encode(keyPair.getPrivate().getEncoded()));
        return keyPair;
    }
}
