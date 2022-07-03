package com.aim.questionnaire.controller;

import com.aim.questionnaire.dao.entity.UserEntity;
import com.aim.questionnaire.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author csk
 */
@RequestMapping("/admin")
@RestController
public class ProviderController {

    @Autowired
    private UserService userService;

    private static final Logger LOG = LoggerFactory.getLogger(ProviderController.class);

    @RequestMapping("/queryIdService")
    public String queryIdService(String createdBy){
        LOG.info("provider invoke");

        System.out.println("provider invoke:project微服务通过feign调用了user微服务queryIdService方法");
        UserEntity userEntity = userService.queryByUserName(createdBy);
        String userId = userEntity.getId();
        return userId;
    }

}
