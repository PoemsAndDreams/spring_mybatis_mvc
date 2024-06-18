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


    @RequestMapping("/one")
    public User test(@RequestParam("id") Integer id){
        User user = userService.getOneUser(id);
        return user;
    }

    @RequestMapping("/add")
    public String insert(@RequestParam("name") String userName, @RequestParam("id") Integer id){
        System.out.println("username : " + userName);
        System.out.println("id :" + id);
        return "list";
    }
}
