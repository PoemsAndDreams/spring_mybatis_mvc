package com.yutian.service.impl;

import com.yutian.service.UserService;
import org.springframework.annotation.Service;

/**
 * @author PoemsAndDreams
 * @date 2023-09-27 07:50
 */
@Service
public class UserServiceImpl implements UserService {

    @Override
    public void test() {
        System.out.println("Hello spring!");
    }
}
