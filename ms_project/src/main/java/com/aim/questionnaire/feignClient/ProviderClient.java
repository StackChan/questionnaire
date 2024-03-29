package com.aim.questionnaire.feignClient;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.RequestMapping;


/**
 * @author csk
 * 为了实现Feign伪HTTP方式调用,创建代理
 * 在声明Feign客户端 之后，Feign会根据**@FeignClient**注解使用java的**动态代理技术生成代理类**，在这里我们
 * //指定@FeignClient value为user，则说明这个类的远程目标为spring cloud的服务名称为user的微服务。
 * Feign英文表意为“假装，伪装，变形”，此处正是将HTTP报文请求方式 伪装为简单的java接口(内部,未通过TTTP)调用方式
 */
@FeignClient(value = "user")
@RequestMapping("/admin")
public interface ProviderClient {

    @RequestMapping("/queryIdService")
    public String queryIdService(String createdBy);
}
