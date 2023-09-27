package com.yutian.controller;

import com.yutian.service.impl.UserServiceImpl;
import org.springframework.annotation.Autowired;
import org.springframework.annotation.Controller;

/**
 * @author PoemsAndDreams
 * @date 2023-09-27 07:49
 */
@Controller(value = "uc")
public class UserController {
    @Autowired
    UserServiceImpl userService;

    public void test(){
        userService.test();
    }
}
