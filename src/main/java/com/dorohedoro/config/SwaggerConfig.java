package com.dorohedoro.config;

import io.swagger.annotations.ApiOperation;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.ApiKey;
import springfox.documentation.service.AuthorizationScope;
import springfox.documentation.service.SecurityReference;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spi.service.contexts.SecurityContext;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

import java.util.List;

@Configuration
@EnableSwagger2
public class SwaggerConfig {
    
    @Bean
    public Docket buildApi() {
        return new Docket(DocumentationType.SWAGGER_2)
                .apiInfo(new ApiInfoBuilder().title("OA微信小程序接口文档").build())
                .select()
                .apis(RequestHandlerSelectors.withMethodAnnotation(ApiOperation.class))
                .paths(PathSelectors.any())
                .build()
                .securitySchemes(List.of(new ApiKey("Authorization", "Authorization", "header"))) // 配置请求头
                .securityContexts(buildSecurityContexts());
    }

    private List<SecurityContext> buildSecurityContexts() {
        AuthorizationScope scope = new AuthorizationScope("global", "accessEverything");
        SecurityReference reference = new SecurityReference("Authorization", 
                new AuthorizationScope[]{scope});
        SecurityContext context = SecurityContext.builder().securityReferences(List.of(reference)).build();
        return List.of(context);
    }
}

