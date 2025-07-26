package com.freemaker.fasf.spring.remoter;

import com.freemaker.fasf.annotation.Remoter;
import com.freemaker.fasf.http.HttpClient;
import com.freemaker.fasf.spring.context.RequestContextSupport;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;

import java.lang.reflect.Proxy;

/**
 * @author: duankun
 * @date: 2020/5/27
 */
public class RemoterFactoryBean<T> extends RequestContextSupport implements FactoryBean<T>, InitializingBean {
    private final Class<T> remoterInterface;
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
        super.initRequestContext(remoterInterface.getAnnotation(Remoter.class));
    }
}
