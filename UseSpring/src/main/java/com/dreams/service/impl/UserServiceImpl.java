package com.dreams.service.impl;

import com.dreams.Dao.BookDao;
import com.dreams.Dao.UserDao;
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
    UserDao userDao;

    @Autowired
    BookDao bookDao;

    @Override
    public void test() {
        userDao.test();
        bookDao.add();
    }
}
