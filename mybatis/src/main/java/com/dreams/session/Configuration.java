package com.dreams.session;

import com.dreams.mapping.MappedStatement;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.sql.DataSource;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author PoemsAndDreams
 * @description SQL配置封装对象
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Configuration {

    //读取mybatis-config.xml，存储数据源信息
    private DataSource dataSource;

    //Mapper.xml 中的一个 SQL 语句对应一个 MappedStatement，这里使用Map存储
    Map<String, MappedStatement> mappedStatements = new ConcurrentHashMap<>();
}
