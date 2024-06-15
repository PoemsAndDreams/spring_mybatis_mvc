package com.dreams.service.impl;


import com.dreams.Dao.Impl.UserDaoImpl;
import com.dreams.service.UserService;
import com.dreams.springframework.stereotype.Autowired;
import com.dreams.springframework.stereotype.Service;

/**
 * @author PoemsAndDreams
 * @date 2023-09-27 07:50
 */
@Service
public class UserServiceImpl implements UserService {

    @Autowired
    UserDaoImpl userDao;


    @Override
    public void test() {
        System.out.println("-----UserServiceImpl------");
        userDao.test();
    }
}
