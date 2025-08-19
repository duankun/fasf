package org.fasf.util;

import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.concurrent.CompletableFuture;

public class ClassUtils {
    public static Class<?> getGenericReturnType(Method method) {
        Type genericReturnType = method.getGenericReturnType();
        if (genericReturnType instanceof ParameterizedType paramType) {
            Type rawType = paramType.getRawType();
            if (rawType instanceof Class<?> rowTypeClass && CompletableFuture.class.isAssignableFrom(rowTypeClass)) {
                Type[] typeArguments = paramType.getActualTypeArguments();
                if (typeArguments.length > 0) {
                    Type actualType = typeArguments[0];
                    if (actualType instanceof Class) {
                        return (Class<?>) actualType;
                    } else if (actualType instanceof ParameterizedType) {
                        return (Class<?>) ((ParameterizedType) actualType).getRawType();
                    }
                }
            }
        }
        return method.getReturnType();
    }
}
