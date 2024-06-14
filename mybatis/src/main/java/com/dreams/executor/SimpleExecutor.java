package com.dreams.executor;

import com.dreams.mapping.MappedStatement;
import com.dreams.session.Configuration;

import java.lang.reflect.Field;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author PoemsAndDreams
 * @description
 */
public class SimpleExecutor implements Executor {

    private Configuration configuration;

    public SimpleExecutor(Configuration configuration) {
        this.configuration = configuration;
    }

    @Override
    public int update(MappedStatement mappedStatement, Object... parameter) throws SQLException {
        //获取数据库连接
        Connection connection = configuration.getDataSource().getConnection();
        String sql = mappedStatement.getSql();
        String parameterType = mappedStatement.getParameterType();
        String boundSql = replacePlaceholders(sql, parameter);
        PreparedStatement prepareStatement = connection.prepareStatement(boundSql);

        int result = prepareStatement.executeUpdate();
        return result;
    }

    @Override
    public <E> List<E> query(MappedStatement mappedStatement, Object... parameter) throws SQLException {
        //获取数据库连接
        Connection connection = configuration.getDataSource().getConnection();
        //获取sql
        String sql = mappedStatement.getSql();

        //替换成执行sql
        String boundSql = replacePlaceholders(sql, parameter);
        //sql执行
        PreparedStatement prepareStatement = connection.prepareStatement(boundSql);
        ResultSet resultSet = prepareStatement.executeQuery();


        //封装结果集
        String resultType = mappedStatement.getResultType();
        Class<?> resultClass = null;
        try {
            resultClass = Class.forName(resultType);
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }

        ArrayList<Object> resultList = new ArrayList<>();
        while (resultSet.next()) {
            try {
                // 创建 resultType 对应类的实例
                Object instance = resultClass.newInstance();

                ResultSetMetaData metaData = resultSet.getMetaData();

                int columnCount = metaData.getColumnCount();

                for (int i = 1; i <= columnCount; i++) {
                    String columnName = metaData.getColumnName(i);
                    Object columnValue = resultSet.getObject(i);

                    // 使用反射设置实例的属性
                    Field field = resultClass.getDeclaredField(columnName); // 假设属性名与列名相同
                    field.setAccessible(true);
                    field.set(instance, columnValue);
                }
                // 将实例添加到 resultList 中
                resultList.add(instance);
            } catch (Exception e) {
                // 处理异常
                e.printStackTrace();
            }
        }

        return (List<E>) resultList;
    }


    private static String replacePlaceholders(String sql, Object... parameters) {
        // 如果参数为空，则返回原始的SQL语句
        if (parameters == null) {
            return sql;
        }

        StringBuffer newSql = new StringBuffer(sql);
        StringBuffer sb = null;

        // 遍历SQL语句中的所有占位符
        // 这里假设占位符的格式为#{0}、#{1}、#{2}等
        for (int i = 0; i < parameters.length; i++) {
            Object parameter = parameters[i];

            // 获取参数的类
            Class<?> parameterClass = parameter.getClass();

            // 遍历SQL语句中的所有占位符
            // 这里假设占位符的格式为#{propertyName}，例如#{id}、#{name}
            Pattern pattern = Pattern.compile("#\\{([^{}]*)\\}");
            Matcher matcher = pattern.matcher(newSql);
            sb = new StringBuffer();
            while (matcher.find()) {
                String placeholder = matcher.group(1);

                // 获取参数对象中与占位符对应的属性值
                try {
                    // 使用反射获取属性值
                    if (parameterClass.isPrimitive() ||parameterClass.getName().startsWith("java.lang")){
                        // 替换占位符为属性值
                        matcher.appendReplacement(sb, Matcher.quoteReplacement(parameter.toString()));
                        continue;
                    }

                    Field field = parameterClass.getDeclaredField(placeholder);
                    field.setAccessible(true);
                    Object value = field.get(parameter);

                    // 如果参数类型为字符串，加上双引号
                    String replacement = value != null ? (field.getType().equals(String.class) ? "'" + value + "'" : value.toString()) : "null";

                    // 替换占位符为属性值
                    matcher.appendReplacement(sb, Matcher.quoteReplacement(replacement));
                } catch (NoSuchFieldException | IllegalAccessException e) {
                    // 如果找不到属性或者无法访问属性，可以选择忽略或者抛出异常
                    // 这里简单地将占位符替换为null
//                    matcher.appendReplacement(sb, "null");
                    if (i == parameters.length - 1) {
                        matcher.appendReplacement(sb, "null");
                    }
                }
            }
            matcher.appendTail(sb);
            newSql = new StringBuffer(sb);

        }

        return newSql.toString();
    }




}
