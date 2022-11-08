package com.dorohedoro.config.shiro;

import lombok.extern.slf4j.Slf4j;
import org.apache.shiro.mgt.SecurityManager;
import org.apache.shiro.spring.LifecycleBeanPostProcessor;
import org.apache.shiro.spring.security.interceptor.AuthorizationAttributeSourceAdvisor;
import org.apache.shiro.spring.web.ShiroFilterFactoryBean;
import org.apache.shiro.web.mgt.DefaultWebSecurityManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.servlet.Filter;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

@Slf4j
@Configuration
public class ShiroConfig {

    @Bean
    public SecurityManager securityManager(DefaultRealm defaultRealm) {
        DefaultWebSecurityManager securityManager = new DefaultWebSecurityManager();
        securityManager.setRealm(defaultRealm);
        securityManager.setRememberMeManager(null);
        return securityManager;
    }

    @Bean
    public ShiroFilterFactoryBean shiroFilterFactoryBean(SecurityManager securityManager, JwtFilter jwtFilter) {
        ShiroFilterFactoryBean factoryBean = new ShiroFilterFactoryBean();
        factoryBean.setSecurityManager(securityManager);

        log.debug("这里不能用Map.of()创建Map,因为Map.of()得到的Map是不可变的,Shiro的源码会调用put(),就会抛出uoe异常");
        Map<String, Filter> filters = new HashMap<>() {};
        filters.put("jwt", jwtFilter);
        factoryBean.setFilters(filters);

        Map<String, String> map = new LinkedHashMap<>();
        log.debug("配置匿名访问路径");
        map.put("/webjars/**", "anon");
        map.put("/druid/**", "anon");
        map.put("/app/**", "anon");
        map.put("/sys/login", "anon");
        map.put("/swagger/**", "anon");
        map.put("/v2/api-docs", "anon");
        map.put("/swagger-ui.html", "anon");
        map.put("/swagger-resources/**", "anon");
        map.put("/doc.html", "anon");
        map.put("/captcha.jpg", "anon");
        map.put("/user/register", "anon");
        map.put("/user/login", "anon");
        map.put("/demo/**", "anon");
        map.put("/meeting/receiveNotify", "anon");
        map.put("/**", "jwt");
        factoryBean.setFilterChainDefinitionMap(map);

        return factoryBean;
    }
    
    @Bean
    public LifecycleBeanPostProcessor lifecycleBeanPostProcessor() {
        return new LifecycleBeanPostProcessor();
    }
    
    @Bean
    public AuthorizationAttributeSourceAdvisor authorizationAttributeSourceAdvisor(SecurityManager securityManager) {
        AuthorizationAttributeSourceAdvisor advisor = new AuthorizationAttributeSourceAdvisor();
        advisor.setSecurityManager(securityManager);
        return advisor;
    }
}
