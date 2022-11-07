package com.dorohedoro.config;

import feign.codec.Decoder;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.http.HttpMessageConverters;
import org.springframework.cloud.openfeign.support.SpringDecoder;
import org.springframework.context.annotation.Bean;
import org.springframework.http.MediaType;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;

import java.util.List;

@Slf4j
public class FeignConfig {
    
    @Bean
    public Decoder decoder() {
        log.debug("微信的code2Session接口返回的是JSON数据,格式为[text/plain],所以这里要自定义解码器,支持反序列化[text/plain]格式的数据");
        MappingJackson2HttpMessageConverter weChatConverter = new MappingJackson2HttpMessageConverter();
        weChatConverter.setSupportedMediaTypes(List.of(MediaType.TEXT_PLAIN));
        return new SpringDecoder(() -> new HttpMessageConverters(weChatConverter));
    }
}
