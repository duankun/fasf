package org.fasf.annotation;

import java.lang.annotation.*;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
@Documented
public @interface Api {
    String endpoint() default "";

    String protocol() default "http";
}
