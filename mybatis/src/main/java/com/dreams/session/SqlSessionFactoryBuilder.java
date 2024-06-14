package com.dreams.session;

import com.dreams.session.defaults.DefaultSqlSessionFactory;
import com.dreams.xml.XMLConfigBuilder;
import java.io.InputStream;

/**
 * @author PoemsAndDreams
 * @description
 */
public class SqlSessionFactoryBuilder {

    public SqlSessionFactory build(InputStream is) {
        Configuration configuration = null;
        try {
            XMLConfigBuilder parser = new XMLConfigBuilder(is);
            configuration = parser.parse();
        } catch (Exception e) {
            throw new RuntimeException("Error building SqlSession : ", e);
        }

        return this.build(configuration);
    }

    public SqlSessionFactory build(Configuration configuration) {
        return new DefaultSqlSessionFactory(configuration);
    }

}
