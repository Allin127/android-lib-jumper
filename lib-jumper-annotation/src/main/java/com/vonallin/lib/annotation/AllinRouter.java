package com.vonallin.lib.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

//注解的声明周期声明为source，意即只在编译源文件的过程中有效
@Retention(RetentionPolicy.SOURCE)
//通常一个注解只注释类，方法，或者变量，这样可以使结构更清晰。这里为了演示不同的element，把这个注解的使用对象定义为类，变量，参数，方法
@Target({ElementType.TYPE, ElementType.FIELD,ElementType.PARAMETER,ElementType.METHOD})
public @interface AllinRouter {
    String value();
}
