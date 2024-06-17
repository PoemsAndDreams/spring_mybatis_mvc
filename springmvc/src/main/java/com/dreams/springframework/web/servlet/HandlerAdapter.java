package com.dreams.springframework.web.servlet;

import com.dreams.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.lang.reflect.Constructor;
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
    private HandlerMapping handlerMapping;

    private HttpServletRequest request;

    private HttpServletResponse response;

    public HandlerAdapter(HandlerMapping handlerMapping, HttpServletRequest request, HttpServletResponse response) {
        this.handlerMapping = handlerMapping;
        this.request = request;
        this.response = response;
    }

    public void execute() {
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
                parameterValues.add(parameterObj);
            }
        }

        try {

            method.setAccessible(true);
            Object[] array = parameterValues.toArray(new Object[0]);
            // 反射调用控制器方法
            System.out.println(method);
            Object result = method.invoke(object, parameterValues.toArray(new Object[0]));
            // 处理方法返回值
            // todo 处理方法返回值

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