package com.dreams.Dao.Impl;

import com.dreams.Dao.BookDao;
import com.dreams.springframework.stereotype.Repository;

/**
 * @author PoemsAndDreams
 * @date 2023-09-27 21:14
 */
@Repository
public class BookDaoImpl implements BookDao {
    @Override
    public void add() {
        System.out.println("Hello spring add");
    }
}
