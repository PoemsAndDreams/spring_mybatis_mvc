package com.dreams.session;


import java.util.List;

/**
 * @author PoemsAndDreams
 * @description SqlSession接口
 */
public interface SqlSession {
    /**
     * 搜索
     * @param statement 唯一标识
     * @param parameter 参数
     * @return
     * @param <T>
     */
    <T> T selectOne(String statement, Object ...parameter);

    <E> List<E> selectList(String statement, Object ...parameter);

    int insert(String statement, Object ...parameter);

    int update(String statement, Object ...parameter);

    int delete(String statement, Object ...parameter);

    <T> T getMapper(Class<T> mapperClass);

}
