package org.fasf.annotation;

import java.lang.annotation.*;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
@Documented
public @interface Remoter {
    String endpoint() default "";

    String protocol() default "http";
}
