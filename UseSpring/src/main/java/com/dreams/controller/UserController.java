package com.dreams.controller;

import com.dreams.service.impl.UserServiceImpl;
import com.dreams.springframework.stereotype.Autowired;
import com.dreams.springframework.stereotype.Controller;

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
