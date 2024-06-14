package com.dreams.springframework.beans.factory.config;

import com.dreams.springframework.beans.factory.xml.XmlBeanDefinitionReader;

import java.util.concurrent.ConcurrentHashMap;

/**
 * @author PoemsAndDreams
 * @date 2024-06-14 17:34
 * @description //TODO
 */
public interface ConfigurableListableBeanFactory {

    ConcurrentHashMap<String, Object> getInstanceMap();

    ConcurrentHashMap<String, BeanDefinition> getBeanDefinitionMap();

    Object getBean(String beanName);

    Object getBean(Class<?> clazz);

    void createBeanInstance() ;
}
