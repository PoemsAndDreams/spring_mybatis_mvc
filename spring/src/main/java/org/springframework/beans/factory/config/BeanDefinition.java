package org.springframework.beans.factory.config;


/**
 * @author PoemsAndDreams
 * @date 2023-09-26 11:14
 * Bean的定义类
 */
public class BeanDefinition {

    private String BeanName;
    private Class clazz;

    //其他信息，如单例等，这里实现一个简单的spring，就不实现了


    //Getter and Setter
    public Class getClazz() {
        return clazz;
    }

    public void setClazz(Class clazz) {
        this.clazz = clazz;
    }

    public String getBeanName() {
        return BeanName;
    }

    public void setBeanName(String beanName) {
        BeanName = beanName;
    }
}
