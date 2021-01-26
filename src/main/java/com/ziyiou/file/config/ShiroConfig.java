package com.ziyiou.file.config;

import org.apache.shiro.mgt.RememberMeManager;
import org.apache.shiro.realm.Realm;
import org.apache.shiro.spring.web.ShiroFilterFactoryBean;
import org.apache.shiro.web.mgt.CookieRememberMeManager;
import org.apache.shiro.web.mgt.DefaultWebSecurityManager;
import org.apache.shiro.web.servlet.SimpleCookie;
import org.apache.shiro.web.session.mgt.DefaultWebSessionManager;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.LinkedHashMap;
import java.util.Map;

@Configuration
public class ShiroConfig {

    @Bean
    public ShiroFilterFactoryBean shiroFilterFactoryBean(
            @Qualifier("defaultWebSecurityManager") DefaultWebSecurityManager manager) {

        ShiroFilterFactoryBean factoryBean = new ShiroFilterFactoryBean();
        factoryBean.setSecurityManager(manager);

        //设置登录页
        factoryBean.setLoginUrl("login");

        // 添加shiro内置过滤器
        Map<String, String> map = new LinkedHashMap<>();

        // 主页只能由管理员访问

        factoryBean.setFilterChainDefinitionMap(map);

        return factoryBean;
    }

    @Bean
    public DefaultWebSecurityManager defaultWebSecurityManager(@Qualifier("userRealm") Realm realm,
                                                               @Qualifier("defaultWebSessionManager")DefaultWebSessionManager sessionManager,
                                                               @Qualifier("cookieRememberMeManager")RememberMeManager rememberMeManager) {
        DefaultWebSecurityManager manager = new DefaultWebSecurityManager();
        manager.setRealm(realm);
        manager.setSessionManager(sessionManager);
        manager.setRememberMeManager(rememberMeManager);

        return manager;
    }

    @Bean
    public Realm userRealm() {
        return new UserRealm();
    }

    @Bean
    public DefaultWebSessionManager defaultWebSessionManager() {
        DefaultWebSessionManager sessionManager = new DefaultWebSessionManager();
        sessionManager.setSessionIdUrlRewritingEnabled(false);
        return sessionManager;
    }

    // 记住我
    @Bean
    public CookieRememberMeManager cookieRememberMeManager(@Qualifier("simpleCookie") SimpleCookie simpleCookie){
        CookieRememberMeManager cookieRememberMeManager = new CookieRememberMeManager();
        cookieRememberMeManager.setCookie(simpleCookie);
        return cookieRememberMeManager;
    }

    @Bean
    public SimpleCookie simpleCookie(){
        SimpleCookie rememberMe = new SimpleCookie("rememberMe");
        rememberMe.setHttpOnly(true);
        rememberMe.setMaxAge(10*24*3600);// 10天
        return rememberMe;
    }

}