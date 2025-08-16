package org.fasf.annotation;

import org.fasf.interceptor.RequestInterceptor;
import org.fasf.interceptor.ResponseInterceptor;

import java.lang.annotation.*;

@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE, ElementType.METHOD})
@Documented
public @interface Interceptors {
    Class<? extends RequestInterceptor>[] requestInterceptors() default {};
    Class<? extends ResponseInterceptor> responseInterceptor() default ResponseInterceptor.NoOpResponseInterceptor.class;
}
