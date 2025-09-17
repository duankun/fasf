package org.fasf.core.annotation;

import org.fasf.core.http.HttpMethod;

import java.lang.annotation.*;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
@Documented
public @interface Request {
    String path() default "";
    HttpMethod method() default HttpMethod.POST;
    String contentType() default "application/json;charset=utf-8";
}
