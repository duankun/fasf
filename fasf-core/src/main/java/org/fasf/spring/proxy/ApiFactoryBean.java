package org.fasf.spring.proxy;

import org.fasf.http.HttpClient;
import org.fasf.spring.context.ApiContextSupport;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;

import java.lang.reflect.Proxy;

public class ApiFactoryBean<T> extends ApiContextSupport implements FactoryBean<T>, InitializingBean {
    private final Class<T> apiInterface;
    /**
     * if there is no {@link HttpClient} configured, a default one {@link HttpClient.DefaultHttpClient} will be used
     */
    @Autowired(required = false)
    private HttpClient httpClient;

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
        super.initApiContext(apiInterface);
    }
}
