package com.dreams.session.defaults;

import com.dreams.executor.SimpleExecutor;
import com.dreams.mapping.MappedStatement;
import com.dreams.session.Configuration;
import com.dreams.session.SqlSession;

import java.lang.reflect.*;
import java.sql.SQLException;
import java.util.List;

/**
 * @author PoemsAndDreams
 * @date 2024-06-09 02:08
 * @description
 */
public class DefaultSqlSession implements SqlSession {
    private Configuration configuration;

    public DefaultSqlSession(Configuration configuration) {
        this.configuration = configuration;
    }

    @Override
    public <T> T selectOne(String statement, Object... parameter) {
        List<Object> query = this.selectList(statement, parameter);
        if (query.isEmpty()){
            return null;
        }
        return (T)query.get(0);
    }

    @Override
    public <E> List<E> selectList(String statement, Object... parameter) {
        SimpleExecutor executor = new SimpleExecutor(configuration);
        MappedStatement mappedStatement = this.configuration.getMappedStatements().get(statement);
        try {
            List<Object> query = executor.query(mappedStatement, parameter);
            return (List<E>) query;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public int insert(String statement, Object... parameter) {
        SimpleExecutor executor = new SimpleExecutor(configuration);
        MappedStatement mappedStatement = this.configuration.getMappedStatements().get(statement);
        try {
            int insert = executor.update(mappedStatement, parameter);
            return insert;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
    @Override
    public int update(String statement, Object... parameter) {
        SimpleExecutor executor = new SimpleExecutor(configuration);
        MappedStatement mappedStatement = this.configuration.getMappedStatements().get(statement);
        try {
            int update = executor.update(mappedStatement, parameter);
            return update;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public int delete(String statement, Object... parameter) {
        SimpleExecutor executor = new SimpleExecutor(configuration);
        MappedStatement mappedStatement = this.configuration.getMappedStatements().get(statement);
        try {
            int delete = executor.update(mappedStatement, parameter);
            return delete;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }


    @Override
    public <T> T getMapper(Class<T> mapperClass) {
        Object proxyInstance = Proxy.newProxyInstance(mapperClass.getClassLoader(), new Class<?>[]{mapperClass}, new InvocationHandler() {
            @Override
            public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
                // 获取方法名
                String methodName = method.getName();
                // 获取接口的全类名
                String className = method.getDeclaringClass().getName();
                // 拼接SQL的唯一标识
                String statement = className + "." + methodName;
                // 获取方法被调用之后的返回值类型
                Type returnType = method.getGenericReturnType();

                MappedStatement mappedStatement = configuration.getMappedStatements().get(statement);

                String sql = mappedStatement.getSql();

                // 
                if (sql.contains("select")) {
                    if (returnType instanceof ParameterizedType){
                        return selectList(statement, args);
                    }
                    return selectOne(statement,args);
                } else if (sql.contains("insert")) {
                    return insert(statement, args);
                } else if (sql.contains("update")) {
                    return update(statement, args);
                } else if (sql.contains("delete")) {
                    return delete(statement, args);
                } else {
                    throw new UnsupportedOperationException("方法不存在: " + methodName);
                }
            }
        });
        return (T) proxyInstance;
    }
}
