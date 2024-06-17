package com.dreams.springframework.web.servlet;

import java.lang.reflect.Method;

/**
 * @author PoemsAndDreams
 * @description //请求映射器
 */
public class HandlerMapping {
    private Object object;

    private Method method;


    public HandlerMapping(Object object, Method method) {
        this.object = object;
        this.method = method;
    }

    public Object getObject() {
        return object;
    }

    public Method getMethod() {
        return method;
    }
}
