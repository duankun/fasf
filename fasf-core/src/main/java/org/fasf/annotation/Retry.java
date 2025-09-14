package org.fasf.annotation;

import java.lang.annotation.*;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface Retry {
    int maxAttempts() default 3;

    int delay() default 2;

    int maxBackoff() default 10;
}
