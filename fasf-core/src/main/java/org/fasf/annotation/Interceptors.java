package org.fasf.annotation;

import org.fasf.interceptor.*;
import org.springframework.core.annotation.AliasFor;

import java.lang.annotation.*;

@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE, ElementType.METHOD})
@Documented
public @interface Interceptors {
    @AliasFor("interceptors")
    Class<? extends RequestInterceptor>[] value() default {};
    @AliasFor("value")
    Class<? extends RequestInterceptor>[] interceptors() default {};
}
