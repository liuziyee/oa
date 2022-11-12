package com.dorohedoro;

import com.baomidou.mybatisplus.generator.FastAutoGenerator;
import com.baomidou.mybatisplus.generator.config.OutputFile;
import com.baomidou.mybatisplus.generator.engine.FreemarkerTemplateEngine;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.Collections;

@Slf4j
@SpringBootTest
@ExtendWith(SpringExtension.class)
public class MybatisPlusGenerator {
    
    @Test
    public void generate() {
        FastAutoGenerator.create(
                "jdbc:mysql://localhost:3306/oa?autoReconnect=true&useUnicode=true&characterEncoding=utf8&useSSL=false&serverTimezone=UTC",
                "root",
                "12345")
                .globalConfig(builder -> {
                    builder.author("liuziye")
                            .outputDir("C://mybatis-plus");
                })
                .packageConfig(builder -> {
                    builder.parent("com.dorohedoro")
                            .pathInfo(Collections.singletonMap(OutputFile.xml, "C://mybatis-plus//com//dorohedoro"));
                })
                .strategyConfig(builder -> builder.addInclude(
                        "sys_config", "user", "role", "permission", "module", "action", "checkin", 
                        "city", "dept", "face_model", "holiday", "meeting", "workday", "holiday"))
                .templateEngine(new FreemarkerTemplateEngine())
                .execute();
    }
}
