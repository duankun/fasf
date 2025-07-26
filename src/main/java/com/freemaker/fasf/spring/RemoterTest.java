package com.freemaker.fasf.spring;

import com.freemaker.fasf.spring.remoter.Animal;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;

@Component
public class RemoterTest implements ApplicationContextAware {
    @Autowired
    private Animal animal;

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        String result = animal.remote("1234");
        System.out.println(result);
        String getResult = animal.get("duankun");
    }
}
