package com.dreams.springframework.beans.factory.config;

import com.dreams.springframework.aspectj.annotation.After;
import com.dreams.springframework.aspectj.annotation.Before;
import com.dreams.springframework.context.ClassPathXmlApplicationContext;
import com.dreams.springframework.stereotype.Autowired;
import net.sf.cglib.proxy.Enhancer;
import net.sf.cglib.proxy.MethodInterceptor;
import net.sf.cglib.proxy.MethodProxy;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author PoemsAndDreams
 * @date 2024-06-14 17:35
 * @description
 */
public class DefaultListableBeanFactory implements ConfigurableListableBeanFactory {

    //存储bean定义信息，所以扫描到的
    ConcurrentHashMap<String,BeanDefinition> beanDefinitionMap = new ConcurrentHashMap<>();

    //实例化存储
    ConcurrentHashMap<String, Object> InstanceMap = new ConcurrentHashMap<>();

    // 存储切面数据
    ConcurrentHashMap<String, Class<?>> advisorsCacheMap = new ConcurrentHashMap<>();


    @Override
    public ConcurrentHashMap<String, Class<?>> getAdvisorsCacheMap() {
        return advisorsCacheMap;
    }

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

        this.resolveInstantiationAspect();

        //自动注入逻辑
        this.postProcessProperties();
    }



    //处理切面
    private void resolveInstantiationAspect() {

        for (Map.Entry<String, Class<?>> entry : advisorsCacheMap.entrySet()) {
            String packagePattern = entry.getKey();
            Class<?> aspectClass = entry.getValue();

            for (Map.Entry<String, Object> beanEntry : InstanceMap.entrySet()) {
                Object bean = beanEntry.getValue();
                String beanName = beanEntry.getKey();

                if (bean.getClass().getPackage().getName().matches(packagePattern.replace("*", ".*"))) {
                    System.out.println("Applying aspect to bean: " + beanName);
                    // 创建 Enhancer 对象，用于生成代理类
                    Enhancer enhancer = new Enhancer();
                    // 设置目标类的父类，即被代理的类
                    enhancer.setSuperclass(bean.getClass());
                    // 设置回调，即拦截器
                    enhancer.setCallback(new CustomInterceptor(bean, aspectClass));
                    // 生成代理对象
                    Object proxy = enhancer.create();
                    InstanceMap.put(beanName, proxy);
                }
            }
        }
    }

    // MethodInterceptor 实现类，用于处理方法调用
    class CustomInterceptor implements MethodInterceptor {
        private final Object target;
        private final  Class<?> aspectClass;

        public CustomInterceptor(Object target,  Class<?> aspectClass) {
            this.target = target;
            this.aspectClass = aspectClass;
        }

        @Override
        public Object intercept(Object obj, Method method, Object[] args, MethodProxy proxy) throws Throwable {
            Method beforeMethod = null;
            Method afterMethod = null;
            Method[] methods = aspectClass.getMethods();

            for (Method m : methods) {
                if (m.isAnnotationPresent(Before.class)) {
                    beforeMethod = m;
                } else if (m.isAnnotationPresent(After.class)) {
                    afterMethod = m;
                }
            }

            Object aspectInstance = null;
            try {
                aspectInstance = aspectClass.getDeclaredConstructor().newInstance();
            } catch (Exception e) {
                throw new RuntimeException(e);
            }

            beforeMethod.invoke(aspectInstance);
            // 调用目标对象的方法
            Object result = proxy.invokeSuper(obj, args);
            afterMethod.invoke(aspectInstance);
            return result;
        }
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
