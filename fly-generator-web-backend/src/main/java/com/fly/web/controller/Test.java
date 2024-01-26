package com.fly.web.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

@RequestMapping( "/test" )
@RestController
public class Test {

    /**
     * 测试极限情况空接口
     * @return
     */
    @GetMapping( "" )
    public String ok() {
        return "ok";
    }
}
