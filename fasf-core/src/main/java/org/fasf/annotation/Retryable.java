package org.fasf.annotation;

import java.lang.annotation.*;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface Retryable {
    int maxAttempts() default 3;
    // ms
    int delay() default 1000;
}
