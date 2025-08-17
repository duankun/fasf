package org.fasf.spring.annotation;

import org.fasf.annotation.Api;
import org.fasf.spring.proxy.ClassPathApiScanner;
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

public class ApiScannerRegistrar implements ImportBeanDefinitionRegistrar, EnvironmentAware {
    private Environment environment;

    @Override
    public void registerBeanDefinitions(AnnotationMetadata importingClassMetadata, @NonNull BeanDefinitionRegistry registry) {
        AnnotationAttributes apiScanAttrs = AnnotationAttributes
                .fromMap(importingClassMetadata.getAnnotationAttributes(ApiScan.class.getName()));
        ClassPathApiScanner scanner = new ClassPathApiScanner(registry, Api.class);
        Assert.notNull(apiScanAttrs, "@ApiScan is not present on importing class");
        String[] basePackagesArray = apiScanAttrs.getStringArray("basePackages");
        List<String> resolvedBasePackages = new ArrayList<>();
        Arrays.stream(basePackagesArray).forEach(basePackage -> resolvedBasePackages.add(environment.resolvePlaceholders(basePackage)));
        List<String> allBasePackages = new ArrayList<>();
        resolvedBasePackages.forEach(basePackage -> allBasePackages.addAll(Arrays.asList(basePackage.split(","))));
        scanner.doScan(allBasePackages.toArray(new String[0]));
    }

    @Override
    public void setEnvironment(@NonNull Environment environment) {
        this.environment = environment;
    }
}
