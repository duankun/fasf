package com.freemaker.fasf.spring.remoter;

import com.freemaker.fasf.annotation.Remoter;
import com.freemaker.fasf.interceptor.RequestInterceptor;
import org.springframework.beans.factory.annotation.AnnotatedBeanDefinition;
import org.springframework.beans.factory.config.BeanDefinitionHolder;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.RootBeanDefinition;
import org.springframework.context.annotation.ClassPathBeanDefinitionScanner;
import org.springframework.core.type.AnnotationMetadata;
import org.springframework.core.type.filter.AnnotationTypeFilter;
import org.springframework.lang.NonNull;

import java.lang.annotation.Annotation;
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
            beanDefinitionHolder.getBeanDefinition().setBeanClassName(RemoterFactoryBean.class.getName());
            beanDefinitionHolder.getBeanDefinition().getConstructorArgumentValues().addGenericArgumentValue(beanClass);
            registry.registerBeanDefinition(beanDefinitionHolder.getBeanName(), beanDefinitionHolder.getBeanDefinition());
        });
        interceptors.forEach(interceptor -> registry.registerBeanDefinition(interceptor.getName(), new RootBeanDefinition(interceptor)));
    }

    @Override
    public boolean isCandidateComponent(AnnotatedBeanDefinition beanDefinition) {
        AnnotationMetadata metadata = beanDefinition.getMetadata();
        return metadata.isIndependent() && metadata.isInterface();
    }

}
