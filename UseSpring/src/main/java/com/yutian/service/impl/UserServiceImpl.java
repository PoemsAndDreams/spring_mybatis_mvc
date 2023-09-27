package com.yutian.service.impl;

import com.yutian.Dao.BookDao;
import com.yutian.Dao.Impl.UserDaoImpl;
import com.yutian.Dao.UserDao;
import com.yutian.service.UserService;
import org.springframework.annotation.Autowired;
import org.springframework.annotation.Service;

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
