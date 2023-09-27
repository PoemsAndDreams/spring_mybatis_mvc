package com.yutian.Dao.Impl;

import com.yutian.Dao.BookDao;
import org.springframework.annotation.Repository;

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
