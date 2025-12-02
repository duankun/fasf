package org.fasf.core.spring.proxy;

import org.springframework.beans.factory.annotation.AnnotatedBeanDefinition;
import org.springframework.beans.factory.config.BeanDefinitionHolder;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.context.annotation.ClassPathBeanDefinitionScanner;
import org.springframework.core.type.AnnotationMetadata;
import org.springframework.core.type.filter.AnnotationTypeFilter;
import org.springframework.lang.NonNull;
import org.springframework.util.Assert;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
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
            Method[] declaredMethods = beanClass.getDeclaredMethods();
            Assert.notEmpty(declaredMethods, "No methods found in api interface " + beanClass.getName());
            beanDefinitionHolder.getBeanDefinition().setBeanClassName(ApiFactoryBean.class.getName());
            beanDefinitionHolder.getBeanDefinition().getConstructorArgumentValues().addGenericArgumentValue(beanClass);
            registry.registerBeanDefinition(beanDefinitionHolder.getBeanName(), beanDefinitionHolder.getBeanDefinition());
        });
    }

    @Override
    public boolean isCandidateComponent(AnnotatedBeanDefinition beanDefinition) {
        AnnotationMetadata metadata = beanDefinition.getMetadata();
        return metadata.isIndependent() && metadata.isInterface();
    }

}
