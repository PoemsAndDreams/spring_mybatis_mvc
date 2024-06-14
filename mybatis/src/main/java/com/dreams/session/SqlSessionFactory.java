package com.dreams.session;

/**
 * @author PoemsAndDreams
 * @description SqlSessionFactory工厂类
 */
public interface SqlSessionFactory {
    SqlSession openSession(boolean bool);
    SqlSession openSession();
}
