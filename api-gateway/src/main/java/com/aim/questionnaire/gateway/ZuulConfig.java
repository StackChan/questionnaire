package com.aim.questionnaire.gateway;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.cloud.netflix.zuul.filters.ZuulProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

/**
 * @author csk
 * zuul网关配置类
 * nacos通过 @RefreshScope 自动刷新配置， zuul在nacos刷新时则需要更新自身配置，从而实现动态刷新网关路由
 */
@Configuration
public class ZuulConfig {
    @Bean(name="zuul.CONFIGURATION_PROPERTIES")
    @RefreshScope
    @ConfigurationProperties("zuul")
    @Primary
    public ZuulProperties zuulProperties() {
        return new ZuulProperties();
    }
}