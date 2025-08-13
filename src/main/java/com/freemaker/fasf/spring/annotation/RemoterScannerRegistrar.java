package com.freemaker.fasf.spring.annotation;

import com.freemaker.fasf.annotation.Remoter;
import com.freemaker.fasf.spring.proxy.ClassPathRemoterScanner;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.context.EnvironmentAware;
import org.springframework.context.annotation.ImportBeanDefinitionRegistrar;
import org.springframework.core.annotation.AnnotationAttributes;
import org.springframework.core.env.Environment;
import org.springframework.core.type.AnnotationMetadata;
import org.springframework.lang.NonNull;
import org.springframework.util.Assert;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class RemoterScannerRegistrar implements ImportBeanDefinitionRegistrar, EnvironmentAware {
    private Environment environment;
    @Override
    public void registerBeanDefinitions(AnnotationMetadata importingClassMetadata, @NonNull BeanDefinitionRegistry registry) {
        AnnotationAttributes remoterScanAttrs = AnnotationAttributes
                .fromMap(importingClassMetadata.getAnnotationAttributes(RemoterScan.class.getName()));
        ClassPathRemoterScanner scanner = new ClassPathRemoterScanner(registry, Remoter.class);
        Assert.notNull(remoterScanAttrs, "@RemoterScan is not present on importing class");
        String[] basePackagesArray = remoterScanAttrs.getStringArray("basePackages");
        List<String> resolvedBasePackages = new ArrayList<>();
        Arrays.stream(basePackagesArray).forEach(basePackage -> resolvedBasePackages.add(environment.resolvePlaceholders(basePackage)));
        List<String> allBasePackages = new ArrayList<>();
        resolvedBasePackages.forEach(basePackage -> allBasePackages.addAll(Arrays.asList(basePackage.split(","))));
        scanner.doScan(allBasePackages.toArray(new String[0]));
    }

    @Override
    public void setEnvironment(@NonNull Environment environment) {
        this.environment = environment;
        System.out.println("=== Remoter Configuration ===");
        System.out.println("fasf.remoter.enable = " + environment.getProperty("fasf.remoter.enable"));
        System.out.println("fasf.remoter.basePackages = " + environment.getProperty("fasf.remoter.basePackages"));
        System.out.println("fasf.remoter.encrypt.aesKey = " + environment.getProperty("fasf.remoter.encrypt.aesKey"));
    }
}
