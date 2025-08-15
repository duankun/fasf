package org.fasf.annotation;

import org.fasf.http.HttpMethod;

import java.lang.annotation.*;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
@Documented
public @interface Request {
    String path() default "";
    HttpMethod method() default HttpMethod.POST;
    String contentType() default "application/json";
}
