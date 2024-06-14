package com.dreams;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import com.dreams.io.Resources;
import com.dreams.mappers.UserMapper;
import com.dreams.pojo.User;
import com.dreams.session.SqlSession;
import com.dreams.session.SqlSessionFactory;
import com.dreams.session.SqlSessionFactoryBuilder;

/**
 * @author PoemsAndDreams
 * @description //测试
 */
public class Main {
    public static void main(String[] args) {
        InputStream is = null;
        try {
            is = Resources.getResourceAsStream("mybatis-config.xml");
            SqlSessionFactoryBuilder sqlSessionFactoryBuilder = new SqlSessionFactoryBuilder();
            SqlSessionFactory sqlSessionFactory = sqlSessionFactoryBuilder.build(is);
            System.out.println(sqlSessionFactory);
            SqlSession sqlSession = sqlSessionFactory.openSession();
            UserMapper mapper = sqlSession.getMapper(UserMapper.class);
            List<User> list = mapper.list();
            for (User user : list) {
                System.out.println(user);
            }

            User user = new User();
            user.setId("41");
            user.setUsername("dreams");
            user.setPassword("123456");
            int i = mapper.insertUser(user);
            System.out.println("新增成功！");

            List<User> select = mapper.select(user);
            for (User u : select) {
                System.out.println(u);
            }


            int de = mapper.deleteById(41);
            System.out.println(de);

            User user1 = new User();
            user1.setId("4");
            user1.setUsername("dreams1");
            user1.setPassword("1234561");
            int update = mapper.update(user1);
            System.out.println(update);


        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}