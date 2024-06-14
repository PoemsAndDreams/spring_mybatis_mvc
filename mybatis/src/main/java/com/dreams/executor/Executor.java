package com.dreams.executor;

import com.dreams.mapping.MappedStatement;

import java.sql.SQLException;
import java.util.List;

/**
 * @author PoemsAndDreams
 * @description
 */
public interface Executor {

    int update(MappedStatement ms, Object ...parameter) throws SQLException;

    <E> List<E> query(MappedStatement ms, Object ...parameter) throws SQLException;

}
