package com.aim.questionnaire.controller;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RefreshScope
@RestController
@RequestMapping("/admin")
public class LoadBalanceTest {
    //server.port的值动态更新
    @Value("${server.port}")
    private String config;


    /**
     * @return 当前使用的微服务实例的使用的端口
     */
    @RequestMapping("/LoadBalanceTest")
    public String getConfigs(){
        //读取配置信息
//        return config1;
        System.out.println(config);
        return config;
    }
}