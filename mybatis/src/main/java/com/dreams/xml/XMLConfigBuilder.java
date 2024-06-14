package com.dreams.xml;

import com.alibaba.druid.pool.DruidDataSource;
import com.dreams.io.Resources;
import com.dreams.session.Configuration;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

/**
 * @author PoemsAndDreams
 * @description 解析mybatis-config.xml
 */
public class XMLConfigBuilder {
    private Configuration configuration = new Configuration();

    //接收字节流处理，赋值给configuration对象
    public XMLConfigBuilder(InputStream is) {
        //创建一个解析器
        SAXReader saxReader = new SAXReader();
        //读取xml文件
        Document document = null;
        try {
            document = saxReader.read(is);
        } catch (DocumentException e) {
            throw new RuntimeException(e);
        }

        //获取根节点configuration
        Element rootElement = document.getRootElement();
        Element environments = rootElement.element("environments");
        Element environment = environments.element("environment");
        Element dataSource = environment.element("dataSource");
        List<Element> elements = dataSource.elements();

        DruidDataSource druidDataSource = new DruidDataSource();

        for (Element element : elements) {
            String name = element.attributeValue("name");
            String value = element.attributeValue("value");
            if (name.equals("driver")){
                druidDataSource.setDriverClassName(value);
            }else if (name.equals("url")){
                druidDataSource.setUrl(value);
            }else if (name.equals("username")){
                druidDataSource.setUsername(value);
            }else if (name.equals("password")){
                druidDataSource.setPassword(value);
            }
        }

        configuration.setDataSource(druidDataSource);


        //处理每一个mapper.xml文件
        Element mappers = rootElement.element("mappers");
        List<Element> mapper = mappers.elements("mapper");
        for (Element element : mapper) {
            String value = element.attributeValue("resource");
            try {
                InputStream inputStream = Resources.getResourceAsStream(value);
                XMLMapperBuilder xmlMapperBuilder = new XMLMapperBuilder(inputStream, configuration);

            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }

    public Configuration parse() {
        return configuration;
    }
}
