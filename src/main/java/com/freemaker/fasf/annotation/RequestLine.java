package com.freemaker.fasf.annotation;

import com.freemaker.fasf.http.HttpMethod;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface RequestLine {
    String path() default "";
    HttpMethod method() default HttpMethod.POST;
}
