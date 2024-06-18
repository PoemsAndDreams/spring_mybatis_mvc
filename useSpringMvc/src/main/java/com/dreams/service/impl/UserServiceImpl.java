package com.dreams.service.impl;


import com.dreams.Dao.Impl.UserDaoImpl;
import com.dreams.mappers.UserMapper;
import com.dreams.pojo.User;
import com.dreams.service.UserService;
import com.dreams.springframework.stereotype.Autowired;
import com.dreams.springframework.stereotype.Service;

/**
 * @author PoemsAndDreams
 * @date 2023-09-27 07:50
 */
@Service
public class UserServiceImpl implements UserService {

//    @Autowired
//    UserMapper userMapper;

    @Override
    public User getOneUser(Integer id) {
        User user = new User();
        user.setUsername("Dreams");
        user.setPassword("123456");
        return user;
    }
}
