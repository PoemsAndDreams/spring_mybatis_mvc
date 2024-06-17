package com.dreams.controller;

import com.dreams.pojo.User;
import com.dreams.service.impl.UserServiceImpl;
import com.dreams.springframework.stereotype.Autowired;
import com.dreams.springframework.stereotype.Controller;
import com.dreams.springframework.web.bind.annotation.RequestMapping;
import com.dreams.springframework.web.bind.annotation.RequestParam;

/**
 * @author PoemsAndDreams
 */
@Controller
@RequestMapping("/list")
public class UserController {
    @Autowired
    UserServiceImpl userService;


    @RequestMapping("/list")
    public void test(User user){
        userService.test();
    }

    @RequestMapping("/add")
    public void insert(@RequestParam("name") String userName, @RequestParam("id") Integer id){
        System.out.println("username : " + userName);
        System.out.println("id :" + id);
        userService.test();
    }
}
