package com.zut.lpf.lidou.controller;

import com.zut.lpf.lidou.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HelloController {

    @Autowired
    private UserService userService;
    @RequestMapping("/hello")
    public int hello()
    {
        if(userService==null)
            return 0;
        else
            return 1;
    }

}
