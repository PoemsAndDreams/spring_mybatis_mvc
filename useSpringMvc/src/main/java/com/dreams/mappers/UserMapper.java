package com.dreams.mappers;

import com.dreams.pojo.User;
import com.dreams.springframework.stereotype.Repository;

import java.util.List;

/**
 * @author PoemsAndDreams
 * @description
 */
public interface UserMapper {
    List<User> list();

    List<User> select(User user);
    User getOneUser(Integer id);

    int update(User user);

    int deleteById(Integer id);

    int insertUser(User user);

}
