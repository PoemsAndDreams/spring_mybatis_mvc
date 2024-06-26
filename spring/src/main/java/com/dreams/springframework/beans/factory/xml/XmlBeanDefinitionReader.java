package com.dreams.springframework.beans.factory.xml;

import com.dreams.springframework.aspectj.annotation.Aspect;
import com.dreams.springframework.aspectj.annotation.Pointcut;
import com.dreams.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import com.dreams.springframework.stereotype.Component;
import com.dreams.springframework.stereotype.Repository;
import com.dreams.springframework.stereotype.Service;
import org.dom4j.Attribute;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;
import com.dreams.springframework.stereotype.Controller;
import com.dreams.springframework.beans.factory.config.BeanDefinition;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Method;
import java.net.URL;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author PoemsAndDreams
 * @date 2023-09-25 10:15
 * @description xml解析类
 */
public class XmlBeanDefinitionReader {


    //加载xml配置文件
    public void loadBeanDefinitions(ConfigurableListableBeanFactory beanFactory, String configResources){
        //获取扫描包
        String scanPackage = getComponentScanPackage(configResources);
        //获取扫描包路径
        findScanPackagePath(beanFactory,scanPackage);
    }

    private void findScanPackagePath(ConfigurableListableBeanFactory beanFactory, String scanPackage) {
        ClassLoader classLoader = XmlBeanDefinitionReader.class.getClassLoader();
        scanPackage = scanPackage.replace(".","/");
        URL url = classLoader.getResource(scanPackage);
        //因为获取的目录可能含有空格，且会使用%20替代空格，所以我们需要替换回去
        String urlFile = url.getFile();
        if (urlFile.contains("%20")){
            urlFile = urlFile.replace("%20"," ");
        }
        File file = new File(urlFile);
        //加载并实例化
        loadAllClass(beanFactory,file);

    }

    //加载
    private void loadAllClass(ConfigurableListableBeanFactory beanFactory, File path) {
        ConcurrentHashMap<String, BeanDefinition> beanDefinitionMap = beanFactory.getBeanDefinitionMap();
        ConcurrentHashMap<String, Class<?>> advisorsCacheMap = beanFactory.getAdvisorsCacheMap();
        File[] files = path.listFiles();
        for (File file : files) {
            //如果是个目录
            if (!file.isDirectory()){
                //获取文件路劲
                String fileName = file.getAbsolutePath();
                if (fileName.endsWith(".class")){
                    String className = fileName.substring(fileName.indexOf("com"), fileName.indexOf(".class"));
                    className = className.replace("\\",".");
                    //获取类加载器
                    ClassLoader classLoader = XmlBeanDefinitionReader.class.getClassLoader();
                    try {
                        //加载类
                        Class<?> clazz = classLoader.loadClass(className);

                        //判断是否是切面类
                        if (clazz.isAnnotationPresent(Aspect.class) && clazz.isAnnotationPresent(Component.class)){
                            //获取 @Pointcut 注解的值，并使用它作为键，将类存储在 advisorsCacheMap 中。
                            for (Method method : clazz.getDeclaredMethods()) {
                                if (method.isAnnotationPresent(Pointcut.class)) {
                                    Pointcut pointcut = method.getAnnotation(Pointcut.class);
                                    String pointcutExpression = pointcut.value();

                                    if (!advisorsCacheMap.containsKey(pointcutExpression)) {
                                        advisorsCacheMap.put(pointcutExpression, clazz);
                                    }
                                }
                            }
                            continue;
                        }

                        //类是否有Component，Service,Controller,Repository注解
                        if (clazz.isAnnotationPresent(Component.class) || clazz.isAnnotationPresent(Controller.class) || clazz.isAnnotationPresent(Service.class) || clazz.isAnnotationPresent(Repository.class)){

                            Component componentAnnotation = clazz.getDeclaredAnnotation(Component.class);
                            Controller controllerAnnotation = clazz.getDeclaredAnnotation(Controller.class);
                            Service serviceAnnotation = clazz.getDeclaredAnnotation(Service.class);
                            Repository repositoryAnnotation = clazz.getDeclaredAnnotation(Repository.class);

                            String value = "";
                            //Bean定义类
                            BeanDefinition beanDefinition = new BeanDefinition();
                            //判断注解是否有value值
                            if (componentAnnotation != null || controllerAnnotation != null || serviceAnnotation != null || repositoryAnnotation != null){
                                if (componentAnnotation != null && !componentAnnotation.value() .equals("")){
                                    value = componentAnnotation.value();
                                }else if (controllerAnnotation != null && !controllerAnnotation.value().equals("")){
                                    value = controllerAnnotation.value();
                                }else if (serviceAnnotation != null && !serviceAnnotation.value().equals("")){
                                    value = serviceAnnotation.value();
                                } else if (repositoryAnnotation != null && !repositoryAnnotation.value().equals("")) {
                                    value = repositoryAnnotation.value();
                                } else {
                                    String name = clazz.getSimpleName();
                                    //默认以开头小写的类名作为实例名
                                    value = name.valueOf(name.charAt(0)).toLowerCase() + name.substring(1);

                                }
                                //不能重名
                                if (beanDefinitionMap.get(value) != null) {
                                    throw new RuntimeException("spring IOC Container is already exists " + beanDefinitionMap.get(value));
                                }

                                beanDefinition.setClazz(clazz);
                                beanDefinition.setBeanName(value);
                                //保存到bean定义
                                beanDefinitionMap.put(value,beanDefinition);

                                //获取到该类实现的所有接口
                                Class[] interfaces = clazz.getInterfaces();
                                //在beanDefinitionMap中存储为一个接口对应一个实现类
                                for (Class anInterface : interfaces) {
                                    String interfaceSimpleName = anInterface.getSimpleName();
                                    interfaceSimpleName = interfaceSimpleName.valueOf(interfaceSimpleName.charAt(0)).toLowerCase() + interfaceSimpleName.substring(1);
                                    beanDefinitionMap.put(interfaceSimpleName,beanDefinition);
                                }
                            }

                        }

                    } catch (ClassNotFoundException e) {
                        throw new RuntimeException(e);
                    }

                }
            }
            else {
                loadAllClass(beanFactory, file);
            }
        }
    }


    //获取扫描包
    private String getComponentScanPackage(String configResources) {
        //DOM4J 解析 XML
        InputStream resourceAsStream = null;

        try {
            //创建 SAXReader 对象
            SAXReader saxReader = new SAXReader();
            //获取class对象加载文件返回流
            resourceAsStream = XmlBeanDefinitionReader.class.getClassLoader().getResourceAsStream(configResources);
            //获取document对象
            Document document = saxReader.read(resourceAsStream);
            //获取根节点
            Element rootElement = document.getRootElement();
            //获取扫描包
            Element element = rootElement.element("component-scan");
            Attribute attribute = element.attribute("base-package");
            return attribute.getValue();
        } catch (DocumentException e) {
            throw new RuntimeException(e);
        }finally {
            if (resourceAsStream != null){
                try {
                    resourceAsStream.close();
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
        }

    }
}
