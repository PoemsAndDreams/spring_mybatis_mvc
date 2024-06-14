package com.dreams.springframework.beans.factory.annotation;

import com.dreams.springframework.stereotype.Autowired;
import com.dreams.springframework.beans.factory.config.BeanDefinition;
import com.dreams.springframework.beans.factory.xml.XmlBeanDefinitionReader;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.util.concurrent.ConcurrentHashMap;


/**
 * @author PoemsAndDreams
 * @date 2023-09-25 12:34
 */
public class AutowiredAnnotationBeanPostProcessor {

    private XmlBeanDefinitionReader xmlBeanDefinitionReader;

    //实例化存储
    private ConcurrentHashMap<String, Object> InstanceMap = new ConcurrentHashMap<>();

    public ConcurrentHashMap<String, Object> getInstanceMap() {
        return InstanceMap;
    }

    public void setXmlBeanDefinitionReader(XmlBeanDefinitionReader xmlBeanDefinitionReader) {
        this.xmlBeanDefinitionReader = xmlBeanDefinitionReader;
    }

    //@Autowired注入逻辑
    public void postProcessProperties() {
        ConcurrentHashMap<String, BeanDefinition> beanDefinitionMap = xmlBeanDefinitionReader.getBeanDefinitionMap();

        for (String beanName : beanDefinitionMap.keySet()) {
            BeanDefinition beanDefinition = beanDefinitionMap.get(beanName);
            //获取到实际的class
            Class clazz = beanDefinition.getClazz();
            //获取到所有字段
            Field[] declaredFields = clazz.getDeclaredFields();
            for (Field declaredField : declaredFields) {
                //是否有Autowired注解
                if (declaredField.isAnnotationPresent(Autowired.class)) {
                    Class<?> aClass = declaredField.getType();
                    Object instance = null;
                    try {
                        String name = declaredField.getName();
                        //获取实现该接口的类
                        instance = InstanceMap.get(name);
                        Object o = InstanceMap.get(beanName);
                        declaredField.setAccessible(true);
                        declaredField.set(o, instance);
                    } catch (IllegalAccessException e) {
                        throw new RuntimeException(e);
                    }
                }

            }

        }

    }


    public void createBeanInstance() {
        ConcurrentHashMap<String, BeanDefinition> beanDefinitionMap = xmlBeanDefinitionReader.getBeanDefinitionMap();
        for (String beanName : beanDefinitionMap.keySet()) {
            BeanDefinition beanDefinition = beanDefinitionMap.get(beanName);
            Class clazz = beanDefinition.getClazz();
            try {
                //实例化
                Object instance = clazz.getDeclaredConstructor().newInstance();
                //保存至InstanceMap，方便使用
                InstanceMap.put(beanName, instance);
            } catch (InstantiationException e) {
                throw new RuntimeException(e);
            } catch (IllegalAccessException e) {
                throw new RuntimeException(e);
            } catch (InvocationTargetException e) {
                throw new RuntimeException(e);
            } catch (NoSuchMethodException e) {
                throw new RuntimeException(e);
            }
        }
        //自动注入逻辑
        postProcessProperties();
    }


}
