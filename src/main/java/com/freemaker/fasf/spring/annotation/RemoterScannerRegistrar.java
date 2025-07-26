package com.freemaker.fasf.spring.annotation;

import com.freemaker.fasf.annotation.Remoter;
import com.freemaker.fasf.spring.remoter.ClassPathRemoterScanner;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.context.EnvironmentAware;
import org.springframework.context.annotation.ImportBeanDefinitionRegistrar;
import org.springframework.core.annotation.AnnotationAttributes;
import org.springframework.core.env.Environment;
import org.springframework.core.type.AnnotationMetadata;
import org.springframework.lang.NonNull;

public class RemoterScannerRegistrar implements ImportBeanDefinitionRegistrar, EnvironmentAware {
    private Environment environment;
    @Override
    public void registerBeanDefinitions(AnnotationMetadata importingClassMetadata, @NonNull BeanDefinitionRegistry registry) {
        AnnotationAttributes remoterScanAttrs = AnnotationAttributes
                .fromMap(importingClassMetadata.getAnnotationAttributes(RemoterScan.class.getName()));
        ClassPathRemoterScanner scanner = new ClassPathRemoterScanner(registry, Remoter.class);
        assert remoterScanAttrs != null;
        String basePackages = environment.getProperty("fasf.basePackages");
        assert basePackages != null;
        scanner.doScan(basePackages.split(","));
//        scanner.doScan(remoterScanAttrs.getStringArray("basePackages"));
    }

    @Override
    public void setEnvironment(@NonNull Environment environment) {
        this.environment = environment;
    }
}
