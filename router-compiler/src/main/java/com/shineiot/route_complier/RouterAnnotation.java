package com.shineiot.route_complier;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @Description 注解类
 * @Author : GF63
 * @Date : 2023/2/17
 */
@Retention(RetentionPolicy.CLASS)
@Target({ElementType.TYPE,ElementType.FIELD})
public @interface RouterAnnotation {
    String path();
}
