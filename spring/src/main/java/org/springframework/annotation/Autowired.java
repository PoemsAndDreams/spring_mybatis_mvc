package org.springframework.annotation;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 　　ElementType：
 * 　　　　CONSTRUCTOR:用于描述构造器
 * 　　　　METHOD:用于描述方法
 * 　　　　FIELD:用于描述域
 * 　　　　LOCAL_VARIABLE:用于描述局部变量
 * 　　　　PACKAGE:用于描述包
 * 　　　　PARAMETER:用于描述参数
 * 　　　　TYPE:用于描述类、接口(包括注解类型) 或enum声明
 */
@Target({ElementType.CONSTRUCTOR, ElementType.METHOD, ElementType.PARAMETER, ElementType.FIELD, ElementType.ANNOTATION_TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface Autowired {
    //参数默认值为true
    boolean required() default true;
}