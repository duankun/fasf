package org.fasf.core.spring.proxy;

import org.fasf.core.annotation.Interceptors;
import org.fasf.core.interceptor.RequestInterceptor;
import org.fasf.core.interceptor.ResponseInterceptor;
import org.springframework.beans.factory.annotation.AnnotatedBeanDefinition;
import org.springframework.beans.factory.config.BeanDefinitionHolder;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.RootBeanDefinition;
import org.springframework.context.annotation.ClassPathBeanDefinitionScanner;
import org.springframework.core.annotation.AnnotatedElementUtils;
import org.springframework.core.type.AnnotationMetadata;
import org.springframework.core.type.filter.AnnotationTypeFilter;
import org.springframework.lang.NonNull;
import org.springframework.util.Assert;
import org.springframework.util.CollectionUtils;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

public class ClassPathApiScanner extends ClassPathBeanDefinitionScanner {

    public ClassPathApiScanner(BeanDefinitionRegistry registry, Class<? extends Annotation> annotationClass) {
        super(registry);
        super.addIncludeFilter(new AnnotationTypeFilter(annotationClass));
    }

    @Override
    public Set<BeanDefinitionHolder> doScan(@NonNull String... basePackages) {
        Set<BeanDefinitionHolder> beanDefinitionHolders = super.doScan(basePackages);
        if (!beanDefinitionHolders.isEmpty()) {
            processBeanDefinition(beanDefinitionHolders);
        }
        return beanDefinitionHolders;
    }

    public void processBeanDefinition(Set<BeanDefinitionHolder> beanDefinitionHolders) {
        BeanDefinitionRegistry registry = super.getRegistry();
        Set<Class<? extends RequestInterceptor>> requestInterceptors = new HashSet<>();
        Set<Class<? extends ResponseInterceptor>> responseInterceptors = new HashSet<>();
        beanDefinitionHolders.forEach(beanDefinitionHolder -> {
            if (registry.containsBeanDefinition(beanDefinitionHolder.getBeanName())) {
                registry.removeBeanDefinition(beanDefinitionHolder.getBeanName());
            }
            Class<?> beanClass;
            try {
                beanClass = Class.forName(beanDefinitionHolder.getBeanDefinition().getBeanClassName());
            } catch (ClassNotFoundException e) {
                throw new RuntimeException(e);
            }
            Interceptors classInterceptors = beanClass.getAnnotation(Interceptors.class);
            if (classInterceptors != null) {
                Collections.addAll(requestInterceptors, classInterceptors.requestInterceptors());
                Collections.addAll(responseInterceptors, classInterceptors.responseInterceptor());
            }
            Method[] declaredMethods = beanClass.getDeclaredMethods();
            Assert.notEmpty(declaredMethods, "No methods found in api interface " + beanClass.getName());
            Arrays.stream(declaredMethods).forEach(method -> {
                Interceptors methodInterceptors = AnnotatedElementUtils.findMergedAnnotation(method, Interceptors.class);
                if (methodInterceptors != null) {
                    Collections.addAll(requestInterceptors, methodInterceptors.requestInterceptors());
                    Collections.addAll(responseInterceptors, methodInterceptors.responseInterceptor());
                }
            });
            beanDefinitionHolder.getBeanDefinition().setBeanClassName(ApiFactoryBean.class.getName());
            beanDefinitionHolder.getBeanDefinition().getConstructorArgumentValues().addGenericArgumentValue(beanClass);
            registry.registerBeanDefinition(beanDefinitionHolder.getBeanName(), beanDefinitionHolder.getBeanDefinition());
        });
        if (!CollectionUtils.isEmpty(requestInterceptors)) {
            requestInterceptors.forEach(interceptor -> registry.registerBeanDefinition(interceptor.getName(), new RootBeanDefinition(interceptor)));
        }
        if (!CollectionUtils.isEmpty(responseInterceptors)) {
            responseInterceptors.forEach(interceptor -> registry.registerBeanDefinition(interceptor.getName(), new RootBeanDefinition(interceptor)));
        }
    }

    @Override
    public boolean isCandidateComponent(AnnotatedBeanDefinition beanDefinition) {
        AnnotationMetadata metadata = beanDefinition.getMetadata();
        return metadata.isIndependent() && metadata.isInterface();
    }

}
