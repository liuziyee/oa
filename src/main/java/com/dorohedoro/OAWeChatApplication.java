package com.dorohedoro;

import cn.hutool.core.io.FileUtil;
import cn.hutool.core.util.StrUtil;
import com.dorohedoro.config.Constants;
import com.dorohedoro.config.Properties;
import com.dorohedoro.domain.SysConfig;
import com.dorohedoro.mapper.SysConfigMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.ServletComponentScan;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.scheduling.annotation.EnableAsync;

import javax.annotation.PostConstruct;
import java.lang.reflect.Field;
import java.util.List;

import static java.util.stream.Collectors.toSet;

@Slf4j
@EnableAsync
@EnableFeignClients
@SpringBootApplication
@MapperScan("com.dorohedoro.mapper")
@ServletComponentScan
@RequiredArgsConstructor
public class OAWeChatApplication {

    private final SysConfigMapper sysConfigMapper;
    private final Properties properties;
    
    public static void main(String[] args) {
        SpringApplication.run(OAWeChatApplication.class, args);
    }
    
    @PostConstruct
    public void doInit() {
        log.info("读取SYS_CONFIG表");
        List<SysConfig> configs = sysConfigMapper.selectAll();
        configs.stream().peek(config -> {
            String key = StrUtil.toCamelCase(config.getParamKey());
            try {
                Field field = Constants.class.getDeclaredField(key);
                field.set(null, config.getParamValue());
            } catch (Throwable e) {
                log.error(e.getMessage(), e);
            }
        }).collect(toSet());

        log.info("创建本地文件夹,用于存储签到照片");
        FileUtil.mkdir(properties.getImgDir());
    }
}
