package org.fasf.spring.proxy;

import org.fasf.http.HttpClient;
import org.fasf.spring.context.RemoterContextSupport;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;

import java.lang.reflect.Proxy;

public class RemoterFactoryBean<T> extends RemoterContextSupport implements FactoryBean<T>, InitializingBean {
    private final Class<T> remoterInterface;
    /**
     * if there is no {@link HttpClient} configured, a default one {@link HttpClient.DefaultHttpClient} will be used
     */
    @Autowired(required = false)
    private HttpClient httpClient;

    public RemoterFactoryBean(Class<T> remoterInterface) {
        this.remoterInterface = remoterInterface;
    }

    @Override
    public T getObject() throws Exception {
        return (T) Proxy.newProxyInstance(remoterInterface.getClassLoader(), new Class[]{remoterInterface}, new RemoterInvocationHandler(remoterInterface, super.getRemoterContext(), httpClient));
    }

    @Override
    public Class<?> getObjectType() {
        return remoterInterface;
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        super.initRequestContext(remoterInterface);
    }
}
