package org.fasf.core.annotation;

import org.fasf.core.interceptor.RequestInterceptor;
import org.fasf.core.interceptor.ResponseInterceptor;

import java.lang.annotation.*;

@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE, ElementType.METHOD})
@Documented
public @interface Interceptors {
    Class<? extends RequestInterceptor>[] requestInterceptors() default {};
    Class<? extends ResponseInterceptor> responseInterceptor() default ResponseInterceptor.NoOpResponseInterceptor.class;
}
