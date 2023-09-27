package com.yutian.Dao.Impl;

import com.yutian.Dao.UserDao;
import org.springframework.annotation.Repository;

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
