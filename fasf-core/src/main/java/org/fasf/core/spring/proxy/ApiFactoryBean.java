package org.fasf.core.spring.proxy;

import org.fasf.core.http.HttpClient;
import org.fasf.core.interceptor.RequestInterceptor;
import org.fasf.core.interceptor.ResponseInterceptor;
import org.fasf.core.spring.context.ApiContextSupport;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;

import java.lang.reflect.Proxy;
import java.util.List;

public class ApiFactoryBean<T> extends ApiContextSupport implements FactoryBean<T>, InitializingBean {
    private final Class<T> apiInterface;
    @Autowired
    private HttpClient httpClient;
    @Autowired(required = false)
    private List<RequestInterceptor> requestInterceptors;
    @Autowired(required = false)
    private List<ResponseInterceptor> responseInterceptors;

    public ApiFactoryBean(Class<T> apiInterface) {
        this.apiInterface = apiInterface;
    }

    @Override
    public T getObject() throws Exception {
        //noinspection unchecked
        return (T) Proxy.newProxyInstance(apiInterface.getClassLoader(), new Class[]{apiInterface}, new ApiInvocationHandler(apiInterface, super.getApiContext(), httpClient));
    }

    @Override
    public Class<?> getObjectType() {
        return apiInterface;
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        super.createApiContext(apiInterface, requestInterceptors, responseInterceptors);
    }
}
