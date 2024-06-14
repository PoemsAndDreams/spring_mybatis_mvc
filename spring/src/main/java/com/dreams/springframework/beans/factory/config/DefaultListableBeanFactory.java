package com.dreams.springframework.beans.factory.config;

import com.dreams.springframework.stereotype.Autowired;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author PoemsAndDreams
 * @date 2024-06-14 17:35
 * @description //TODO
 */
public class DefaultListableBeanFactory implements ConfigurableListableBeanFactory {

    //存储bean定义信息，所以扫描到的
    ConcurrentHashMap<String,BeanDefinition> beanDefinitionMap = new ConcurrentHashMap<>();

    //实例化存储
    ConcurrentHashMap<String, Object> InstanceMap = new ConcurrentHashMap<>();

    @Override
    public ConcurrentHashMap<String, BeanDefinition> getBeanDefinitionMap() {
        return beanDefinitionMap;
    }

    @Override
    public ConcurrentHashMap<String, Object> getInstanceMap() {
        return InstanceMap;
    }

    @Override
    public void createBeanInstance() {
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
        this.postProcessProperties();
    }


    @Override
    public Object getBean(String beanName){
        Object o = InstanceMap.get(beanName);
        //获取不到即抛出异常
        if (o == null){
            throw new RuntimeException("No bean named" + beanName +" available");
        }
        return o;
    }

    @Override
    public Object getBean(Class<?> clazz) {
        Object bean = new Object();
        for (BeanDefinition value : beanDefinitionMap.values()) {
            Class aClass = value.getClazz();
            if (aClass == clazz){
                String beanName = value.getBeanName();
                bean = InstanceMap.get(beanName);
                return bean;
            }
        }
        //获取不到即抛出异常
        throw new RuntimeException("No qualifying bean of type" + clazz + " available");
    }


    //@Autowired注入逻辑
    public void postProcessProperties() {

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

}
