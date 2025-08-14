package org.fasf.annotation;

import org.fasf.interceptor.RequestInterceptor;

import java.lang.annotation.*;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
@Documented
public @interface Remoter {
    String endpoint() default "";

    String protocol() default "http";

    Class<? extends RequestInterceptor>[] interceptors() default {};
}
