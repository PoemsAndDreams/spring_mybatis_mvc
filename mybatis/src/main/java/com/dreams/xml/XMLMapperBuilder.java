package com.dreams.xml;


import com.dreams.mapping.MappedStatement;
import com.dreams.session.Configuration;
import org.dom4j.*;
import org.dom4j.io.SAXReader;

import java.io.InputStream;
import java.util.List;

/**
 * @author PoemsAndDreams
 * @description 解析mapper.xml
 */
public class XMLMapperBuilder {

    private InputStream resource;

    private Configuration configuration;

    public XMLMapperBuilder(InputStream resource, Configuration configuration) {
        this.resource = resource;
        this.configuration = configuration;
        parse();
    }

    public void parse() {
        //创建一个解析器
        SAXReader saxReader = new SAXReader();
        //读取xml文件
        Document document = null;
        try {
            document = saxReader.read(resource);
        } catch (DocumentException e) {
            throw new RuntimeException(e);
        }

        //获取根节点
        Element rootElement = document.getRootElement();

        // mapper.xml
        Attribute namespace = rootElement.attribute("namespace");

        //获取根节点下的子节点
        List<Element> elements = rootElement.elements();
        for (Element element : elements) {
            //获取id
            String id = element.attributeValue("id");
            //获取返回值resultType
            String resultType = element.attributeValue("resultType");

            String parameterType = element.attributeValue("parameterType");

            //去除空格
            String sql = element.getTextTrim();


            //封装MappedStatement对象
            MappedStatement mappedStatement = new MappedStatement();
            mappedStatement.setId(id);
            mappedStatement.setResultType(resultType);
            mappedStatement.setParameterType(parameterType);
            mappedStatement.setSql(sql);
            String value = namespace.getValue();
            String key = value+ "." + id;

            configuration.getMappedStatements().put(key,mappedStatement);
        }


    }

}
