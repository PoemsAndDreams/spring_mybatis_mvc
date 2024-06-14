package com.dreams.mapping;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author PoemsAndDreams
 * @description 映射信息的封装类
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class MappedStatement {

    //id唯一标识
    private String id;

    //返回类型
    private String resultType;


    //传入类型
    private String parameterType;

    //sql语句
    private String sql;
}
