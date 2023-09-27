package org.springframework.context.support;

import org.springframework.beans.factory.annotation.AutowiredAnnotationBeanPostProcessor;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.xml.XmlBeanDefinitionReader;

import java.util.concurrent.ConcurrentHashMap;

/**
 * @author PoemsAndDreams
 * @date 2023-09-25 10:42
 */
public class ClassPathXmlApplicationContext {

    private String configResources;

    private XmlBeanDefinitionReader xmlBeanDefinitionReader = new XmlBeanDefinitionReader();

    //@Autowired注入逻辑
    private AutowiredAnnotationBeanPostProcessor autowiredAnnotationBeanPostProcessor = new AutowiredAnnotationBeanPostProcessor();


    public ClassPathXmlApplicationContext(String configResources) {
        this.configResources = configResources;
        //初始化spring容器
        refresh();
    }

    private void refresh() {

        // obtainFreshBeanFactory 加载spring入口
        obtainFreshBeanFactory();

        //实例化Bean
        createBean();
    }

    private void createBean() {

        autowiredAnnotationBeanPostProcessor.setXmlBeanDefinitionReader(xmlBeanDefinitionReader);
        //具体逻辑
        autowiredAnnotationBeanPostProcessor.createBeanInstance();
    }

    private void obtainFreshBeanFactory() {
        //加载xml配置文件加载Bean定义
        loadBeanDefinitions();
    }

    private void loadBeanDefinitions() {
        xmlBeanDefinitionReader.loadBeanDefinitions(configResources);
    }

    public Object getBean(String beanName){
        //获取
        ConcurrentHashMap<String, Object> instanceMap = autowiredAnnotationBeanPostProcessor.getInstanceMap();
        Object o = instanceMap.get(beanName);
        //获取不到即抛出异常
        if (o == null){
            throw new RuntimeException("No bean named" + beanName +" available");
        }
        return o;
    }


    public Object getBean(Class<?> clazz) {
        ConcurrentHashMap<String, Object> instanceMap = autowiredAnnotationBeanPostProcessor.getInstanceMap();
        ConcurrentHashMap<String, BeanDefinition> beanDefinitionMap = xmlBeanDefinitionReader.getBeanDefinitionMap();
        Object bean = new Object();
        for (BeanDefinition value : beanDefinitionMap.values()) {
            Class aClass = value.getClazz();
            if (aClass == clazz){
                String beanName = value.getBeanName();
                bean = instanceMap.get(beanName);
                return bean;
            }
        }
        //获取不到即抛出异常
        throw new RuntimeException("No qualifying bean of type" + clazz + " available");
    }
}
