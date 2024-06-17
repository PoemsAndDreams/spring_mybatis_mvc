package com.dreams.springframework.web.context;

import com.dreams.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import com.dreams.springframework.context.ClassPathXmlApplicationContext;
import com.dreams.springframework.stereotype.Controller;
import com.dreams.springframework.web.bind.annotation.RequestMapping;
import com.dreams.springframework.web.servlet.HandlerMapping;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.annotation.WebListener;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author PoemsAndDreams
 * @description //监听器监听web启动
 */
@WebListener
public class ContextLoaderListener implements ServletContextListener {

    @Override
    public void contextInitialized(ServletContextEvent sce) {
        System.out.println("spring容器启动！");
        String contextConfigLocation = sce.getServletContext().getInitParameter("contextConfigLocation");

        if (contextConfigLocation == null || contextConfigLocation.isEmpty()) {
            System.err.println("contextConfigLocation 未配置或为空");
            return;
        }

        if (contextConfigLocation.contains("classpath:")) {
            contextConfigLocation = contextConfigLocation.replace("classpath:","")   ;
        }

        ClassPathXmlApplicationContext context = new ClassPathXmlApplicationContext(contextConfigLocation);
        //处理请求路径
        handle(sce,context);
    }

    @Override
    public void contextDestroyed(ServletContextEvent sce) {
        System.out.println("web服务停止！");
    }


    /**
     * 处理请求路径
     * @param sce
     * @param context
     */
    void handle(ServletContextEvent sce,ClassPathXmlApplicationContext context){

        Map<String, HandlerMapping> pathMethodMap = new HashMap<>();


        ConfigurableListableBeanFactory beanFactory = context.getBeanFactory();
        ConcurrentHashMap<String, Object> instanceMap = beanFactory.getInstanceMap();

        // 创建一个新的 Map，用于存储带有@Controller注解的类实例
        Map<String, Object> controllerMap = new HashMap<>();

        Set<Map.Entry<String, Object>> entries = instanceMap.entrySet();
        for (Map.Entry<String, Object> entry : entries) {
            String beanName = entry.getKey();
            Object value = entry.getValue();
            // 检查值的类是否标记了@Controller注解
            if (value.getClass().isAnnotationPresent(Controller.class)) {
                // 将符合条件的条目存入新的 Map
                controllerMap.put(beanName, value);
            }
        }

        Set<Map.Entry<String, Object>> entrySet = controllerMap.entrySet();

        for (Map.Entry<String, Object> entry : entrySet) {
            Object controller = entry.getValue();

            String classPath =null;
            // 获取类上的@RequestMapping注解
            if (controller.getClass().isAnnotationPresent(RequestMapping.class)) {
                RequestMapping classRequestMapping = controller.getClass().getAnnotation(RequestMapping.class);
                // 处理斜杠
                classPath = classRequestMapping.value();
                if (classPath != null){
                    classPath = classPath.startsWith("/")?classPath:"/"+classPath;
                    classPath = classPath.endsWith("/")?classPath.substring(0, classPath.length() - 1):classPath;
                }
            }
            // 获取方法上的@RequestMapping注解
            Method[] methods = controller.getClass().getDeclaredMethods();
            for (Method method : methods) {
                if (method.isAnnotationPresent(RequestMapping.class)) {
                    RequestMapping methodRequestMapping = method.getAnnotation(RequestMapping.class);
                    String methodPath = methodRequestMapping.value();
                    // 路径开头的斜杠
                    if (classPath == null && methodPath != null){
                        methodPath = methodPath.startsWith("/")?methodPath:"/" + methodPath;
                    }else if (methodPath != null){
                        // 去除路径开头的斜杠
                        methodPath = methodPath.startsWith("/")?methodPath:"/" + methodPath;
                    }
                    String path = classPath + methodPath;
                    path = path.endsWith("/")?path.substring(0, path.length() - 1):path;
                    HandlerMapping handlerMapping = new HandlerMapping(controller, method);
                    pathMethodMap.put(path,handlerMapping);
                }
            }
        }
        //存储到上下文
        sce.getServletContext().setAttribute("pathMethodMap",pathMethodMap);
    }


}
