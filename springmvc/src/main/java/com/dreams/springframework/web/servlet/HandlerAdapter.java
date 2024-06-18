package com.dreams.springframework.web.servlet;

import com.dreams.springframework.web.bind.annotation.RequestParam;
import com.google.gson.Gson;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.WebContext;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

/**
 * @author PoemsAndDreams
 * @description //封装请求参数，调用目标方法
 */
public class HandlerAdapter {

    private TemplateEngine templateEngine;
    private HandlerMapping handlerMapping;

    private HttpServletRequest request;

    private HttpServletResponse response;


    public HandlerAdapter(TemplateEngine templateEngine, HandlerMapping handlerMapping, HttpServletRequest request, HttpServletResponse response) {
        this.templateEngine = templateEngine;
        this.handlerMapping = handlerMapping;
        this.request = request;
        this.response = response;
    }

    public void execute() {

        // 创建 WebContext,用于Thymeleaf
        ServletContext servletContext = request.getServletContext();
        WebContext webContext = new WebContext(request, response, servletContext, request.getLocale());

        //控制器对象
        Object object = handlerMapping.getObject();
        //控制器方法
        Method method = handlerMapping.getMethod();
        // 获取方法的参数
        Parameter[] parameters = method.getParameters();

        ArrayList<Object> parameterValues = new ArrayList<>();

        for (Parameter parameter : parameters) {
            String name = parameter.getName();
            Class<?> type = parameter.getType();
            // 如果参数上面有@RequestParam注解
            if (parameter.isAnnotationPresent(RequestParam.class)) {
                RequestParam requestParam = parameter.getAnnotation(RequestParam.class);
                String param = requestParam.value();
                if (param != null) {
                    name = param;
                }
            }

            if (type == HttpServletRequest.class) {
                parameterValues.add(request);
            } else if (type == HttpServletResponse.class) {
                parameterValues.add(response);
            } else if (type == HttpSession.class) {
                parameterValues.add(request.getSession());
            } else {
                // 使用HttpServletRequest获取查询参数name和id的值
                String requestParameter = request.getParameter(name);
                // 简单转换字符串参数为对应类型
                Object parameterObj = convert(requestParameter, type);
                webContext.setVariable(name, parameterObj);
                parameterValues.add(parameterObj);
            }
        }

        try {
            method.setAccessible(true);
            Object[] array = parameterValues.toArray(new Object[0]);
            // 反射调用控制器方法
            System.out.println(method);
            Object result = method.invoke(object, parameterValues.toArray(new Object[0]));


            // 返回Thymeleaf视图
            if (result != null && result instanceof String) {
                String viewName = (String) result;
                // 处理视图名称，确保视图名称符合 Thymeleaf 的期望格式
                viewName = viewName.startsWith("/") ? viewName.substring(1) : viewName; // 去掉开头的斜杠
                // 转发到 Thymeleaf 模板页面
                viewName = viewName.startsWith("/") ? viewName.replace("/", "") : viewName;
                // 渲染 Thymeleaf 模板
                response.setContentType("text/html;charset=UTF-8");
                templateEngine.process(viewName, webContext, response.getWriter());
                return;
            }

            // 返回json
            if (result instanceof Object){
                Gson gson=new Gson();
                String json = gson.toJson(result);
                response.setContentType("application/json;charset=utf-8");
                response.getWriter().write(json);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    // 将字符串转换为目标类型
    private Object convert(String value, Class<?> targetType) {
        if (value == null){
            value = "0";
        }
        if (targetType == String.class) {
            return value;
        } else if (targetType == int.class || targetType == Integer.class) {
            return Integer.parseInt(value);
        } else if (targetType == long.class || targetType == Long.class) {
            return Long.parseLong(value);
        } else if (targetType == boolean.class || targetType == Boolean.class) {
            return Boolean.parseBoolean(value);
        } else if (targetType == float.class || targetType == Float.class) {
            return Float.parseFloat(value);
        } else if (targetType == double.class || targetType == Double.class) {
            return Double.parseDouble(value);
        } else if (targetType == Date.class) {
            // 简单日期格式解析，可以根据需要更改
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
            try {
                return dateFormat.parse(value);
            } catch (ParseException e) {
                throw new IllegalArgumentException("Failed to parse date: " + value, e);
            }
        } else {
            // 支持自定义类
            // todo 支持 自定义类型
            throw new UnsupportedOperationException("Unsupported parameter type: " + targetType);
        }
    }


}