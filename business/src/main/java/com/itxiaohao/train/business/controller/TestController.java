package com.itxiaohao.train.business.controller;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @Author: itxiaohao
 * @date: 2023-09-16 17:57
 * @Description:
 */
@RestController
@RequestMapping("/hello")
@RefreshScope //配置中心生效的范围
public class TestController {
    @Value("${test.nacos}")
    private String nacos;
    @Value("${server.port}")
    private String port;
    @GetMapping("/sayHello")
    public String sayHello(){
        return "Hello " + nacos + port;
    }
}
