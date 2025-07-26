package com.freemaker.fasf.interceptor;

/**
 * @author duankun
 * @date 2025/7/26
 */
public interface ResponseInterceptor {
    String intercept(String responseBody);
}
