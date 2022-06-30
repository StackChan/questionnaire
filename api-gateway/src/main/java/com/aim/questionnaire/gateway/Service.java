package com.aim.questionnaire.gateway;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author csk
 * 该Service暴露/configs接口,用于显示部分zuul在nacos中心上的配置
 * 用于验证nacos配置中心功能的实现
 * zuul网关的配置来自与nacos配置中心,方便管理
 * ZuulConfig实现了zuul网关配置在nacos配置中心api-gateway.yaml更改时,动态更新配置(热刷新)
 */
//@RefreshScope搭配@Value使用,配置要刷新的范围
@RefreshScope
@RestController
public class Service {

//    通过value注解读取配置信息
//    zuul:
//    routes:
//    application1:
//    stripPrefix: false
//    path: /application1/**
    @Value("${zuul.routes.project.path}")
    private String projectConfig;

    @Value("${zuul.routes.user.path}")
    private String userConfig;

    @GetMapping("/configs")
    public String getConfigs(){
        //读取配置信息
//        return config1;
        System.out.println(projectConfig+userConfig);
        return projectConfig+userConfig;
    }
}
