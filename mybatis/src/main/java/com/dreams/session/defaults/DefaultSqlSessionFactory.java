package com.dreams.session.defaults;

import com.dreams.session.Configuration;
import com.dreams.session.SqlSession;
import com.dreams.session.SqlSessionFactory;

/**
 * @author PoemsAndDreams
 * @date 2024-06-09 02:02
 * @description //TODO
 */
public class DefaultSqlSessionFactory implements SqlSessionFactory {
    private Configuration configuration;

    public DefaultSqlSessionFactory(Configuration configuration) {
        this.configuration = configuration;
    }

    @Override
    public SqlSession openSession(boolean bool) {
        return this.openSession();
    }

    @Override
    public SqlSession openSession() {
        return new DefaultSqlSession(configuration);
    }
}
