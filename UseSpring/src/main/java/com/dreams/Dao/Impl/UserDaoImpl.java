package com.dreams.Dao.Impl;

import com.dreams.Dao.UserDao;
import com.dreams.springframework.stereotype.Repository;

/**
 * @author PoemsAndDreams
 * @date 2023-09-27 15:00
 */
@Repository
public class UserDaoImpl implements UserDao {
    @Override
    public void test() {
        System.out.println("Hello spring");
    }
}
