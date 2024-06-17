package com.dreams.springframework.context;

import com.dreams.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import com.dreams.springframework.beans.factory.config.DefaultListableBeanFactory;
import com.dreams.springframework.beans.factory.xml.XmlBeanDefinitionReader;
import net.sf.cglib.proxy.Enhancer;
import net.sf.cglib.proxy.MethodInterceptor;
import net.sf.cglib.proxy.MethodProxy;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;


/**
 * @author PoemsAndDreams
 * @date 2023-09-25 10:42
 */
public class ClassPathXmlApplicationContext {

    private ConfigurableListableBeanFactory beanFactory = new DefaultListableBeanFactory();

    private String configResources;

    public ConfigurableListableBeanFactory getBeanFactory() {
        return beanFactory;
    }

    public ClassPathXmlApplicationContext(String configResources) {
        this.configResources = configResources;
        //初始化spring容器
        refresh();
    }

    private void refresh() {

        // obtainFreshBeanFactory 加载spring入口
        obtainFreshBeanFactory();


        //实例化Bean
        beanFactory.createBeanInstance();

    }

    private void obtainFreshBeanFactory() {

        //加载xml配置文件
        loadBeanDefinitions(beanFactory);
    }

    private void loadBeanDefinitions(ConfigurableListableBeanFactory beanFactory) {
        XmlBeanDefinitionReader xmlBeanDefinitionReader = new XmlBeanDefinitionReader();
        xmlBeanDefinitionReader.loadBeanDefinitions(beanFactory,configResources);
    }


    public Object getBean(String beanName){
        return beanFactory.getBean(beanName);
    }

    public Object getBean(Class<?> clazz) {
        return beanFactory.getBean(clazz);
    }

}