package org.fasf.spring.proxy;

import org.fasf.annotation.Interceptors;
import org.fasf.annotation.Remoter;
import org.fasf.interceptor.RequestInterceptor;
import org.springframework.beans.factory.annotation.AnnotatedBeanDefinition;
import org.springframework.beans.factory.config.BeanDefinitionHolder;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.RootBeanDefinition;
import org.springframework.context.annotation.ClassPathBeanDefinitionScanner;
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

public class ClassPathRemoterScanner extends ClassPathBeanDefinitionScanner {

    public ClassPathRemoterScanner(BeanDefinitionRegistry registry, Class<? extends Annotation> annotationClass) {
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
        Set<Class<? extends RequestInterceptor>> interceptors = new HashSet<>();
        beanDefinitionHolders.forEach(beanDefinitionHolder -> {
            if (registry.containsBeanDefinition(beanDefinitionHolder.getBeanName())) {
                registry.removeBeanDefinition(beanDefinitionHolder.getBeanName());
            }
            Class<?> beanClass = null;
            try {
                beanClass = Class.forName(beanDefinitionHolder.getBeanDefinition().getBeanClassName());
            } catch (ClassNotFoundException e) {
                throw new RuntimeException(e);
            }
            Remoter annotation = beanClass.getAnnotation(Remoter.class);
            Collections.addAll(interceptors, annotation.interceptors());
            Method[] declaredMethods = beanClass.getDeclaredMethods();
            Assert.notEmpty(declaredMethods, "No methods found in remoter interface " + beanClass.getName());
            Arrays.stream(declaredMethods).forEach(method -> {
                Interceptors methodAnnotation = method.getAnnotation(Interceptors.class);
                if (methodAnnotation != null) {
                    Class<? extends RequestInterceptor>[] methodInterceptors = methodAnnotation.interceptors();
                    Collections.addAll(interceptors, methodInterceptors);
                }
            });
            beanDefinitionHolder.getBeanDefinition().setBeanClassName(RemoterFactoryBean.class.getName());
            beanDefinitionHolder.getBeanDefinition().getConstructorArgumentValues().addGenericArgumentValue(beanClass);
            registry.registerBeanDefinition(beanDefinitionHolder.getBeanName(), beanDefinitionHolder.getBeanDefinition());
        });
        if (!CollectionUtils.isEmpty(interceptors)) {
            interceptors.forEach(interceptor -> registry.registerBeanDefinition(interceptor.getName(), new RootBeanDefinition(interceptor)));
        }
    }

    @Override
    public boolean isCandidateComponent(AnnotatedBeanDefinition beanDefinition) {
        AnnotationMetadata metadata = beanDefinition.getMetadata();
        return metadata.isIndependent() && metadata.isInterface();
    }

}
