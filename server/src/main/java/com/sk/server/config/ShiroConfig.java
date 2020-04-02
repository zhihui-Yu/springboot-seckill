package com.sk.server.config;

import com.sk.server.service.CustomRealm;
import org.apache.shiro.spring.web.ShiroFilterFactoryBean;
import org.apache.shiro.web.mgt.DefaultWebSecurityManager;
import org.apache.shiro.mgt.SecurityManager;
import org.apache.shiro.web.servlet.ShiroFilter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.HashMap;
import java.util.Map;

/**
 * @Author yzh
 * @Date 2020/4/2 21:38
 * @Version 1.0
 */
@Configuration
public class ShiroConfig {

    @Bean
    public CustomRealm customRealm(){
        return new CustomRealm();
    }

    //默认使用lang包下的
    //要自己导包 import org.apache.shiro.mgt.SecurityManager;
    @Bean
    public SecurityManager securityManager() {
        DefaultWebSecurityManager securityManager = new DefaultWebSecurityManager();
        securityManager.setRealm(customRealm());
        return securityManager;
    }

    @Bean
    public ShiroFilterFactoryBean shiroFilterFactoryBean(){
        ShiroFilterFactoryBean bean = new ShiroFilterFactoryBean();
        bean.setSecurityManager(securityManager());
        bean.setLoginUrl("to/login");
        bean.setUnauthorizedUrl("/unauth");

        //设置拦截路径 和 不拦截路径
        Map<String,String> filterChainDefinitionMap = new HashMap<>();
        filterChainDefinitionMap.put("/to/login","anon");
        filterChainDefinitionMap.put("/**","anon");
        filterChainDefinitionMap.put("/kill/execute/*","authc");
        filterChainDefinitionMap.put("/item/detail/*","authc");

        return bean;
    }
}
