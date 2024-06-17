package com.dreams.aspect;

import com.dreams.springframework.aspectj.annotation.After;
import com.dreams.springframework.aspectj.annotation.Aspect;
import com.dreams.springframework.aspectj.annotation.Before;
import com.dreams.springframework.aspectj.annotation.Pointcut;
import com.dreams.springframework.stereotype.Component;

/**
 * @author PoemsAndDreams
 * @description //日志
 */
@Aspect
@Component
public class MyAspect {

    @Pointcut("com.dreams.service.impl")
    public void pointCut(){}

    @Before("pointCut()")
    public void before(){
        System.out.println("方法执行前！");
    }

    @After("pointCut()")
    public void after(){
        System.out.println("方法执行后！");
    }

}
